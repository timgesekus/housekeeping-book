package net.gesekus.housekeeping.main
import pureconfig.generic.semiauto._
import pureconfig.ConfigConvert

object config {

  final case class Config(
    appConfig: AppConfig,
    apiConfig: ApiConfig
  )

  object Config {
    implicit val convert: ConfigConvert[Config] = deriveConvert
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
