package net.gesekus.housekeeping.services.bookservice
import net.gesekus.housekeeping.services.book.{ BookCommand, BookEvents, BookStore }
import zio.{ Task, ZIO }
import EventStore.Service
import net.gesekus.housekeeping.services.book.BookStore.addEntry
import scalaz.{ -\/, \/, \/- }

trait LiveBookActor[R] extends BookActor {
  val eventStore: EventStore.Service[R]
  val eventPublisher: EventPublisher.Service[R]

  val bookActor: BookActor.Service[Any] = new BookActor.Service[Any] {
    override def restore: Task[BookServiceState] =
      for {
        state <- eventStore.getSnapShot()
        events <- eventStore.getEventsSinceLastSnapShot()
        state <- {
          val (newState, errorEt) = BookStore.handleEvents(events).run.run(state)
          errorEt match {
            case \/-(_) => ZIO.succeed(newState)
            case -\/(e) => ZIO.fail(e)
          }
        }
      } yield state

    override def handleCommand(bookCommand: BookCommand, state: BookServiceState): Task[Seq[BookEvents]] =
      for {
        genEvents <- {
          val eventsEt = BookStore.handleCommand(bookCommand).run.eval(state)
          eventsEt match {
            case \/-(events) => ZIO.succeed(events)
            case -\/(e)      => ZIO.fail(e)
          }
        }
      } yield genEvents

    override def applyEvents(events: Seq[BookEvents], state: BookServiceState): Task[BookServiceState] =
      for {
        state <- {
          val (newState, errorEt) = BookStore.handleEvents(events).run.run(state)
          errorEt match {
            case \/-(_) => ZIO.succeed(newState)
            case -\/(e) => ZIO.fail(e)
          }
        }
      } yield state

    override def publishAndStoreEvents(events: Seq[BookEvents]): Task[Int] =
      for {
        _ <- eventPublisher.publishEvents(events)
        index <- eventStore.storeEvents(events)
      } yield index
  }

}
