package net.gesekus.housekeeping.services.bookservice

import net.gesekus.housekeeping.services.book.BookEvents
import zio.{  Task}

trait EventStore {
  val eventStore: EventStore.Service[Any]
}

object EventStore {
  trait Service[R] {
    def getSnapShot(): Task[BookServiceState]
    def getEventsSinceLastSnapShot(): Task[Seq[BookEvents]]
    def storeEvents(events: Seq[BookEvents]): Task[Int]
    def storeSnapShot(bookServiceState: BookServiceState): Task[Int]
  }
}
