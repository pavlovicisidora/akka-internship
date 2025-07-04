name := "akka-quickstart-scala"

version := "1.0"

scalaVersion := "2.13.16"

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
  "com.typesafe.akka" %% "akka-http-testkit" % "10.5.3" % Test,
  "ch.qos.logback" % "logback-classic" % "1.5.18",
  "org.scalatest" %% "scalatest" % "3.2.19" % Test,
  "org.scalatestplus" %% "scalacheck-1-15" % "3.2.11.0" % Test,
  "com.typesafe.akka" %% "akka-http" % "10.5.3",
  "com.typesafe.akka" %% "akka-stream" % "2.8.8",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.3",
  "joda-time" % "joda-time" % "2.14.0",
  "com.beachape" %% "enumeratum" % "1.7.0",
  "com.typesafe.slick" %% "slick" % "3.4.1",
  "org.postgresql" % "postgresql" % "42.6.0",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.4.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.4.2",
  "org.scalikejdbc" %% "scalikejdbc"       % "4.3.2",
  "com.h2database"  %  "h2"                % "2.2.224",
  "ch.qos.logback"  %  "logback-classic"   % "1.5.6",
  "org.scalikejdbc" %% "scalikejdbc-config" % "4.0.0",
  "com.pauldijou" %% "jwt-core" % "5.0.0",
  "com.pauldijou" %% "jwt-spray-json" % "5.0.0",
  "com.github.t3hnar" %% "scala-bcrypt" % "4.3.0",
  "org.scalikejdbc" %% "scalikejdbc-test" % "4.1.0" % Test

)
