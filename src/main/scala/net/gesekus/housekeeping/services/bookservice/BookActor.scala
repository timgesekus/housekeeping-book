package net.gesekus.housekeeping.services.bookservice


import net.gesekus.housekeeping.services.book.{BookCommand, BookEvents}
import zio.{RIO, Task, ZIO}

trait BookActor {
  def bookActor: BookActor.Service
}

object BookActor {

  trait Service {
    def restore: Task[BookServiceState]
    def handleCommand(bookCommand: BookCommand) : Task[Seq[BookEvents]]
    def applyEvents(events: Seq[BookEvents]): Task[BookServiceState]
    def publishEvents(events: Seq[BookEvents]): Task[Unit]
  }
}
