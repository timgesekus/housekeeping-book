package net.gesekus.housekeeping.algebra.book

import net.gesekus.housekeeping.algebra.category.{ Category, CategoryId }
import net.gesekus.housekeeping.algebra.entry.{ Entry, EntryId }
import scalaz.{ Maybe, NonEmptyList }
import zio.Task

final case class BookId(id: String)
final case class BookTitle(title: String)
final case class Book(id: BookId, title: BookTitle, entries: Map[EntryId, Entry], categories: Map[CategoryId, Category])

trait Books {
  def getAll(): Task[Maybe[NonEmptyList[Book]]]
  def getBook(id: BookId): Task[Book]
  def addBook(book: Book): Task[Book]
  def updateBook(book: Book): Task[Book]
  def deleteBook(id: BookId): Task[Book]
}

object Book {
  def init(id: BookId, title: BookTitle) = Book(id, title, Map[EntryId, Entry](), Map[CategoryId, Category]())
}
