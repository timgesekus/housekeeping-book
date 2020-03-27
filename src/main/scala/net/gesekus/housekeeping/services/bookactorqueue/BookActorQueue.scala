package net.gesekus.housekeeping.services.bookactorqueue

import cats.implicits._
import net.gesekus.housekeeping.log.Log
import net.gesekus.housekeeping.services.bookrepository.BookCommand
import net.gesekus.housekeeping.services.bookrepository.{BookActor, BookServiceState}
import zio._

trait BookActorQueue {
  val bookActorQueue: BookActorQueue.Service[Any]
}

object BookActorQueue {

  trait Service[R] {
    def put(bookCommand: BookCommand): ZIO[R, Throwable, Boolean]
    def run(): ZIO[R, Throwable, Unit]
  }

  object Live {
    type PendingMessage = (BookCommand, Promise[Throwable, Boolean])
  }

  trait Live extends BookActor with Log {
    import Live.PendingMessage
    val log: Log.Service[Any]
    val bookActor: BookActor.Service[Any]
    val commandQueue: Queue[PendingMessage]

    val bookActorQueue: Service[Any] = new Service[Any] {

      override def put(bookCommand: BookCommand): Task[Boolean] =
        for {
          _ <- log.info("New message")
          promise <- Promise.make[Throwable, Boolean]
          _ <- commandQueue.offer((bookCommand, promise))
          value <- promise.await
        } yield value

      def stuff(command: BookCommand, state: Ref[BookServiceState]): ZIO[Any, Throwable, Unit] =
        for {
          s <- state.get
          _ <- log.info(s"State : ${s}")
          events <- bookActor.handleCommand(command, s).foldM (
            e => ZIO.fail(e),
            events => ZIO.succeed(events)
          )
          _ <- log.info(s"Handeled command")
          newState <- bookActor.applyEvents(events, s)
          _ <- log.info(s"Applied events")
          _ <- bookActor.publishAndStoreEvents(events)
          _ <- log.info(s"Published and stored events")
          _ <- state.set(newState)
        } yield ()

      def process(message: PendingMessage, state: Ref[BookServiceState]): IO[Nothing, Unit] =
        for {
          _ <- log.info(s"Start processing Message $message")
          (command, promise) = message
          _ <- stuff (command, state).foldM (
            e => promise.fail(e),
            _ => promise.succeed(true)
          )
        } yield ()

      def runForever(state: Ref[BookServiceState]): ZIO[Any, Throwable, Unit] =
        for {
          _ <- log.info("Wait for new message")
          pendingMessage <- commandQueue.take
          _ <- log.info("New message")
          _ <- process(pendingMessage, state)
        } yield ()

      override def run(): ZIO[Any, Throwable, Unit] =
        for {
          _ <- log.info("BookActorQueue startup")
          initialState <- bookActor.restore
          state <- Ref.make(initialState)
          _ <- runForever(state).forever.fork
        } yield ()
    }
  }

}
