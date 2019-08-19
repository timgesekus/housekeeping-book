package net
package gesekus
package housekeeping
package algebra
package book 


import scalaz.Maybe
import scalaz.NonEmptyList
import net.gesekus.housekeeping.algebra.entry.EntryId
import scala.Int
import java.lang.String

import scalaz.std.string._
import zio._ 

import scalaz.Order
import scalaz.Show


final case class BookId(id: Int)
final case class Title(test: String)
final case class Book(id: BookId, title: Title,  entries: Maybe[NonEmptyList[EntryId]] )

trait Books {
    def getAll() : Task[Maybe[NonEmptyList[Book]]]
    def getBook(id: BookId) : Task[Book]
    def addBook(book: Book) : Task[Book]
    def updateBook (book: Book) : Task[Book]
    def deleteBook (id: BookId) : Task[Book]
}