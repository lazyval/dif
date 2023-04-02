val scala3Version    = "3.2.2"
val CirceVersion     = "0.14.5"
val ScalaTestVersion = "3.2.15"

lazy val root = project
  .in(file("."))
  .enablePlugins(ScoverageSbtPlugin)
  .settings(
    name         := "dif",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "com.google.cloud" % "google-cloud-dataflow" % "0.18.0",
      "io.circe"        %% "circe-core"            % CirceVersion,
      "io.circe"        %% "circe-generic"         % CirceVersion,
      "io.circe"        %% "circe-parser"          % CirceVersion,
      "org.scalatest"   %% "scalatest"             % ScalaTestVersion % Test,
      "org.scalactic"   %% "scalactic"             % ScalaTestVersion % Test,
      // scalacheck magnolify can be used for automatic derivation of arbitrary instances
      // but sadly it doesn't work with scala 3 yet, see https://github.com/spotify/magnolify/pull/676
      // "com.spotify"     %% "magnolify-scalacheck"  % "0.22.0",
      "org.gnieh"      %% "diffson-circe" % "4.4.0",
      "org.jline"        % "jline"        % "3.23.0",
      "org.scalacheck" %% "scalacheck"    % "1.17.0" % Test
    )
  )
