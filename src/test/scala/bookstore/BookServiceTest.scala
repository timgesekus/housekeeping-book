package net.gesekus.housekeeping.services.book

import java.time.LocalDateTime

import net.gesekus.housekeeping.algebra.{entry, _}
import book._
import org.scalatest._
import BookStore._
import scalaz._
import Scalaz._
import scalaz.Maybe.Empty
import net.gesekus.housekeeping.algebra._
import bookstore.BookStore._
import net.gesekus.housekeeping.algebra.category.{Category, CategoryId, CategoryTitle}

class BookServiceTest extends FlatSpec {
  "A BookStore" should "create an empty BookStore" in {
    val bookId =  BookId(1)
    val bookTitle = BookTitle("TestBook")
    val emptyBook = Book.init( bookId, bookTitle)
    assert (emptyBook.id === bookId )
    assert (emptyBook.title === bookTitle)
    assert (emptyBook.entries.isEmpty)
    assert (emptyBook.categories.isEmpty)

  }
  it should "have a new entry after adding one" in {
    val state = BookStore.empty
    val newEntry = entry.Entry(entry.EntryId(0), entry.EntryTitle("Entry Title"), 10.0, LocalDateTime.now(), Set[CategoryId]())
    val result = addEntry(BookId(1),newEntry).run.run(state)
    val newState = result._1
    val addedEntryEt = result._2
    assert (addedEntryEt.isRight)
    addedEntryEt match {
      case \/-(addedEntry) => {
        assert (addedEntry.id.id == 1)
        assert (addedEntry.title.title == "Entry Title")
        assert (newState.book.entries.length == 1)
        assert (newState.book.categories.length == 0)
        assert (newState.book.entries.get(addedEntry.id).nonEmpty)
        assert (newState.book.entries.get(addedEntry.id).get === addedEntry)
      }
      case -\/(_) => assert(false)
    }
  }

  it should "fail when adding entry with non existing categories" in {
    val state = BookStore.empty
    val newEntry = entry.Entry(entry.EntryId(0), entry.EntryTitle("Entry Title"), 10.0, LocalDateTime.now(), Set[CategoryId](CategoryId(2)))
    val result = addEntry(BookId(1),newEntry).run.run(state)
    val newState = result._1
    val addedEntryEt = result._2
    addedEntryEt match {
      case -\/(e) => {
        assert (e.getMessage == "At least one category did not exist")
      }
      case \/-(_) => assert(false)
    }
  }
  it should "have a new category after adding one" in {
    val state = BookStore.empty
    val newCategory = Category(CategoryId(0),CategoryTitle("Category Title"))
    val result = addCategory(BookId(1),newCategory).run(state)
    val newState = result._1
    val addedCategoryEt = result._2
    addedCategoryEt match {
      case \/-(_) => {
        assert (newState.book.entries.length == 0)
        assert (newState.book.categories.length == 1)
      }
      case -\/(_) => assert(false)
    }
  }
  it should "fail on adding a category that doesn't exist" in {
    val state = BookStore.empty
    val newEntry = entry.Entry(entry.EntryId(0), entry.EntryTitle("Entry Title"), 10.0, LocalDateTime.now(), Set())
    val result = addEntry(BookId(1),newEntry).run.run(state)
    val newState = result._1
    val addedEntryEt = result._2
    addedEntryEt  match {
      case \/-(addedEntry) => {
        val addCategoriesResult = addCategoriesToEntry(addedEntry.id,Set(CategoryId(5))).run.run(newState)
        val stateAfterAdd = addCategoriesResult._1
        val changedEntryEt = addCategoriesResult._2
        changedEntryEt match {
          case -\/(e) => {
            assert (e.getMessage == "At least one category did not exist")
          }
          case \/-(_) => assert(false)
        }  }
      case -\/(_) => assert(false)
    }
  }

  it should "fail on adding a category to an entry that doesn't exist" in {
    val state = BookStore.empty
    val newEntry = entry.Entry(entry.EntryId(0), entry.EntryTitle("Entry Title"), 10.0, LocalDateTime.now(), Set[CategoryId](CategoryId(2)))
    val result = addEntry(BookId(1),newEntry).run.run(state)
    val newState = result._1
    val addCategoriesResult = addCategoriesToEntry(entry.EntryId(5),Set()).run.run(newState)
    val stateAfterAdd = addCategoriesResult._1
    val changedEntryEt = addCategoriesResult._2
    changedEntryEt match {
      case -\/(e) => {
        assert (e.getMessage == "At least one entry did not exist")
      }
      case \/-(_) => assert(false)
    }
  }
}
