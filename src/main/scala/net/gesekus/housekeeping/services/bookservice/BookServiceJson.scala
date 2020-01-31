package net.gesekus.housekeeping.services.bookservice

import argonaut._
import Argonaut._
import net.gesekus.housekeeping.services.book.{
  AddCategory,
  AddEntry,
  AddEntryToCategory,
  BookCreated,
  CategoryAdded,
  CreateBook,
  EntryAdded,
  EntryAddedToCategory
}
import net.gesekus.housekeeping.algebra.json.BookJson._
import net.gesekus.housekeeping.algebra.json.EntryJson._
import net.gesekus.housekeeping.algebra.json.CategoryJson._

object BookServiceJson {
  // Commands
  implicit def CreateBookCodec: CodecJson[CreateBook] =
    casecodec1(CreateBook.apply, CreateBook.unapply)("book")

  implicit def AddEntryCodec: CodecJson[AddEntry] =
    casecodec2(AddEntry.apply, AddEntry.unapply)("bookid", "entry")

  implicit def AddCategoryCodec: CodecJson[AddCategory] =
    casecodec2(AddCategory.apply, AddCategory.unapply)("bookid", "category")

  implicit def AddEntryToCategoryCodec: CodecJson[AddEntryToCategory] =
    casecodec3(AddEntryToCategory.apply, AddEntryToCategory.unapply)("bookid", "entryId", "categoryId")

  //Events
  implicit def BookCreatedCodec: CodecJson[BookCreated] =
    casecodec1(BookCreated.apply, BookCreated.unapply)("book")

  implicit def EntryAddedCodec: CodecJson[EntryAdded] =
    casecodec2(EntryAdded.apply, EntryAdded.unapply)("bookid", "entry")

  implicit def CategoryAddedCodec: CodecJson[CategoryAdded] =
    casecodec2(CategoryAdded.apply, CategoryAdded.unapply)("bookid", "category")

  implicit def EntryAddedToCategoryCodec: CodecJson[EntryAddedToCategory] =
    casecodec3(EntryAddedToCategory.apply, EntryAddedToCategory.unapply)("bookid", "entryId", "categoryId")

}
