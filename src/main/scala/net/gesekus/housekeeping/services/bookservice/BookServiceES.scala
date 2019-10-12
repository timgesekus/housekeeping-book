package net.gesekus.housekeeping.services.bookservice

import scalaz.Scalaz._
import scalaz.{ State, _ }

object BookServiceES {
  def apply[A](s: BookServiceS[BookServiceStateType[A]]): BookServiceES[A] = EitherT(s)
  def liftE[A](e: Throwable \/ A): BookServiceES[A]                        = apply(e.point[BookServiceS])
  def liftS[A](s: BookServiceS[A]): BookServiceES[A]                       = MonadTrans[ET].liftM(s)

  def liftV[A](a: A): BookServiceES[A] = {
    val state = State[BookServiceState, A] { s =>
      (s, a)
    }
    liftS(state)
  }
}
