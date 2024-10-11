lazy val wordle = (project in file("Wordle"))
  .settings(
    name := "wordle1",
    scalaVersion := "3.3.1"
  )

lazy val server = (project in file("server"))
  .enablePlugins(PlayScala)
  .settings(
    name := "server",
    scalaVersion := "3.3.1",
      libraryDependencies ++= Seq(
      guice,
      "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test
    )
  ).dependsOn(wordle)


lazy val root = (project in file("."))
  .aggregate(server, wordle)
  .settings(
    name := "wordle_web",
    scalaVersion := "3.3.1"
  )