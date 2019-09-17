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

libraryDependencies ++= Seq (
  "dev.zio" %% "zio" % "1.0.0-RC11-1",
  "dev.zio" %% "zio-nio" % "0.1.2-SNAPSHOT",
  "io.argonaut" %% "argonaut-scalaz" % "6.2.3" ,
  "org.scalatest" %% "scalatest" % "3.0.8" % "test"
)


//val derivingVersion = "1.0.0"
//libraryDependencies ++= Seq(
//  "org.scalaz" %% "deriving-macro" % derivingVersion % "provided",
//  compilerPlugin("org.scalaz" %% "deriving-plugin" % derivingVersion),
//  "org.scalaz" %% "scalaz-deriving"            % derivingVersion,
//  "org.scalaz" %% "scalaz-deriving-magnolia"   % derivingVersion,
//  "org.scalaz" %% "scalaz-deriving-scalacheck" % derivingVersion,
//  "org.scalaz" %% "scalaz-deriving-jsonformat" % derivingVersion
//)

//scalacOptions ++= Seq(
//  "-language:_",
//  "-unchecked",
 // "-explaintypes",
//  "-Ywarn-value-discard",
 // "-Ywarn-numeric-widen",
//  "-Ypartial-unification",
//  "-Xlog-free-terms",
//  "-Xlog-free-types",
//  "-Xlog-reflective-calls",
//  "-Yrangepos",
  //"-Yno-imports",
  //"-Yno-predef",
 // "-Ywarn-unused:explicits,patvars,imports,privates,locals,implicits",
 // "-opt:l:method,inline",
 // "-opt-inline-from:scalaz.**"
//)


lazy val housekeepingbook = (project in file("."))
  .settings(
    name := "Housekeeping book"
  )