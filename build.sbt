val scala3Version = "3.2.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "dif",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "com.google.cloud" % "google-cloud-dataflow" % "0.17.0",
      "org.scalameta" %% "munit" % "0.7.29" % Test
    )
  )
