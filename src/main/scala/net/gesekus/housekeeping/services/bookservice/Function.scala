package net.gesekus.housekeeping.services.bookservice

import net.gesekus.housekeeping.services.book.{BookEvents, BookStore}
import net.gesekus.housekeeping.services.eventstore.EventStore
import zio.{RIO}

class Function {
  type Program = RIO[BookStore with EventStore, BookServiceState]
  def buildFromEvents(): Program                            = ???
  def applyAndStoreEvents(events: Seq[BookEvents]): Program = ???
}
