package net.gesekus.housekeeping.testenv

import net.gesekus.housekeeping.services.book.BookEvents
import net.gesekus.housekeeping.services.bookservice.EventPublisher
import zio.{ Task, ZIO }

trait InMemoryEventPublisher extends EventPublisher {
  override val eventPublisher: EventPublisher.Service[Any] = new InMemoryEventPublisherImpl {}
}

trait InMemoryEventPublisherImpl extends EventPublisher.Service[Any] {
  private var eventSeq: Seq[BookEvents] = Seq()

  override def publishEvents(events: Seq[BookEvents]): Task[Unit] = {
    eventSeq = eventSeq ++ events
    ZIO.succeed()
  }

  def events(): Task[Seq[BookEvents]] = ZIO.succeed(eventSeq)
}
