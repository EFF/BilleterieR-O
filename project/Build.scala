import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "BilletterieRO"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "com.google.inject" % "guice" % "3.0",
    javaCore,
    javaJdbc,
    javaEbean,
    "com.google.guava" % "guava" % "15.0"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
  )

}
