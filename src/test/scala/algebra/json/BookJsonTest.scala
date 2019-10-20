package net.gesekus.housekeeping.algebra.json

import org.scalatest.FlatSpec
import argonaut._

import BookJson._
import net.gesekus.housekeeping.algebra._
import book.{ Book, BookId, BookTitle }
import json.BookJson._

class BookJsonTest extends FlatSpec {
  "A BookJson" should "should encode and decode an empty book" in {
    val emptyBook      = Book.init(BookId("One"), BookTitle("My Book"))
    val bookJson       = bookCodecJson.encode(emptyBook)
    val bookJsonString = bookJson.spaces2
    val decodedBookEt  = Parse.decodeEither[Book](bookJsonString)
    assert(decodedBookEt.isRight)
    val decodedBook = decodedBookEt.right.get
    assert(emptyBook === decodedBook)
  }
  it should "have a new book after adding one" in {}
}
