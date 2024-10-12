name := """server"""
organization := "de.htwg"

version := "1.0-SNAPSHOT"


scalaVersion := "3.3.1"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test
libraryDependencies += "de.htwg.se" %% "wordle" % "1.0-SNAPSHOT" from ""

enablePlugins(PlayScala)




// Adds additional packages into Twirl
//TwirlKeys.templateImports += "de.htwg.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "de.htwg.binders._"
