lazy val wordle = (project in file("Wordle"))
  .settings(
    name := "wordle1",
    scalaVersion := "3.3.1"
  )

lazy val server = (project in file("server"))
  .enablePlugins(PlayScala)
  .settings(
    name := "server",
    scalaVersion := "3.3.1"
  ).dependsOn(wordle)


lazy val root = (project in file("."))
  .aggregate(server, wordle)
  .settings(
    name := "wordle_web",
    scalaVersion := "3.3.1"
  )