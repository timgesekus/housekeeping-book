package net.gesekus.housekeeping.algebra.json

import argonaut.Argonaut.{casecodec1, casecodec2}
import argonaut.CodecJson
import net.gesekus.housekeeping.algebra.category.{Category, CategoryId, CategoryTitle}

object CategoryJson {
  implicit def categoryIdCodec: CodecJson[CategoryId] =
    casecodec1(CategoryId.apply, CategoryId.unapply)("id")

  implicit def categoryTitleCodec: CodecJson[CategoryTitle] =
    casecodec1(CategoryTitle.apply, CategoryTitle.unapply)("title")

  implicit def categoryCodecJson: CodecJson[Category] =
    casecodec2(Category.apply, Category.unapply)("id", "title")
}
