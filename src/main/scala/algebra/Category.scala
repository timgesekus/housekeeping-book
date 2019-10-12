package net.gesekus.housekeeping.algebra.category

final case class CategoryId(id: Int)
final case class CategoryTitle(title: String)
final case class Category(id: CategoryId, title: CategoryTitle)

trait Categories[F[_]] {
  def getCategory(id: CategoryId): F[Category]
  def addCategory(Category: Category): F[Category]
  def updateCategory(Category: Category): F[Category]
  def deleteCategory(id: CategoryId): F[Category]
}

object Category {}
