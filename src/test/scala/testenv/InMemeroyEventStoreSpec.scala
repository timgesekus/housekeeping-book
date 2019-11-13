package testenv

import zio._
import zio.console._
import zio.test.{testM, _}
import zio.test.Assertion._
import zio.test.environment._
import ZIO.succeed
import net.gesekus.housekeeping.algebra.book.BookId
import net.gesekus.housekeeping.algebra.entry._
import net.gesekus.housekeeping.algebra.category.{Category, CategoryId, CategoryTitle}
import net.gesekus.housekeeping.services.book.{BookStore, CategoryAdded, EntryAdded}
import net.gesekus.housekeeping.services.bookservice.{BookServiceES, BookServiceState}
import net.gesekus.housekeeping.testenv.InMemoryEvenStore
import scalaz.Scalaz._
import scalaz._
import zio.test.Assertion._

object HelloWorldSpec
    extends DefaultRunnableSpec(
      suite("InMemoryEvenStoreSpec")(
        testM("initial snapshot is empty book") {
          val es = new InMemoryEvenStore {}.eventStore
          for {
            snapshot <- es.getSnapShot()
          } yield assert(snapshot.book.id.id, equalTo("Book1"))
        },
        testM("Stored snapshot can be restored") {
          val newCategory = Category(CategoryId("Test"), CategoryTitle("Title"))
          val es = new InMemoryEvenStore {}.eventStore
          for {
            snapshot <- es.getSnapShot()
            newState <- BookStore.addCategory(newCategory).run.exec(snapshot) |> succeed
            index <- es.storeSnapShot(newState)
            newSnapshot <- es.getSnapShot()
          } yield assert(index, equalTo(0)) &&
            assert(newSnapshot.book.categories.size, equalTo(1))
        },
        testM("Events can be replayed") {
          val es = new InMemoryEvenStore {}.eventStore
          val bid = BookId("Book1")
          def createCategroyAdded(i: Int) : CategoryAdded = {
            val c = Category(CategoryId(s"C${i}"), CategoryTitle(s"C${i}Title"))
            CategoryAdded(bid,c)
          }
          val c1 = Category(CategoryId("C1"), CategoryTitle("C11Title"))
          val e1 = createCategroyAdded(1)
          val e2 = createCategroyAdded(2)
          val e3 = createCategroyAdded(3)
          for {
            index1 <- es.storeEvents(Seq(e1,e2))
            index2 <- es.storeEvents(Seq(e3))
            events <- es.getEventsSinceLastSnapShot()
          } yield assert(index1, equalTo(2)) &&
            assert(index2, equalTo(3)) &&
            assert(events.size, equalTo(3) ) &&
            assert(events, equalTo(Seq(e1,e2,e3)))
        }
      )
    )
