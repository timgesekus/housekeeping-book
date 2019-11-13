package net.gesekus.housekeeping.services.bookservice

import net.gesekus.housekeeping.log.Log
import net.gesekus.housekeeping.services.book.{ BookCommand, BookEvents }
import zio.console.{ Console, putStrLn }
import zio.{ DefaultRuntime, IO, Promise, Queue, Ref, Task, UIO, ZIO }
import cats.implicits._
import ch.qos.logback.classic.util.DefaultNestedComponentRules
import net.gesekus.housekeeping.log
import zio.internal.{ Platform, PlatformLive }

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

      def process(message: PendingMessage, state: Ref[BookServiceState]): ZIO[Any, Throwable, Unit] =
        for {
          _ <- log.info(s"Start processing Message $message")
          s <- state.get
          _ <- log.info(s"State : ${s}")
          (command, promise) = message
          events <- bookActor.handleCommand(command, s)
          _ <- log.info(s"Handeled command")
          newState <- bookActor.applyEvents(events, s)
          _ <- log.info(s"Applied events")
          _ <- bookActor.publishAndStoreEvents(events)
          _ <- log.info(s"Published and stored events")
          _ <- state.set(newState)
          _ <- promise.succeed(true)
        } yield ()

      def runForever (state: Ref[BookServiceState]): ZIO[Any, Throwable, Unit] = for {
        _ <- log.info("Wait for new message")
        pendingMessage <- commandQueue.take
        _ <- log.info("New message")
        _ <- process(pendingMessage, state)
      } yield ()

      override def run(): ZIO[Any,Throwable, Unit] =
        for {
          _ <- log.info("BookActorQueue startup")
          initialState <- bookActor.restore
          state<- Ref.make(initialState)
          _ <- runForever(state).forever.fork
        } yield ()
    }
  }

}
