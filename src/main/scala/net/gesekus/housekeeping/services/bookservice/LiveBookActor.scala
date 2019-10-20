package net.gesekus.housekeeping.services.bookservice
import net.gesekus.housekeeping.services.book.{ BookCommand, BookEvents, BookStore }
import zio.{ Task, ZIO }
import EventStore.Service
import net.gesekus.housekeeping.services.book.BookStore.addEntry
import scalaz.{ -\/, \/, \/- }

abstract class LiveBookActor extends BookActor.Service {
  val eventStore: EventStore.Service

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

  override def handleCommand(bookCommand: BookCommand): Task[Seq[BookEvents]] = ???

  override def applyEvents(events: Seq[BookEvents]): Task[BookServiceState] = ???

  override def publishEvents(events: Seq[BookEvents]): Task[Unit] = ???
}
