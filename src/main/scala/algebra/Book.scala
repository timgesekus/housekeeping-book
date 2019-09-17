package net.gesekus.housekeeping.algebra.book

import scalaz.Maybe
import scalaz.NonEmptyList
import scalaz.syntax._
import scalaz._
import Scalaz._
import net.gesekus.housekeeping.algebra.entry.{Entry, EntryId}
import net.gesekus.housekeeping.algebra.category.{CategoryId,Category}
import net.gesekus.housekeeping.algebra.entry.Entry._

import scala.Int
import java.lang.String

import scalaz.std.string._
import zio._
import scalaz.Order
import scalaz.Show
import argonaut.CodecJson
import argonaut.Argonaut._
import net.gesekus.housekeeping.algebra.category.CategoryId
import scalaz.Maybe._

final case class BookId(id: Int)
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
  def init(id: BookId, title: BookTitle ) = Book(id,title, Map[EntryId, Entry](),Map[CategoryId, Category]())
}
