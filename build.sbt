name := "akka-quickstart-scala"

version := "1.0"

scalaVersion := "2.13.15"

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

lazy val akkaVersion = sys.props.getOrElse("akka.version", "2.10.6")

// Run in a separate JVM, to make sure sbt waits until all threads have
// finished before returning.
// If you want to keep the application running while executing other
// sbt tasks, consider https://github.com/spray/sbt-revolver/
fork := true

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.8.8",
  "com.typesafe.akka" %% "akka-testkit" % "2.8.8" % Test,
  "ch.qos.logback" % "logback-classic" % "1.5.18",
  "org.scalatest" %% "scalatest" % "3.2.19" % Test,
  "com.typesafe.akka" %% "akka-http" % "10.5.3",
  "com.typesafe.akka" %% "akka-stream" % "2.8.8",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.3",
  "joda-time" % "joda-time" % "2.14.0",
  "com.beachape" %% "enumeratum" % "1.7.0"
)
