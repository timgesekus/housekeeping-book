package bookstore

import java.time.LocalDateTime

import argonaut.Parse
import net.gesekus.housekeeping.algebra.book.{Book, BookId, BookTitle}
import net.gesekus.housekeeping.algebra.category.{Category, CategoryId, CategoryTitle}
import net.gesekus.housekeeping.algebra.entry
import net.gesekus.housekeeping.services.book.{BookStore, CreateBook}
import net.gesekus.housekeeping.services.bookservice.BookServiceJson._
import org.scalatest.FlatSpec

class BookServiceJsonSpec extends FlatSpec {
  val emptyBookTitle = BookTitle("TestBook")
  val emptyBookId    = BookId("One")

  val newEntry = entry.Entry(
    entry.EntryId("TEST1"),
    entry.EntryTitle("Entry Title"),
    10.0,
    LocalDateTime.now(),
    Set[CategoryId]()
  )
  // val newCategory                 = Category(CategoryId("CAT1"), CategoryTitle("Category Title"))

  val newCategory = Category(CategoryId("0"), CategoryTitle("First Category"))

  "A BookStoreJson" should "create json from messages" in {
    val createBook = CreateBook(Book(BookId("Test"),BookTitle("TestTitle"),Map(), Map()))
    val createBookEncode = CreateBookCodec.encode(createBook)
    val createBookString = createBookEncode.spaces2
    val decodedCreateBookET = Parse.decodeEither[CreateBook](createBookString)
    assert(decodedCreateBookET.isRight)
    assert(decodedCreateBookET.right.get == createBook)
  }

}
