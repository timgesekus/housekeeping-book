package net.gesekus.housekeeping

import zio.{ App, IO, Task, ZIO }
import zio.ZIO._

object Playground extends App {

  def process(): IO[Exception, Int] =
    for {
      a <- succeed(10)
      _ <- fail(new IllegalStateException())
      a <- succeed(14)
    } yield a

  def main(): IO[Exception, Int] =
    for {
      i <- process
    } yield i

  override def run(args: List[String]): ZIO[Playground.Environment, Nothing, Int] = main.fold(e => 1, a => a)
}
