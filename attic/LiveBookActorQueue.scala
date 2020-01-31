package net.gesekus.housekeeping.services.bookservice
import net.gesekus.housekeeping.services.book.BookCommand
import net.gesekus.housekeeping.services.bookactorqueue.BookActorQueue
import net.gesekus.housekeeping.services.bookservice.BookServiceState
import zio.{IO, Promise, Queue, Ref, Task, ZIO}

trait LiveBookActorQueue extends BookActorQueue  {
  val bookActor: BookActor.Service[Any]
  val bookActorQueue: BookActorQueue.Service[Any] = new BookActorQueue.Service[Any] {
    type PendingMessage = (BookCommand, Promise[Throwable,Boolean] )
    val commandQueue = Queue.bounded[BookCommand](100)
    val bookActorQueue: Task[BookActorQueue.Service[Any]] = run
    override def put(bookCommand: BookCommand): Task[Boolean] = bookActorQueue.flatMap (_.put(bookCommand))
    def process(message: PendingMessage, state: Ref[BookServiceState]): Task[Unit] = {
      for {
        s <- state.get
        (command, promise) = message
        events <- bookActor.handleCommand(command,s)
        newState <- bookActor.applyEvents(events,s)
        _ <- bookActor.publishAndStoreEvents(events)
        _ <- state.set(s)
      } yield  IO.unit
    }

    def run() : Task[BookActorQueue.Service[Any]] = for {
      initialState <- bookActor.restore
      state <- Ref.make(initialState)
      commandQueue <- Queue.bounded[PendingMessage](100)
      _ <- (for {
        pendingMessage <- commandQueue.take
        _ <- process(pendingMessage,state)
      } yield IO.unit).forever.fork
    } yield new BookActorQueue.Service[Any] {
      override def put(bookCommand: BookCommand): Task[Boolean] = for {
        promise <- Promise.make[Throwable, Boolean]
        _       <- commandQueue.offer((bookCommand, promise))
        value   <- promise.await
      } yield value
    }
  }
}

