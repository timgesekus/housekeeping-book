package net.gesekus.housekeeping.services.book

import java.time.LocalDateTime

import net.gesekus.housekeeping.algebra.book._
import net.gesekus.housekeeping.algebra.category.{ Category, CategoryId, CategoryTitle }
import net.gesekus.housekeeping.algebra.entry.EntryId
import net.gesekus.housekeeping.algebra.{ entry, _ }
import net.gesekus.housekeeping.services.book.BookStore._
import org.scalatest._
import scalaz.Scalaz._
import scalaz._

class BookServiceTest extends FlatSpec {
  val emptyBookTitle = BookTitle("TestBook")
  val emptyBookId    = BookId("One")
  val state          = BookStore.init(emptyBookId, emptyBookTitle)

  val newEntry = entry.Entry(
    entry.EntryId("TEST1"),
    entry.EntryTitle("Entry Title"),
    10.0,
    LocalDateTime.now(),
    Set[CategoryId]()
  )
  // val newCategory                 = Category(CategoryId("CAT1"), CategoryTitle("Category Title"))

  val newCategory = Category(CategoryId("0"), CategoryTitle("First Category"))

  "A BookStore" should "create an empty BookStore" in {
    assert(state.book.id === emptyBookId)
    assert(state.book.title === emptyBookTitle)
    assert(state.book.entries.isEmpty)
    assert(state.book.categories.isEmpty)
  }

  it should "have a new entry after adding one" in {
    val (newState, addedEntryEt) = addEntry(newEntry).run.run(state)
    addedEntryEt match {
      case \/-(addedEntry) => {
        assert(addedEntry.id === newEntry.id)
        assert(addedEntry.title.title == "Entry Title")
        assert(newState.book.entries.length == 1)
        assert(newState.book.categories.length == 0)
        assert(newState.book.entries.get(addedEntry.id).nonEmpty)
        assert(newState.book.entries.get(addedEntry.id).get === addedEntry)
      }
      case -\/(e) => assert(false, e.getMessage)
    }
  }

  it should "fail when adding entry with non existing categories" in {
    val entryWithUnkownCat = entry.Entry(
      entry.EntryId("TEST1"),
      entry.EntryTitle("Entry Title"),
      10.0,
      LocalDateTime.now(),
      Set[CategoryId](CategoryId("3"))
    )
    val (newState, addedEntryEt) = addEntry(entryWithUnkownCat).run.run(state)
    addedEntryEt match {
      case -\/(e) => {
        assert(e.getMessage == "At least one category did not exist")
      }
      case \/-(_) => assert(false, "No exception thrown")
    }
  }
  it should "have a new category after adding one" in {
    val (newState, addedCategoryEt) = addCategory(newCategory).run(state)
    addedCategoryEt match {
      case \/-(_) => {
        assert(newState.book.entries.length == 0)
        assert(newState.book.categories.length == 1)
      }
      case -\/(e) => assert(false, e.getMessage)
    }
  }
  it should "fail on adding a category that doesn't exist" in {
    def test =
      for {
        addedEntry   <- addEntry(newEntry)
        changedEntry <- addCategoriesToEntry(addedEntry.id, Set(CategoryId("5")))
      } yield changedEntry

    val (newSate, changedEntryEt) = test.run.run(state)
    changedEntryEt match {
      case -\/(e) => {
        assert(e.getMessage == "At least one category did not exist")
      }
      case \/-(_) => assert(false, "Should have failed")
    }
  }

  it should "fail on adding a category to an entry that doesn't exist" in {
    def test =
      for {
        _            <- addCategory(newCategory)
        _            <- addEntry(newEntry)
        changedEntry <- addCategoriesToEntry(entry.EntryId("NA"), Set())
      } yield changedEntry

    val (newState, changedEntryEt) = test.run.run(state)
    changedEntryEt match {
      case -\/(e) => {
        assert(e.getMessage == "At least one entry did not exist")
      }
      case \/-(_) => assert(false)
    }
  }

  it should "fail on removal of an non existing entry" in {
    val (stateAfterRemove, removedEntryEt) = removeEntry(EntryId("NA")).run.run(state)
    removedEntryEt match {
      case -\/(e) => {
        assert(e.getMessage == "At least one entry did not exist")
      }
      case \/-(_) => assert(false)
    }
  }

  it should "remove categories from an entry" in {
    val newEntry = entry.Entry(
      entry.EntryId("Ent1"),
      entry.EntryTitle("Entry Title"),
      10.0,
      LocalDateTime.now(),
      Set[CategoryId](newCategory.id)
    )

    def test =
      for {
        _            <- addCategory(newCategory)
        addedEntry   <- addEntry(newEntry)
        changedEntry <- removeCategoriesFromEntry(addedEntry.id, Set(newCategory.id, CategoryId("2")))
      } yield changedEntry

    val (stateAfterRemove, changedEntryET) = test.run.run(state)
    changedEntryET match {
      case \/-(changedEntry) => {
        assert(changedEntry.categories.isEmpty)
      }
      case -\/(e) => assert(false, e.getMessage)
    }
    assert(stateAfterRemove.book.entries.size == 1)
    assert(stateAfterRemove.book.entries(newEntry.id).categories.isEmpty)
  }

  it should "remove a category and the category from all entries" in {
    val entry1 = entry.Entry(
      entry.EntryId("Ent1"),
      entry.EntryTitle("Entry Title"),
      10.0,
      LocalDateTime.now(),
      Set[CategoryId](CategoryId("1"), CategoryId("2"))
    )
    val entry2 = entry.Entry(
      entry.EntryId("Ent2"),
      entry.EntryTitle("Entry Title"),
      10.0,
      LocalDateTime.now(),
      Set[CategoryId](CategoryId("1"), CategoryId("2"))
    )

    def test =
      for {
        _               <- addCategory(Category(CategoryId("1"), CategoryTitle("First Category")))
        _               <- addCategory(Category(CategoryId("2"), CategoryTitle("Second Category")))
        _               <- addEntry(entry1)
        _               <- addEntry(entry2)
        removedCategory <- removeCategory(CategoryId("1"))
      } yield removedCategory

    val (stateAfterRemove, removeCategoryET) = test.run.run(state)
    removeCategoryET match {
      case \/-(removeCategory) => {
        assert(removeCategory.id === CategoryId("1"))
      }
      case -\/(e) => assert(false, e.getStackTraceString)
    }
    assert(stateAfterRemove.book.entries.size == 2)
    assert(!stateAfterRemove.book.entries(EntryId("Ent1")).categories.contains(CategoryId("1")))
    assert(!stateAfterRemove.book.entries(EntryId("Ent2")).categories.contains(CategoryId("1")))
  }
}
