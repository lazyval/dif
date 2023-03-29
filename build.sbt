val scala3Version = "3.2.2"
val CirceVersion  = "0.14.5"

lazy val root = project
  .in(file("."))
  .enablePlugins(ScoverageSbtPlugin)
  .settings(
    name         := "dif",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "com.google.cloud" % "google-cloud-dataflow" % "0.17.0",
      "io.circe"        %% "circe-core"            % CirceVersion,
      "io.circe"        %% "circe-generic"         % CirceVersion,
      "io.circe"        %% "circe-parser"          % CirceVersion,
      "org.scalatest"   %% "scalatest"             % "3.2.10" % Test,
      "org.scalactic"   %% "scalactic"             % "3.2.10" % Test,
      "org.scalacheck"  %% "scalacheck"            % "1.17.0" % Test
    )
  )
