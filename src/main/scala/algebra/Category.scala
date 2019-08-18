package net
package gesekus
package housekeeping
package algebra
package category 


import java.lang.String
import scala.Int

final case class CategoryId(id: Int)
final case class Title(test: String)
final case class Category(id: CategoryId, title: Title )

trait Categories[F[_]] {
    def getCategory(id: CategoryId) : F[Category]
    def addCategory(Category: Category) : F[Category]
    def updateCategory (Category: Category) : F[Category]
    def deleteCategory (id: CategoryId) : F[Category]
}