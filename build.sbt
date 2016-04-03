import java.text.SimpleDateFormat
import java.util.Date

import sbt._
import Keys._

import scala.util.Try
//import scalariform.formatter.preferences._

val slf4jVersion = "1.7.19"
val logBackVersion = "1.1.6"
val scalaLoggingVersion = "3.1.0"
val slickVersion = "3.1.1"
val seleniumVersion = "2.48.2"
val circeVersion = "0.4.0-RC1"
val akkaVersion = "2.4.3"

val slf4jApi = "org.slf4j" % "slf4j-api" % slf4jVersion
val logBackClassic = "ch.qos.logback" % "logback-classic" % logBackVersion
val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
val loggingStack = Seq(slf4jApi, logBackClassic, scalaLogging)

val typesafeConfig = "com.typesafe" % "config" % "1.3.0"

val circeCore = "io.circe" %% "circe-core" % circeVersion
val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
val circeJawn = "io.circe" %% "circe-jawn" % circeVersion
val circe = Seq(circeCore, circeGeneric, circeJawn)

val javaxMailSun = "com.sun.mail" % "javax.mail" % "1.5.5"

val slick = "com.typesafe.slick" %% "slick" % slickVersion
val slickHikari = "com.typesafe.slick" %% "slick-hikaricp" % slickVersion
val h2 = "com.h2database" % "h2" % "1.3.176" //watch out! 1.4.190 is beta
val postgres = "org.postgresql" % "postgresql" % "9.4.1208"
val flyway = "org.flywaydb" % "flyway-core" % "4.0"
val slickStack = Seq(slick, h2, postgres, slickHikari, flyway)

val scalatest = "org.scalatest" %% "scalatest" % "2.2.6" % "test"
val unitTestingStack = Seq(scalatest)

val seleniumJava = "org.seleniumhq.selenium" % "selenium-java" % seleniumVersion % "test"
val seleniumFirefox = "org.seleniumhq.selenium" % "selenium-firefox-driver" % seleniumVersion % "test"
val seleniumStack = Seq(seleniumJava, seleniumFirefox)


val akkaStack = Seq(
"com.typesafe.akka" %% "akka-http-core" % akkaVersion, 
"com.typesafe.akka" %% "akka-http-experimental" % akkaVersion,
"com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaVersion,
"com.typesafe.akka" %% "akka-http-testkit" % akkaVersion % "test", 
"com.softwaremill.akka-http-session" %% "core" % "0.2.4"
)

val commonDependencies = unitTestingStack ++ loggingStack

val gdbscan = Seq(
    "org.scalanlp"        %% "nak"            % "1.3",
    "org.scalanlp" 		  %% "breeze-natives" % "0.8" % "test, runtime"
)

lazy val commonSettings = Seq(
  organization := "com.afei",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.11.8",
  scalacOptions ++= Seq("-unchecked", "-deprecation"),
  libraryDependencies ++= commonDependencies
)


lazy val app = crossProject.in(file("."))
  .settings(commonSettings: _*)
  .settings(
  unmanagedSourceDirectories in Compile += baseDirectory.value  / "shared" / "main" / "scala",
  libraryDependencies ++= Seq(
	"com.lihaoyi" %%% "upickle" % "0.3.9",
	"com.lihaoyi" %%% "autowire" % "0.2.5"
  )
).jsSettings(
	  name := "appJs-pro",
	libraryDependencies ++= Seq(
	"be.doeraene" %%% "scalajs-jquery" % "0.9.0",
	"com.greencatsoft" %%% "scalajs-angular" % "0.6"
	),

	skip in packageJSDependencies := false,

	jsDependencies ++= Seq(
	"org.webjars.bower" % "angular" % "1.5.1" / "angular.js",
	"org.webjars.bower" % "d3" % "3.5.16" / "d3.js",
	"org.webjars.bower" % "nvd3" % "1.8.2" / "nv.d3.js" dependsOn "d3.js",
	"org.webjars.bower" % "angular-nvd3" % "1.0.5" / "angular-nvd3.js" dependsOn "angular.js" 
	),

	//jsDependencies += RuntimeDOM

	// uTest settings
	libraryDependencies += "com.lihaoyi" %%% "utest" % "0.3.0" % "test",
	testFrameworks += new TestFramework("utest.runner.Framework"),

	persistLauncher in Compile := true,
	persistLauncher in Test := false,

        artifactPath in (Compile, packageScalaJSLauncher) := baseDirectory.value / ".." / "jvm" / "webapp" / "js" / "launcher.js",
        artifactPath in (Compile, fastOptJS) := baseDirectory.value / ".." / "jvm" / "webapp" / "js" / "fastOpt.js",
	artifactPath in (Compile, fullOptJS) := baseDirectory.value / ".." / "jvm" / "webapp" / "js" / "fullOpt.js",
        artifactPath in (Compile, packageJSDependencies) := baseDirectory.value / ".." / "jvm" / "webapp" / "js" / "dependency.js"

).jvmSettings(
	 name := "appJvm-pro",
    libraryDependencies ++= slickStack ++ akkaStack ++ circe ++ Seq(javaxMailSun, typesafeConfig) ++ gdbscan,

//    buildInfoPackage := "com.afei.akkaangular.version",
//    buildInfoObject := "BuildInfo",
//    buildInfoKeys := Seq[BuildInfoKey](
//    BuildInfoKey.action("buildDate")(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())),
      // if the build is done outside of a git repository, we still want it to succeed
//      BuildInfoKey.action("buildSha")(Try(Process("git rev-parse HEAD").!!.stripLineEnd).getOrElse("?"))),

    compile in Compile := {
      val compilationResult = (compile in Compile).value
      IO.touch(target.value / "compilationFinished")

      compilationResult
    },
	
    mainClass in Compile := Some("com.afei.akkaangular.Main")
)

lazy val appJS = app.js.enablePlugins(ScalaJSPlugin)

lazy val appJVM = app.jvm
//.enablePlugins(BuildInfoPlugin)





//EclipseKeys.useProjectId := true
//EclipseKeys.skipParents in ThisBuild := true

