package net.gesekus.housekeeping.testenv

import net.gesekus.housekeeping.algebra.book.{ BookId, BookTitle }
import net.gesekus.housekeeping.services.book.{ BookEvents, BookStore }
import net.gesekus.housekeeping.services.bookservice
import net.gesekus.housekeeping.services.bookservice.{ BookServiceES, EventStore }
import zio.Exit.Success
import zio.{ Task, UIO, ZIO }
import ZIO.succeed

trait InMemoryEvenStore extends EventStore {
  override val eventStore: EventStore.Service[Any] = new EventStore.Service[Any] {
    private var eventIdBeforeSnapshot: Int = 0;
    private var snapshot = BookStore.init(BookId("Book1"), BookTitle("Anna"))
    private var eventSeq: Seq[BookEvents] = Nil

    override def getSnapShot(): Task[bookservice.BookServiceState] = ZIO.succeed(snapshot)

    override def getEventsSinceLastSnapShot(): Task[Seq[BookEvents]] = {
      val size = eventSeq.size
      succeed(eventSeq.slice(eventIdBeforeSnapshot, size))
    }

    override def storeEvents(events: Seq[BookEvents]): Task[Int] = {
      eventSeq = eventSeq ++ events
      succeed(eventSeq.length)
    }

    override def storeSnapShot(bookServiceState: bookservice.BookServiceState): Task[Int] = {
      snapshot = bookServiceState
      eventIdBeforeSnapshot = eventSeq.length
      Console.out.println("Event " + eventSeq)
      succeed(eventIdBeforeSnapshot)
      //succeed(1)

    }
  }
}


