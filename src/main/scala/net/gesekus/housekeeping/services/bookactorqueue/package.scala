package net.gesekus.housekeeping.services

import net.gesekus.housekeeping.services.book.BookCommand
import zio.ZIO

package object bookactorqueue extends BookActorQueue.Service[BookActorQueue] {
  override def put(bookCommand: BookCommand): ZIO[BookActorQueue,Throwable,Boolean] = ZIO.accessM(_.bookActorQueue.put(bookCommand))
  override def run(): ZIO[BookActorQueue, Throwable, Unit] = ZIO.accessM(_.bookActorQueue.run())
}
