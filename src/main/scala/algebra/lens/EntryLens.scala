package net.gesekus.housekeeping.algebra.lens

import java.time.LocalDateTime

import net.gesekus.housekeeping.algebra.category.CategoryId
import net.gesekus.housekeeping.algebra.entry.{Entry, EntryId, EntryTitle}
import scalaz.{Lens, Maybe, NonEmptyList}

object EntryLens {
  def idValL= Lens.lensu[EntryId, Int]((entry, newVal) => entry.copy(id = newVal), _.id)
  def idL      = Lens.lensu[Entry, EntryId]((entry, newVal) => entry.copy(id = newVal), _.id)
  def titleL   = Lens.lensu[Entry, EntryTitle]((entry, newVal) => entry.copy(title = newVal), _.title)
  def entryIdL = Lens.lensu[EntryId, Int]((entryId, newVal) => entryId.copy(id = newVal), _.id)
  def categoriesL =
    Lens.lensu[Entry, Set[CategoryId]](
      (categories, newVal) => categories.copy(categories = newVal),
      _.categories
    )
  def amountL = Lens.lensu[Entry, Double]((amount, newVal) => amount.copy(amount = newVal), _.amount)
  def dateL   = Lens.lensu[Entry, LocalDateTime]((date, newVal) => date.copy(date = newVal), _.date)
}
