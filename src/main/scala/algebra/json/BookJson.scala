package net.gesekus.housekeeping.algebra.json

import argonaut._
import Argonaut._
import CategoryJson._
import EntryJson._
import net.gesekus.housekeeping._
import algebra.book.{Book, BookId, BookTitle}
import algebra.category.{Category, CategoryId}
import algebra.entry.{Entry, EntryId}
import argonaut.ArgonautHelper._

object BookJson {
  implicit def BookIdCodec: CodecJson[BookId] =
    casecodec1(BookId.apply, BookId.unapply)("id")

  implicit def TitleCodec: CodecJson[BookTitle] =
    casecodec1(BookTitle.apply, BookTitle.unapply)("title")

  implicit val entriesMapDecode=MapKVDecodeJson[EntryId,Entry]
  implicit val categoriesMapDecode=MapKVDecodeJson[CategoryId,Category]
  implicit val entryIdKeyEncode: EncodeJsonKey[EntryId] = EncodeJsonKey.from(_.id.toString)
  implicit val categoryIdKeyEncode: EncodeJsonKey[CategoryId] = EncodeJsonKey.from(_.id.toString)
  implicit def bookCodecJson: CodecJson[Book] =
    casecodec4(Book.apply, Book.unapply)("id", "title", "entries", "categories")
}
