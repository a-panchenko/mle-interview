name := "order-status-recommender-api"

version := "0.1"

scalaVersion := "2.13.6"


val versions = new {
  val akka = "2.6.16"
  val akkaHttp = "10.2.6"
  val circe = "0.14.1"
  val akkaHttpCirce = "1.38.2"
  val scalaLogging = "3.9.4"
  val logback = "1.2.3"
  val config = "1.4.1"

  // test
  val scalatest = "3.2.10"
}

val dependencies = Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % versions.akka,
  "com.typesafe.akka" %% "akka-stream" % versions.akka,
  "com.typesafe.akka" %% "akka-http" % versions.akkaHttp,

  "io.circe" %% "circe-core" % versions.circe,
  "io.circe" %% "circe-generic-extras" % versions.circe,
  "io.circe" %% "circe-parser" % versions.circe,

  "de.heikoseeberger" %% "akka-http-circe" % versions.akkaHttpCirce,

  "com.typesafe" % "config" % versions.config,
  "com.typesafe.scala-logging" %% "scala-logging" % versions.scalaLogging,
  "ch.qos.logback" % "logback-classic" % versions.logback
)

val testDependencies = Seq(
  "org.scalatest" %% "scalatest" % versions.scalatest % Test
)

libraryDependencies ++= dependencies ++ testDependencies

assembly / assemblyJarName := name.value + ".jar"
assembly / assemblyMergeStrategy := {
  case x if x.endsWith(".css") => MergeStrategy.last
  case x if x.endsWith(".properties") => MergeStrategy.concat
  case x if x.endsWith(".class") => MergeStrategy.last
  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}
