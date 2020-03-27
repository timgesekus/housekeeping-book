package net.gesekus.housekeeping.services.bookservice

import net.gesekus.housekeeping.algebra.book.{ Book, BookId, BookTitle }
import net.gesekus.housekeeping.algebra.category.{ Category, CategoryId }
import net.gesekus.housekeeping.algebra.entry.{ Entry, EntryId }
import net.gesekus.housekeeping.algebra.lens._
import scalaz.Scalaz._
import scalaz._
import BookServiceES._
import net.gesekus.housekeeping.services.bookrepository.BookStore.bookL
import BookLens.{ categoriesL, categoriesL, categoryL, entriesL, entryL }
import BookServiceES._

object BookService {
  def init(id: BookId, title: BookTitle): Book = Book(id, title, Map[EntryId, Entry](), Map[CategoryId, Category]())

  private def checkIfCategoriesExist(categoryIds: Set[CategoryId]): BookServiceES[Boolean] = {

    val stateFunc: BookServiceS[BookServiceStateType[Boolean]] = for {
      categories <- bookL >=> categoriesL
      allExist <- state(categoryIds.forall(categories.contains))
    } yield if (allExist) true.right else new Exception("At least one category did not exist").left
    BookServiceES.apply(stateFunc)
  }

  private def checkIfEntryExists(entryId: EntryId): BookServiceES[Boolean] = checkIfEntriesExists(Set[EntryId](entryId))

  def checkIfEntriesContainCategory(categoryId: CategoryId, entries: Map[EntryId, Entry]): Boolean =
    entries.map(_._2.categories).exists(_.contains(categoryId))

  private def checkIfEntriesExists(entryIds: Set[EntryId]): BookServiceES[Boolean] = {
    import BookLens.entriesL
    val stateFunc: BookServiceS[BookServiceStateType[Boolean]] = for {
      entries <- bookL >=> entriesL
      allExist <- state(entryIds.forall(entries.contains(_)))
    } yield if (allExist) true.right else new Exception("At least one entry did not exist").left
    BookServiceES.apply(stateFunc)
  }

  def failIfEntriesContainCategory(categoryId: CategoryId): BookServiceES[Boolean] = {
    val stateFunc: BookServiceS[BookServiceStateType[Boolean]] = for {
      entries <- bookL >=> entriesL
      contains <- state(checkIfEntriesContainCategory(categoryId, entries))
    } yield if (contains) true.right else new Exception("Category still used").left

    BookServiceES.apply(stateFunc)
  }

  def updateEntry(entry: Entry): BookServiceES[Entry] =
    for {
      _ <- checkIfCategoriesExist(entry.categories)
      updatedEntry <- ((bookL >=> entriesL).mods(_ + ((entry.id, entry)))) |> liftS
    } yield updatedEntry

  def removeEntry(entryId: EntryId): BookServiceES[Entry] =
    for {
      _ <- checkIfEntryExists(entryId)
      entryOp <- (bookL >=> entriesL >=> entryL(entryId)).st |> liftS
      entry <- entryOp.toRightDisjunction(new Exception("Entry not found")) |> liftE
      _ <- ((bookL >=> entriesL).mods(_ - entryId)) |> liftS
    } yield entry

  def updateCategory(category: Category): BookServiceES[Category] =
    for {
      updatedCategory <- ((bookL >=> categoriesL).mods(_ + ((category.id, category)))) |> liftS
    } yield updatedCategory

  def removeCategory(categoryId: CategoryId): BookServiceES[Category] =
    for {
      _ <- failIfEntriesContainCategory(categoryId)
      categoryOp <- (bookL >=> categoriesL >=> categoryL(categoryId)).st |> liftS
      category <- categoryOp.toRightDisjunction(new Exception("Entry not found")) |> liftE
      _ <- ((bookL >=> categoriesL).mods(_ - categoryId)) |> liftS
    } yield category
}
