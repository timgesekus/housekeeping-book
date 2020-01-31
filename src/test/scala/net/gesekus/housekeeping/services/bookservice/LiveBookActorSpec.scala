package net.gesekus.housekeeping.services.bookservice

import net.gesekus.housekeeping.algebra.book.{Book, BookId, BookTitle}
import net.gesekus.housekeeping.algebra.category.{Category, CategoryId, CategoryTitle}
import net.gesekus.housekeeping.services.book.CategoryAdded
import net.gesekus.housekeeping.services.eventpublisher.InMemoryEventPublisher
import net.gesekus.housekeeping.services.eventstore.InMemoryEvenStore
import zio.test.Assertion.equalTo
import zio.test.{DefaultRunnableSpec, assert, suite, testM}

object LiveBookActorSpec
    extends DefaultRunnableSpec(
      suite("LiveBookActorSpec")(
        testM("restore from empty eventStore is empty State") {
          val ba = new BookActor.Live with InMemoryEventPublisher  with InMemoryEvenStore {}
          for {
            state <- ba.bookActor.restore
          } yield assert(state, equalTo(BookServiceState(Book.init(BookId("Book1"), BookTitle("Anna")))))
        },
        testM("restore from filled eventStore works") {
          val newCategory = Category(CategoryId("Test"), CategoryTitle("Title"))
          val ba = new BookActor.Live with InMemoryEventPublisher  with InMemoryEvenStore {}
          for {
            _      <- ba.eventStore.storeEvents(Seq(CategoryAdded(BookId("Book1"),newCategory)))
            state <- ba.bookActor.restore
          } yield assert(state.book.categories.size, equalTo(1))
        }
      )
    )
