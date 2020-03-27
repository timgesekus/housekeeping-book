package net.gesekus.housekeeping.main

import cats.effect.ExitCode
import pureconfig.ConfigSource
import config._
import net.gesekus.housekeeping.http.Routes
import net.gesekus.housekeeping.log.Slf4jLogger
import net.gesekus.housekeeping.log._
import org.http4s.Http
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.CORS
import zio.interop.catz._
import zio._
import zio.clock.Clock
import zio.console._
import zio.interop.catz._
import cats.implicits._
import net.gesekus.housekeeping.services.bookactorqueue.BookActorQueue
import net.gesekus.housekeeping.services.bookactorqueue
import net.gesekus.housekeeping.services.bookrepository.{BookActor, BookCommand}
import net.gesekus.housekeeping.services.eventpublisher.{EventPublisher, InMemoryEventPublisher}
import net.gesekus.housekeeping.services.eventstore.{EventStore, InMemoryEvenStore}


trait AppEnvironment
    extends BookActorQueue
    with Console
    with Clock
    with Slf4jLogger
    with EventStore
    with EventPublisher
    with BookActor

object Main extends App {
  type AppTask[A] = RIO[AppEnvironment, A]
  def httpApp: Http[AppTask, AppTask] = Router[AppTask]("/" -> Routes.testRoutes).orNotFound

  def runHttp(port: Int) =
    for {
      server <- ZIO
        .runtime[AppEnvironment]
        .flatMap { implicit rts =>
          BlazeServerBuilder[AppTask]
            .bindHttp(port, "0.0.0.0")
            .withHttpApp(CORS(httpApp))
            .serve
            .compile
            .drain.as(ExitCode.Success)
        }
    } yield server

  def runQueue[R <: BookActorQueue](): ZIO[R, Throwable, Unit] =
    for {
      _ <- bookactorqueue.run
    } yield ()

  def main =
    for {
      cfg <- ZIO.fromEither(ConfigSource.default.load[Config])
      _ <- info(cfg)
      _ <- info("Loaded Config")
      _ <- runQueue()
      f1 <- runHttp(cfg.appConfig.port).fork
      _ <- info("Server started")
      _ <- f1.join
    } yield ZIO.unit

  def run(args: List[String]) =
    for {
      queue <- Queue.bounded[(BookCommand, Promise[Throwable, Boolean])](100)
      env = new AppEnvironment with BookActorQueue.Live with Console.Live with Clock.Live with Slf4jLogger
      with InMemoryEvenStore with InMemoryEventPublisher with BookActor.Live {
        override val commandQueue: Queue[(BookCommand, Promise[Throwable, Boolean])] = queue
      }
      out <- main
        .provide(env)
        .fold(e => {System.err.println(e); 1}, _ => 0)
    } yield out
}
