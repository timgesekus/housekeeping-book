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
import argonaut._
import net.gesekus.housekeeping.algebra.category.Category._
import scalaz.syntax._
import scalaz._, Scalaz._

final case class EntryId(id: Int)
final case class EntryTitle(title: String)
final case class Entry(id: EntryId, title: EntryTitle, amount: Double, date: LocalDateTime, categories: Set[CategoryId])

trait Entries[F[_]] {
  def getEntry(id: EntryId): F[Entry]
  def addEntry(Entry: Entry): F[Entry]
  def updateEntry(Entry: Entry): F[Entry]
  def deleteEntry(id: EntryId): F[Entry]
}

object Entry {

    
   
}
