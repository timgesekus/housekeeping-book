package net.gesekus.housekeeping.services.book

import net.gesekus.housekeeping.algebra.book.{ Book, BookId, BookTitle }
import net.gesekus.housekeeping.algebra.category.{ Category, CategoryId }
import net.gesekus.housekeeping.algebra.entry.{ Entry, EntryId }
import net.gesekus.housekeeping.algebra.lens._
import net.gesekus.housekeeping.services.bookservice._
import scalaz.Scalaz._
import scalaz._

sealed class BookCommand
final case class CreateBook(book: Book)                                                        extends BookCommand
final case class AddEntry(bookId: BookId, entry: Entry)                                        extends BookCommand
final case class AddCategory(bookId: BookId, category: Category)                               extends BookCommand
final case class AddEntryToCategory(bookeId: BookId, entryId: EntryId, categoryId: CategoryId) extends BookCommand

sealed class BookEvents
final case class BookCreated(book: Book)                                                        extends BookEvents
final case class EntryAdded(bookId: BookId, entry: Entry)                                       extends BookEvents
final case class CategoryAdded(bookId: BookId, category: Category)                              extends BookEvents
final case class EntryAddedToCategory(bookId: BookId, entryId: EntryId, categoryId: CategoryId) extends BookEvents

trait BookStore {
  def addEntry(bookId: BookId, entry: Entry): BookServiceES[Entry]
  def addCategory(bookId: BookId, category: Category): BookServiceES[Category]
}

object BookStore extends BookStore {

  val init = BookServiceState(Book.init(BookId(1), BookTitle(" Book")), NextIds(CategoryId(1), EntryId(1)))

  val bookL =
    Lens.lensu[BookServiceState, Book]((bookServiceState, newVal) => bookServiceState.copy(book = newVal), _.book)

  val nextIdsL = Lens
    .lensu[BookServiceState, NextIds]((bookServiceState, newVal) => bookServiceState.copy(nextIds = newVal), _.nextIds)

  val nextEntryIdL = nextIdsL >=> Lens
    .lensu[NextIds, EntryId]((nextIds, newVal) => nextIds.copy(entryId = newVal), _.entryId)

  val nextCategoryIdL = nextIdsL >=>
    Lens.lensu[NextIds, CategoryId]((nextIds, newVal) => nextIds.copy(categoryId = newVal), _.categoryId)

  val empty = BookServiceState(Book.init(BookId(1), BookTitle("Anna")), NextIds(CategoryId(1), EntryId(1)))

  def checkIfCategoriesExist(categoryIds: Set[CategoryId]): BookServiceES[Boolean] = {
    import BookLens.categoriesL
    val stateFunc: BookServiceS[BookServiceStateType[Boolean]] = for {
      categories <- bookL >=> categoriesL
      allExist   <- state(categoryIds.forall(categories.contains(_)))
    } yield if (allExist) true.right else new Exception("Not all").left
    BookServiceES.apply(stateFunc)
  }

  def addEntry(bookId: BookId, entry: Entry): BookServiceES[Entry] = {
    import BookLens.entriesL
    import EntryLens._
    import BookServiceES._
    for {
      entryCategories <- entry.categories |> liftV
      _               <- checkIfCategoriesExist(entryCategories)
      nextEntryId     <- nextEntryIdL.st |> BookServiceES.liftS
      _               <- (nextEntryIdL >=> idValL).mods(_ + 1) |> BookServiceES.liftS
      nextEntry       <- idL.set(entry, nextEntryId) |> liftV
      _               <- (bookL >=> entriesL >=> Lens.mapVLens(nextEntryId)).mods(_ => nextEntry.some) |> liftS
    } yield nextEntry

  }

  def addCategory(bookId: BookId, category: Category): BookServiceES[Category] = {
    import BookLens.categoriesL
    import CategoryLens._
    import BookServiceES._
    for {
      nextCategoryId <- nextCategoryIdL.st |> liftS
      _              <- (nextCategoryIdL >=> valIdL).mods(_ + 1) |> liftS
      nextCategory   <- idL.set(category, nextCategoryId) |> liftV
      _              <- (bookL >=> categoriesL >=> Lens.mapVLens(nextCategoryId)).mods(_ => nextCategory.some) |> liftS
    } yield nextCategory
  }
}
