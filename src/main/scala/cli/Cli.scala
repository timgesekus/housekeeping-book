package net

package gesekus

package housekeeping

package cli

import net.gesekus.housekeeping.algebra.book._

trait Cli[F[_]] {
  def createBook(title: String): F[Book]
}
/* final class CliModule[F[_]: Monad](B: Books[F])
extends Cli[F] {
    def createBook(title: String): F[Book] = B.addBook(Book(BookId(1),Title(title),empty))
} */
