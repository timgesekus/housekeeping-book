ThisBuild / scalaVersion := "2.12.8"
ThisBuild / organization := "net.gesekus"

//val catsVersion = "2.0.0-RC1" // depends on cats 2.0.0-RC1

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.28"

//libraryDependencies ++= Seq(
//"com.github.mpilquist"  %% "simulacrum"          % "0.13.0",
// "com.chuusai"           %% "shapeless"           % "2.3.3",
// "eu.timepit"            %% "refined-scalaz"      % "0.9.2",
// "com.propensive"        %% "contextual"          % "1.1.0",
//  "org.scalatest"         %% "scalatest"           % "3.0.5" % "test,it",
//  "com.github.pureconfig" %% "pureconfig"          % "0.9.1",
//  "org.http4s"            %% "http4s-dsl"          % http4sVersion,
//  "org.http4s"            %% "http4s-blaze-server" % http4sVersion,
//  "org.http4s"            %% "http4s-blaze-client" % http4sVersion,
// and because we're using http4s, all the compat stuff too...
//  "com.codecommit" %% "shims"                % "1.4.0",
//  "org.scalaz"     %% "scalaz-ioeffect-cats" % "2.10.1"
//)

val zioVersion = "1.0.0-RC9-4"
val zioTestVersion = "1.0.0-RC16"
val zioNioVersion = "0.2.1"

val Http4sVersion = "0.20.1"
val CirceVersion = "0.12.0-M1"
val DoobieVersion = "0.7.0-M5"

libraryDependencies ++= Seq(
  "io.argonaut" %% "argonaut-scalaz" % "6.2.3" ,
  "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s" %% "http4s-circe" % Http4sVersion,
  "org.http4s" %% "http4s-dsl" % Http4sVersion,
  "io.circe" %% "circe-core" % CirceVersion,
  "io.circe" %% "circe-generic" % CirceVersion,
  "io.circe" %% "circe-literal" % CirceVersion % "test",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.github.pureconfig" %% "pureconfig" % "0.12.1",
  "dev.zio" %% "zio" % "1.0.0-RC9-4",
  "dev.zio" %% "zio-test" % zioTestVersion % "test",
  "dev.zio" %% "zio-test-sbt" % zioTestVersion % "test",
  "dev.zio" %% "zio-interop-cats" % "1.3.1.0-RC3",
  "dev.zio" %% "zio-delegate" % "0.0.3",
  "com.lihaoyi" %% "sourcecode" % "0.1.7",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  compilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4"),
  compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.0-M4"),
  compilerPlugin(("org.scalamacros" % "paradise" % "2.1.1").cross(CrossVersion.full))
)

testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
//val derivingVersion = "1.0.0"
//libraryDependencies ++= Seq(
//  "org.scalaz" %% "deriving-macro" % derivingVersion % "provided",
//  compilerPlugin("org.scalaz" %% "deriving-plugin" % derivingVersion),
//  "org.scalaz" %% "scalaz-deriving"            % derivingVersion,
//  "org.scalaz" %% "scalaz-deriving-magnolia"   % derivingVersion,
//  "org.scalaz" %% "scalaz-deriving-scalacheck" % derivingVersion,
//  "org.scalaz" %% "scalaz-deriving-jsonformat" % derivingVersion
//)

scalacOptions ++= Seq(
  "-language:_",
  "-unchecked",
  "-explaintypes",
  "-Ywarn-value-discard",
  "-Ywarn-numeric-widen",
  "-Ypartial-unification",
  "-Xlog-free-terms",
  "-Xlog-free-types",
  "-Xlog-reflective-calls",
  "-Yrangepos",
  //"-Yno-imports",
  //"-Yno-predef",
  "-Ywarn-unused:explicits,patvars,imports,privates,locals,implicits",
  "-opt:l:method,inline",
  "-opt-inline-from:scalaz.**"
)

lazy val housekeepingbook = (project in file("."))
  .settings(
    name := "Housekeeping book"
  )
