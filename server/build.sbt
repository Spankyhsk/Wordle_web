name := """server"""
organization := "de.htwg"

version := "1.0-SNAPSHOT"

//lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "3.3.1"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test





enablePlugins(PlayScala, SbtWeb)




// Adds additional packages into Twirl
//TwirlKeys.templateImports += "de.htwg.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "de.htwg.binders._"
