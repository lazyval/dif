val scala3Version = "3.2.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "dif",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,
    Compile / mainClass := Some("space.kostya.dif.Cli"),
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "0.7.29" % Test,
      "com.github.scopt" %% "scopt" % "4.1.0"
    )
  )
