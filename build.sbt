val scala3Version    = "3.3.0"
val CirceVersion     = "0.14.5"
val ScalaTestVersion = "3.2.16"

lazy val root = project
  .in(file("."))
  .enablePlugins(ScoverageSbtPlugin)
  .enablePlugins(NativeImagePlugin)
  .settings(
    name                 := "dif",
    version              := "0.1.0-SNAPSHOT",
    scalaVersion         := scala3Version,
    doctestTestFramework := DoctestTestFramework.ScalaTest,
    libraryDependencies ++= Seq(
      "com.google.cloud" % "google-cloud-dataflow" % "0.22.0",
      "io.circe"        %% "circe-core"            % CirceVersion,
      "io.circe"        %% "circe-generic"         % CirceVersion,
      "io.circe"        %% "circe-parser"          % CirceVersion,
      "org.scalatest"   %% "scalatest"             % ScalaTestVersion % Test,
      "org.scalactic"   %% "scalactic"             % ScalaTestVersion % Test,
      // scalacheck magnolify can be used for automatic derivation of arbitrary instances
      // but sadly it doesn't work with scala 3 yet, see https://github.com/spotify/magnolify/pull/676
      // "com.spotify"     %% "magnolify-scalacheck"  % "0.22.0",
      "org.gnieh"                  %% "diffson-circe"   % "4.4.0",
      "org.jline"                   % "jline"           % "3.23.0",
      "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.5",
      "ch.qos.logback"              % "logback-classic" % "1.4.7",
      "org.scalacheck"             %% "scalacheck"      % "1.17.0" % Test,
      "com.github.erosb"            % "json-sKema"      % "0.7.0"
    ),
    nativeImageVersion := "22.3.1",
    // https://www.graalvm.org/22.1/reference-manual/native-image/Resources/
    nativeImageOptions += "-H:IncludeResources=\".*/job_description.*json$\""
  )
