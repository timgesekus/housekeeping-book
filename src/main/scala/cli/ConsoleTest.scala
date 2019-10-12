package net

package gesekus

package housekeeping

package cli

/*
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
import net.gesekus.housekeeping.bookstorejson.BookStoreJson
import net.gesekus.housekeeping.algebra.book._
import net.gesekus.housekeeping.algebra.json.BookJson._
import scalaz.Maybe._
import scalaz.NonEmptyList
import net.gesekus.housekeeping.algebra.entry.EntryId
import argonaut._

import net.gesekus.housekeeping.algebra.category.CategoryId

 */
object MyApp {
  /*
 extends App {
  type Program[A] = ZIO[Console with BookStore, Throwable, A]

  def run(args: List[String]) =
    myAppLogic.provide(new Console.Live with BookStore.Live).fold(_ => 1, _ => 0)

  val myAppLogic = for {
       bookStoreJson <- bookstore.readBookStore("bookstore.json").onError(err => putStr(s"Laden hat nicht geklappt ${err.prettyPrint}"))
       books         <- ZIO.effect(BookStoreJson.fromJson(bookStoreJson))
       _             <- putStrLn(s"Books: $books")
       newBooks      <- ZIO.effect(add(books))
       _             <- putStrLn(newBooks.asJson.spaces2)
       _             <- putStrLn(s"Books: $newBooks")
       _              <- bookstore.writeBookStore("out.json", BookStoreJson.toJson(newBooks))
  } yield ()
  def add(books: List[net.gesekus.housekeeping.algebra.book.Book]): List[net.gesekus.housekeeping.algebra.book.Book] = {
    val newBook = net.gesekus.housekeeping.algebra.book.Book(BookId(1), BookTitle("Hallo"), just(NonEmptyList(EntryId(1))),just(NonEmptyList(CategoryId(1))))
    newBook :: books
  }
 */
}
