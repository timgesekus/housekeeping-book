package net.gesekus.housekeeping.algebra.lens

import net.gesekus.housekeeping.algebra.book.{ Book, BookId, BookTitle }
import net.gesekus.housekeeping.algebra.category.{ Category, CategoryId }
import net.gesekus.housekeeping.algebra.entry.{ Entry, EntryId }
import scalaz.Lens

object BookLens {
  def idL = Lens.lensu[Book, BookId]((book, newVal) => book.copy(id = newVal), _.id)
  def titleL = Lens.lensu[Book, BookTitle]((book, newVal) => book.copy(title = newVal), _.title)
  def valIdL: Lens[BookId, String] = Lens.lensu((bookId, newVal) => bookId.copy(id = newVal), _.id)

  def entriesL =
    Lens.lensu[Book, Map[EntryId, Entry]]((entries, newVal) => entries.copy(entries = newVal), _.entries)

  def entryL(entryId: EntryId) = Lens.mapVLens[EntryId, Entry](entryId)

  def categoriesL =
    Lens.lensu[Book, Map[CategoryId, Category]](
      (categories, newVal) => categories.copy(categories = newVal),
      _.categories
    )

  def categoryL(categoryId: CategoryId) = Lens.mapVLens[CategoryId, Category](categoryId)

}
