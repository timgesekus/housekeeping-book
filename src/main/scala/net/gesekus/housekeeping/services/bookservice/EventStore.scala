package net.gesekus.housekeeping.services.bookservice

import net.gesekus.housekeeping.services.book.BookEvents
import zio.{ RIO, Task, ZIO }

trait EventStore {
  def eventStore: EventStore.Service
}

object EventStore {
  trait Service {
    def getSnapShot(): Task[BookServiceState]
    def getEventsSinceLastSnapShot(): Task[Seq[BookEvents]]
    def storeEvents(events: Seq[BookEvents]): Task[Int]
    def storeSnapShot(bookServiceState: BookServiceState): Task[Int]
  }
}
