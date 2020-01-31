package net.gesekus.housekeeping.services.book

import net.gesekus.housekeeping.algebra.book.{Book, BookId, BookTitle}
import net.gesekus.housekeeping.algebra.category.{Category, CategoryId}
import net.gesekus.housekeeping.algebra.entry.{Entry, EntryId}
import net.gesekus.housekeeping.algebra.lens._
import net.gesekus.housekeeping.services.bookservice._
import scalaz.Scalaz._
import scalaz._
import BookServiceES._

sealed class BookCommand
final case class CreateBook(book: Book) extends BookCommand
final case class AddEntry(bookId: BookId, entry: Entry) extends BookCommand
final case class AddCategory(bookId: BookId, category: Category) extends BookCommand
final case class AddEntryToCategory(bookId: BookId, entryId: EntryId, categoryId: CategoryId) extends BookCommand

sealed class BookEvents
final case class BookCreated(book: Book) extends BookEvents
final case class EntryAdded(bookId: BookId, entry: Entry) extends BookEvents
final case class CategoryAdded(bookId: BookId, category: Category) extends BookEvents
final case class EntryAddedToCategory(bookId: BookId, entryId: EntryId, categoryId: CategoryId) extends BookEvents

trait BookStore {
  def addEntry(entry: Entry): BookServiceES[Entry]

  def addCategory(category: Category): BookServiceES[Category]
  def addCategoriesToEntry(entryId: EntryId, categoryIds: Set[CategoryId]): BookServiceES[Entry]

  def removeEntry(entryId: EntryId): BookServiceES[Entry]

  def removeCategoriesFromEntry(entryId: EntryId, categoryIds: Set[CategoryId]): BookServiceES[Entry]
}

object BookStore extends BookStore {

  def init(bookId: BookId, title: BookTitle) = {
    val book = Book.init(bookId, title)
    BookServiceState(book)
  }

  val bookL =
    Lens.lensu[BookServiceState, Book]((bookServiceState, newVal) => bookServiceState.copy(book = newVal), _.book)

  def entryL(entryId: EntryId) = Lens.mapVLens[EntryId, Entry](entryId)

  def toOpLens[A, B](lens: Lens[A, B]): Lens[Option[A], Option[B]] = ???

  def handleEvent(event: BookEvents): BookServiceES[Unit] =
    for {
      _ <- event match {
        case EntryAdded(_, entry)                         => addEntry(entry)
        case CategoryAdded(_, category)                   => addCategory(category)
        case EntryAddedToCategory(_, entryId, categoryId) => addCategoriesToEntry(entryId, Set(categoryId))

      }
    } yield ()

  def handleEvents(events: Seq[BookEvents]): BookServiceES[Unit] =
    liftS(State(state => {
      val newState = events.foldLeft(state)((state, event) => handleEvent(event).run.exec(state))
      (newState, ())
    }))

  def handleCommand(command: BookCommand): BookServiceES[Seq[BookEvents]] =
    for {
      events <- command match {
        case AddEntry(bookId, entry)       => liftV(Seq(EntryAdded(bookId, entry)))
        case AddCategory(bookId, category) => liftV(Seq(CategoryAdded(bookId, category)))
        case AddEntryToCategory(bookId, entryId, categoryId) =>
          for {
            _ <- checkIfEntriesExists(Set(entryId))
            _ <- checkIfCategoriesExist(Set(categoryId))
            events <- Seq(EntryAddedToCategory(bookId, entryId, categoryId)) |> liftV
          } yield events

      }
    } yield events

  private def checkIfCategoriesExist(categoryIds: Set[CategoryId]): BookServiceES[Boolean] = {
    import BookLens.categoriesL
    val stateFunc: BookServiceS[BookServiceStateType[Boolean]] = for {
      categories <- bookL >=> categoriesL
      allExist <- state(categoryIds.forall(categories.contains(_)))
    } yield if (allExist) true.right else new Exception("At least one category did not exist").left
    BookServiceES.apply(stateFunc)
  }

