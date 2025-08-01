name := """room-service"""
organization := "com.radovan.play"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.16"

libraryDependencies ++= Seq(
  guice,
  jdbc,
  ehcache,
  ws,
  "org.hibernate.orm" % "hibernate-core" % "6.5.2.Final",
  "jakarta.persistence" % "jakarta.persistence-api" % "3.2.0",
  "com.zaxxer" % "HikariCP" % "5.1.0",
  "org.mariadb.jdbc" % "mariadb-java-client" % "3.5.2",
  "org.playframework.anorm" %% "anorm" % "2.8.1",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.17.2",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.17.2",
  "org.modelmapper" % "modelmapper" % "3.2.4",
  "org.apache.pekko" %% "pekko-slf4j" % "1.0.2",
  "org.apache.pekko" %% "pekko-actor" % "1.0.2",
  "io.nats" % "jnats" % "2.20.5",
  "io.jsonwebtoken" % "jjwt-api" % "0.12.5",
  "io.jsonwebtoken" % "jjwt-impl" % "0.12.5" % "runtime",
  "io.jsonwebtoken" % "jjwt-jackson" % "0.12.5" % "runtime",
  "com.auth0" % "java-jwt" % "4.4.0",
  "org.bouncycastle" % "bcprov-jdk18on" % "1.79",
  "com.github.ben-manes.caffeine" % "caffeine" % "3.2.0",
  "org.jsoup" % "jsoup" % "1.20.1"
)

