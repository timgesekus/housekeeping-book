package net.gesekus.housekeeping.main
import cats.Show
import pureconfig.generic.semiauto._
import pureconfig.ConfigConvert

object config {

  final case class Config(
    appConfig: AppConfig,
    apiConfig: ApiConfig
  )

  object Config {
    implicit val convert: ConfigConvert[Config] = deriveConvert
    implicit val show: Show[Config] = new Show[Config] {
      override def show(t: Config): String = t.toString
    }
  }

  final case class AppConfig(
    port: Int,
    baseUrl: String
  )
  final case class ApiConfig(endpoint: String)

  object AppConfig {
    implicit val convert: ConfigConvert[AppConfig] = deriveConvert
  }

  object ApiConfig {
    implicit val convert: ConfigConvert[ApiConfig] = deriveConvert
  }
}
