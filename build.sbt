name := """TodoManager"""
organization := "org.okarmus"

version := "1.0-SNAPSHOT"

resolvers += "lightshed-maven" at "http://dl.bintray.com/content/lightshed/maven"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies += guice

libraryDependencies += jdbc

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

libraryDependencies += "ch.lightshed" %% "courier" % "0.1.4"

libraryDependencies += "org.postgresql" % "postgresql" % "9.3-1100-jdbc41"

//flywayUrl := "jdbc:h2:file:./target/foobar"

//flywayUser := "SA"