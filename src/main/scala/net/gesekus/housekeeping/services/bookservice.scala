package net.gesekus.housekeeping.services

import net.gesekus.housekeeping.algebra.book.Book
import net.gesekus.housekeeping.algebra.category.CategoryId
import net.gesekus.housekeeping.algebra.entry.EntryId
import net.gesekus.housekeeping.services.book.BookCommand
import scalaz.{EitherT, State, \/}
import zio._

package object bookservice extends BookActorQueue.Service[BookActorQueue] {
  case class NextIds(categoryId: CategoryId, entryId: EntryId)

  case class BookServiceState(book: Book)

  type BookServiceS[A]         = State[BookServiceState, A]
  type ET[F[_], A]             = EitherT[F, Throwable, A]
  type BookServiceES[A]        = ET[BookServiceS, A]
  type BookServiceStateType[A] = Throwable \/ A

  //override def put(bookCommand: BookCommand): Task[Boolean] = ZIO.accessM(_.bookActorQueue.put(bookCommand))
  override def put(bookCommand: BookCommand): ZIO[BookActorQueue,Throwable,Boolean] = ZIO.accessM(_.bookActorQueue.put(bookCommand))

  override def run(): ZIO[BookActorQueue, Throwable, Unit] = ZIO.accessM(_.bookActorQueue.run())
}
