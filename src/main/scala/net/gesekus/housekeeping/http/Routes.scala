package net.gesekus.housekeeping.http

import java.time.LocalDateTime

import io.circe.{ Decoder, Encoder }
import io.circe.generic.semiauto.{ deriveDecoder, deriveEncoder }
import zio.{ Task, TaskR, ZIO }
import org.http4s.dsl.Http4sDsl
import org.http4s.HttpRoutes
import io.circe.{ Encoder, Json }
import org.http4s.EntityEncoder
import org.http4s.circe._
import io.circe.{ Decoder, Encoder }
import org.http4s._
import org.http4s.circe._
import cats.implicits._
import net.gesekus.housekeeping.algebra.book.BookId
import net.gesekus.housekeeping.algebra.category.{ Category, CategoryId, CategoryTitle }
import net.gesekus.housekeeping.algebra.entry
import net.gesekus.housekeeping.algebra.entry.{ Entry, EntryId, EntryTitle }
import net.gesekus.housekeeping.log.Log
import net.gesekus.housekeeping.services.book.{ AddCategory, AddEntry, AddEntryToCategory }
import net.gesekus.housekeeping.services.bookservice._
import zio.interop.catz._
import net.gesekus.housekeeping.log._

object Routes {

  def testRoutes[R <: BookActorQueue with Log]: HttpRoutes[TaskR[R, ?]] = {
    type RoutesTask[A] = TaskR[R, A]

    val dsl: Http4sDsl[RoutesTask] = Http4sDsl[RoutesTask]
    import dsl._
    //implicit def circeJsonDecoder[A](implicit decoder: Decoder[A]): EntityDecoder[RoutesTask, A] = jsonOf[RoutesTask, A]

    //implicit def circeJsonEncoder[A](implicit encoder: Encoder[A]): EntityEncoder[RoutesTask, A] =jsonEncoderOf[RoutesTask, A]

    HttpRoutes.of[RoutesTask] {
      case GET -> Root / "book" / id =>
        for {
          _ <- info("In get book")
          response <- put(AddEntryToCategory(BookId(id), EntryId("test"), CategoryId("test"))).foldM(
            e => BadRequest(e.toString),
            _ => Ok(s"Got book, $id.")
          )
        } yield response
      case GET -> Root / "entry" / id =>
        for {
          _ <- info("In get book")
          _ <- put(
            AddEntry(
              BookId(id),
              Entry(
                entry.EntryId("TEST1"),
                entry.EntryTitle("Entry Title"),
                10.0,
                LocalDateTime.now(),
                Set[CategoryId]()
              )
            )
          )
          response <- Ok(s"Got book, $id.")
        } yield response
    }
  }
}
