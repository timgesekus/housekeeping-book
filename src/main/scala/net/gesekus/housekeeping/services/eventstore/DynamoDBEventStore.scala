package net.gesekus.housekeeping.services.eventstore
import java.util.UUID

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.document.{Item, PrimaryKey}
import net.gesekus.housekeeping.services.book.BookEvents
import net.gesekus.housekeeping.services.bookservice
import zio.Task

case class PersistableEvent(uuid: UUID, index: Long, eventType: String, event: String)
/*
trait  DynamoDBEventStore extends EventStore {
  val db : AmazonDynamoDB
  val eventTableName = "BookEvent"
  val eventHashKeyName = "bookId"
  val eventRangeKey = "version"
  override val eventStore: EventStore.Service[Any] = new EventStore.Service[Any] {

    override def getSnapShot(): Task[bookservice.BookServiceState] = ???

    override def getEventsSinceLastSnapShot(): Task[Seq[BookEvents]] = ???

    override def storeEvents(events: Seq[PersistableEvent]): Task[Int] = {
      val event = events(0)
      new PrimaryKey()
      (new Item()).withPrimaryKey(eventHashKeyName,event.uuid,eventRangeKey, event.index)
        .withString("type", event.eventType)
        .withString("event", event.event)
    }

    override def storeSnapShot(bookServiceState: bookservice.BookServiceState): Task[Int] = ???
  }
 }
*/