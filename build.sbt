val scala3Version = "3.2.2"
val CirceVersion = "0.14.5"

lazy val root = project
  .in(file("."))
  .settings(
    name := "dif",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "com.google.cloud" % "google-cloud-dataflow" % "0.17.0",
      "io.circe" %% "circe-core" % CirceVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "io.circe" %% "circe-parser" % CirceVersion,
      "org.scalameta" %% "munit" % "0.7.29" % Test
    )
  )
