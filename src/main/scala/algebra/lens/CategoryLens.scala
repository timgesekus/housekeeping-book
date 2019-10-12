package net.gesekus.housekeeping.algebra.lens

import net.gesekus.housekeeping.algebra.category.{ Category, CategoryId, CategoryTitle }
import scalaz.Lens

object CategoryLens {
  def idL    = Lens.lensu[Category, CategoryId]((Category, newVal) => Category.copy(id = newVal), _.id)
  def titleL = Lens.lensu[Category, CategoryTitle]((Category, newVal) => Category.copy(title = newVal), _.title)
  def valIdL = Lens.lensu[CategoryId, Int]((CategoryId, newVal) => CategoryId.copy(id = newVal), _.id)
}
