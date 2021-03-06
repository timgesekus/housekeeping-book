package testenv

import zio.test.{testM, _}
import net.gesekus.housekeeping.algebra.book.BookId
import net.gesekus.housekeeping.algebra.category.{Category, CategoryId, CategoryTitle}
import net.gesekus.housekeeping.services.book.CategoryAdded
import net.gesekus.housekeeping.services.eventpublisher.InMemoryEventPublisherImpl
import zio.test.Assertion._

object InMemeroyEventPublisherSpec
    extends DefaultRunnableSpec(
      suite("InMemeroyEventPublisherSpec")(
        testM("initial publisher is empty ") {
          val ep = new InMemoryEventPublisherImpl {}
          for {
            seq <- ep.events
          } yield assert(seq, equalTo(Seq()))
        },
        testM("published events are added") {
          def createCategroyAdded(i: Int): CategoryAdded = {
            val c = Category(CategoryId(s"C${i}"), CategoryTitle(s"C${i}Title"))
            CategoryAdded(BookId("Test"), c)
          }
          val e1 = createCategroyAdded(1)
          val e2 = createCategroyAdded(2)
          val e3 = createCategroyAdded(3)
          val seq1 = Seq(e1, e2)
          val seq2 = Seq(e3)
          val ep = new InMemoryEventPublisherImpl {}
          for {
            _ <- ep.publishEvents(seq1)
            _ <- ep.publishEvents(seq2)
            resultSeq <- ep.events()
          } yield assert(resultSeq, equalTo(Seq(e1, e2, e3)))
        }
      )
    )
