package bookstore

import java.io.{ File, IOException, PrintWriter }

import zio.{ Task, UIO, ZIO }

import scala.io.Source

trait BookStore extends Serializable {
  val bookStore: BookStore.Service[Any]
}

object BookStore extends Serializable {

  trait Service[R] {
    def readBookStore(bookStoreFileName: String): ZIO[R, Throwable, String]
    def writeBookStore(bookStoreFileName: String, bookStoreJson: String): ZIO[R, Throwable, Unit]
  }

  trait Live extends BookStore {

    val bookStore: Service[Any] = new Service[Any] {

      final def readBookStore(bookStoreFileName: String): Task[String] =
        for {
          content <- Task(Source.fromFile(bookStoreFileName)).bracket(closeSource)(convertToString)
        } yield content

      final def writeBookStore(bookStoreFileName: String, bookStoreJson: String): Task[Unit] =
        for {
          bookStroreFile <- Task.effect(new File(bookStoreFileName))
          _              <- Task(new PrintWriter(bookStroreFile)).bracket(closeWriter)(write(_, bookStoreJson))
        } yield ()

      private def closeSource(source: Source) =
        UIO(source.close)

      private def closeWriter(writer: PrintWriter) =
        UIO(writer.close())

      private def convertToString(source: Source) =
        Task.effect(source.getLines().mkString).refineToOrDie[IOException]

      private def write(writer: PrintWriter, content: String) =
        Task.effect(writer.write(content))
    }
  }
  object Live extends Live
}
