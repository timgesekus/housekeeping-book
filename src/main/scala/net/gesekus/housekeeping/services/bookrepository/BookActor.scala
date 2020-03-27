package net.gesekus.housekeeping.services.bookrepository

import net.gesekus.housekeeping.algebra.book.Book
import net.gesekus.housekeeping.services.eventpublisher.EventPublisher
import net.gesekus.housekeeping.services.eventstore.EventStore
import scalaz.{-\/, \/-}
import zio.{Task, ZIO}
import scalaz.{ State, _ }

trait BookActor {
  val bookActor: BookActor.Service[Any]
}

object BookActor {

  trait Service[R] {
    def restore: Task[BookRepositoryState]
    def handleCommand(bookCommand: BookCommand, state: BookRepositoryState): Task[Seq[BookEvents]]
    def applyEvents(events: Seq[BookEvents], state: BookRepositoryState): Task[BookRepositoryState]
    def publishAndStoreEvents(events: Seq[BookEvents]): Task[Int]
  }
  trait Live extends BookActor  {
    val eventStore: EventStore.Service[Any]
    val eventPublisher: EventPublisher.Service[Any]
    val bookActor: BookActor.Service[Any] = new BookActor.Service[Any] {
      override def restore: Task[BookRepositoryState] =
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

      override def handleCommand(bookCommand: BookCommand, state: BookRepositoryState): Task[Seq[BookEvents]] =
        for {
          genEvents <- {
            val eventsEt = BookStore.handleCommand(bookCommand).run.eval(state)
            eventsEt match {
              case \/-(events) => ZIO.succeed(events)
              case -\/(e)      => ZIO.fail(e)
            }
          }
        } yield genEvents

      override def applyEvents(events: Seq[BookEvents], state: BookRepositoryState): Task[BookRepositoryState] =
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
}