  private def checkIfEntriesExists(entryIds: Set[EntryId]): BookServiceES[Boolean] = {
    import BookLens.entriesL
    val stateFunc: BookServiceS[BookServiceStateType[Boolean]] = for {
      entries <- bookL >=> entriesL
      allExist <- state(entryIds.forall(entries.contains(_)))
    } yield if (allExist) true.right else new Exception("At least one entry did not exist").left
    BookServiceES.apply(stateFunc)
  }

  def addCategories(categoriesToAdd: Set[CategoryId]): Entry => Entry =
    EntryLens.categoriesL.mod(oldCats => (categoriesToAdd ++ oldCats), _)

  def removeCategories(categoriesToRemove: Set[CategoryId]): Entry => Entry =
    EntryLens.categoriesL.mod(oldCats => (oldCats -- categoriesToRemove), _)

  def addCategoriesToEntry(entryId: EntryId, categoryIds: Set[CategoryId]): BookServiceES[Entry] = {
    import BookLens.entriesL
    import BookServiceES.{ liftE, liftS }
    for {
      _ <- checkIfEntriesExists(Set[EntryId](entryId))
      _ <- checkIfCategoriesExist(categoryIds)
      newEntryOp <- (bookL >=> entriesL >=> entryL(entryId)).mods(addCategories(categoryIds).lift) |> liftS
      newEntry <- Maybe.fromOption(newEntryOp).toRight[Throwable](new Exception("Cloud nod add entries")) |> liftE
    } yield newEntry
  }

  def addEntry(entry: Entry): DisjunctionT[BookServiceS, Throwable, Entry] = {
    import BookLens.entriesL
    import BookServiceES._
    for {
      entryCategories <- entry.categories |> liftV
      _ <- checkIfCategoriesExist(entryCategories)
      _ <- (bookL >=> entriesL >=> entryL(entry.id)).mods(_ => entry.some) |> liftS
    } yield entry

  }

  def addCategory(category: Category): BookServiceES[Category] = {
    import BookLens.categoriesL
    import BookServiceES._

    for {
      _ <- (bookL >=> categoriesL >=> Lens.mapVLens(category.id)).mods(_ => category.some) |> liftS
    } yield category
  }

  def removeEntry(entryId: EntryId): BookServiceES[Entry] = {
    import BookLens.entriesL
    import BookServiceES._
    for {
      _ <- checkIfEntriesExists(Set(entryId))
      removedEntry <- (bookL >=> entriesL).at(entryId).st |> liftS
      _ <- (bookL >=> entriesL -= entryId) |> liftS
    } yield removedEntry
  }

  def removeCategoriesFromEntry(entryId: EntryId, categoryIds: Set[CategoryId]): BookServiceES[Entry] = {
    import BookLens.entriesL
    import BookServiceES._
    for {
      _ <- checkIfEntriesExists(Set(entryId))
      newEntryOp <- (bookL >=> entriesL >=> entryL(entryId)).mods(removeCategories(categoryIds).lift) |> liftS
      newEntry <- Maybe
        .fromOption(newEntryOp)
        .toRight[Throwable](new Exception("Could not remove categories")) |> liftE[Entry]
    } yield newEntry
  }

  def removeCategoriesFromAllEntries(categoryId: CategoryId): BookServiceES[Category] =
    BookServiceES.liftS(State(s => {
      val entryIds = s.book.entries.keys
      val newState = entryIds.foldLeft(s)(
        (oldState, entryId) => removeCategoriesFromEntry(entryId, Set(categoryId)).run.exec(oldState)
      )

      (newState, null)
    }))

  def removeCategory(categoryId: CategoryId): BookServiceES[Category] = {
    import BookLens.categoriesL
    import BookServiceES._
    for {
      _ <- checkIfCategoriesExist(Set(categoryId))
      removedCategory <- (bookL >=> categoriesL).at(categoryId).st |> liftS
      _ <- (bookL >=> categoriesL -= categoryId) |> liftS
      _ <- removeCategoriesFromAllEntries(categoryId)
    } yield removedCategory
  }

}
