name := "dev-talk-wiremock"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

resolvers += Resolver.bintrayRepo("hmrc", "releases")

libraryDependencies += ws
libraryDependencies += "uk.gov.hmrc" %% "bootstrap-play-25" % "3.10.0"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.1" % Test
libraryDependencies += "com.github.tomakehurst" % "wiremock-standalone" % "2.19.0" % Test
