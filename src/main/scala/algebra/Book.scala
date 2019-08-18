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
import scalaz.deriving
import scalaz.std.string._

import scalaz.Order
import scalaz.Show


final case class BookId(id: Int)
final case class Title(test: String)

@deriving(Order, Show)
final case class MachineNode(id: String)

final case class Book(id: BookId, title: Title,  entries: Maybe[NonEmptyList[EntryId]] )

trait Books[F[_]] {
    def getBook(id: BookId) : F[Book]
    def addBook(book: Book) : F[Book]
    def updateBook (book: Book) : F[Book]
    def deleteBook (id: BookId) : F[Book]
}