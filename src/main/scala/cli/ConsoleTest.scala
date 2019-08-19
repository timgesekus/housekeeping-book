import zio._
import java.nio.file.Paths
import zio._
import zio.nio.channels._
import zio.console._
import zio.stream._
import zio.Exit.Success
import zio.Task
import scala.io.Source
import java.io.IOException
import java.awt.print.Book
import bookstore._
import zio.Cause

object MyApp extends App {

  type Program[A] = ZIO[Console with BookStore, Throwable, A]

  def run(args: List[String]) =
    myAppLogic.provide(new Console.Live with BookStore.Live).fold(_ => 1, _ => 0)
  
  val myAppLogic = for {
       bookStoreJson <- bookstore.readBookStore("bookstore.json").onError(err => putStr(s"Laden hat nicht geklappt ${err.prettyPrint}"))
      _              <- bookstore.writeBookStore("out.json", bookStoreJson)
  } yield ()

}
