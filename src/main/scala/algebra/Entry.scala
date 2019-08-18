package net
package gesekus
package housekeeping
package algebra
package entry
import java.time.LocalDateTime

import scalaz.NonEmptyList
import scalaz.Maybe
import net.gesekus.housekeeping.algebra.category.CategoryId
import scala.Int

import java.lang.String
import java.lang.Double

final case class EntryId(id: Int)
final case class Title(test: String)
final case class Entry(id: EntryId, title: Title, amount: Double, date: LocalDateTime, categories: Maybe[NonEmptyList[CategoryId]] )

trait Entries[F[_]] {
    def getEntry(id: EntryId) : F[Entry]
    def addEntry(Entry: Entry) : F[Entry]
    def updateEntry (Entry: Entry) : F[Entry]
    def deleteEntry (id: EntryId) : F[Entry]
}

