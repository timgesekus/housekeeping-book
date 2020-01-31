package net.gesekus.housekeeping

import zio.{ App, IO, ZIO }
import ZIO._

object Playground extends App {

  def process(): IO[Exception, Int] =
    for {
      _ <- succeed(10)
      _ <- fail(new IllegalStateException())
      a <- succeed(14)
    } yield a

  def main(): IO[Exception, Int] =
    for {
      i <- process
    } yield i

  override def run(args: List[String]) = main.fold(_ => 1, a => a)
}
