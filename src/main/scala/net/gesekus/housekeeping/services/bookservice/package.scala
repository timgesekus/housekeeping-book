package net.gesekus.housekeeping.services

import net.gesekus.housekeeping.algebra.book.Book
import net.gesekus.housekeeping.algebra.category.CategoryId
import net.gesekus.housekeeping.algebra.entry.EntryId
import scalaz.{EitherT, State, \/}

package object bookservice {
  case class NextIds(categoryId: CategoryId, entryId: EntryId)
  case class BookServiceState(book: Book, nextIds: NextIds)

  type BookServiceS[A] = State[BookServiceState, A]
  type ET[F[_], A]     = EitherT[F, Throwable, A]
  type BookServiceES[A] = ET[BookServiceS, A]
  type BookServiceStateType[A] = Throwable \/ A
}
