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
    "com.google.guava" % "guava" % "15.0",
    "joda-time" % "joda-time" % "2.3",
    "javax.mail" % "mail" % "1.4.3",
    "commons-beanutils" % "commons-beanutils" % "1.8.2",

    "org.seleniumhq.selenium" % "selenium-java" % "2.35.0" % "test",
    "org.mockito" % "mockito-all" % "1.9.5" % "test",
    "org.jukito" % "jukito" % "1.2" % "test"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
  )
}
