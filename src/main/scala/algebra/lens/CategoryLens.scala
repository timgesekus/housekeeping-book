package net.gesekus.housekeeping.algebra.lens

import net.gesekus.housekeeping.algebra.category.{ Category, CategoryId, CategoryTitle }
import scalaz.Lens

object CategoryLens {
  def idL: Lens[Category, CategoryId]       = Lens.lensu((Category, newVal) => Category.copy(id = newVal), _.id)
  def titleL: Lens[Category, CategoryTitle] = Lens.lensu((Category, newVal) => Category.copy(title = newVal), _.title)
  def valIdL: Lens[CategoryId, String]      = Lens.lensu((CategoryId, newVal) => CategoryId.copy(id = newVal), _.id)
}
