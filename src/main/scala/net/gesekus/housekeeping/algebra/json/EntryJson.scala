package net.gesekus.housekeeping.algebra.json

import java.time.LocalDateTime

import argonaut.Argonaut.{ casecodec1, casecodec5, jString }
import argonaut.CodecJson
import net.gesekus.housekeeping.algebra.entry.{ Entry, EntryId, EntryTitle }
import net.gesekus.housekeeping.algebra.json.CategoryJson.categoryIdCodec

object EntryJson {
  implicit def LocalDateTimeCodecJson: CodecJson[LocalDateTime] =
    CodecJson(
      (d: LocalDateTime) => jString(d.toString()),
      c =>
        for {
          date <- c.as[String]
        } yield (LocalDateTime.parse(date))
    )

  implicit def entryIdCodec: CodecJson[EntryId] =
    casecodec1(EntryId.apply, EntryId.unapply)("id")

  implicit def entryTitleCodec: CodecJson[EntryTitle] =
    casecodec1(EntryTitle.apply, EntryTitle.unapply)("title")

  implicit def entryCodecJson: CodecJson[Entry] =
    casecodec5(Entry.apply, Entry.unapply)("id", "title", "amount", "date", "categories")
}
