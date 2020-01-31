package net.gesekus.housekeeping.services.eventpublisher

import net.gesekus.housekeeping.services.book.BookEvents
import zio.Task

trait EventPublisher {
  val eventPublisher: EventPublisher.Service[Any]
}

object EventPublisher {
  trait Service[R] {
    def publishEvents(events: Seq[BookEvents]): Task[Unit]
  }
}
