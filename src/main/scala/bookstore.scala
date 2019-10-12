package bookstore

import zio.ZIO

package object bookstore {
  final val bookStoreService: ZIO[BookStore, Nothing, BookStore.Service[Any]] =
    ZIO.access(_.bookStore)
  final def readBookStore(bookStoreFileName: String): ZIO[BookStore, Throwable, String] =
    ZIO.accessM(_.bookStore.readBookStore(bookStoreFileName))

  def writeBookStore(bookStoreFileName: String, bookStoreJson: String): ZIO[BookStore, Throwable, Unit] =
    ZIO.accessM(_.bookStore.writeBookStore(bookStoreFileName, bookStoreJson))
}
