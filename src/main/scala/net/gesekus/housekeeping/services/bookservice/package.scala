package net.gesekus.housekeeping.services

import net.gesekus.housekeeping.algebra.book.Book
import scalaz.{EitherT, State, \/}


package object bookservice  {
  case class BookServiceState(book: Book)
  type BookServiceS[A]         = State[BookServiceState, A]
  type ET[F[_], A]             = EitherT[F, Throwable, A]
  type BookServiceES[A]        = ET[BookServiceS, A]
  type BookServiceStateType[A] = Throwable \/ A
}
