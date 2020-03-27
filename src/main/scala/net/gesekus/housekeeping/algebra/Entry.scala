package net.gesekus.housekeeping.algebra.entry

import java.time.LocalDateTime

import net.gesekus.housekeeping.algebra.category.CategoryId

final case class EntryId(id: String)
final case class EntryTitle(title: String)
final case class Entry(id: EntryId, title: EntryTitle, amount: Double, date: LocalDateTime, categories: Set[CategoryId])

trait Entries[F[_]] {
  def getEntry(id: EntryId): F[Entry]
  def addEntry(Entry: Entry): F[Entry]
  def updateEntry(Entry: Entry): F[Entry]
  def deleteEntry(id: EntryId): F[Entry]
}

object Entry {}
