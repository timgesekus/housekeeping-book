package net.gesekus.housekeeping.services

import net.gesekus.housekeeping.algebra.book.{Book, BookId}
import net.gesekus.housekeeping.algebra.category.{Category, CategoryId}
import net.gesekus.housekeeping.algebra.entry.{Entry, EntryId}
import scalaz.{EitherT, State, \/}


package object bookrepository {
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

  case class BookRepositoryState ()
  type BookRepositoryS[A]         = State[Book, A]
  type ET[F[_], A]             = EitherT[F, Throwable, A]
  type BookRepositoryES[A]        = ET[BookRepositoryS, A]
  type BookServiceStateType[A] = Throwable \/ A
}
