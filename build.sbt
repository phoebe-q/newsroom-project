name := """NewsRoom"""
organization := "com.NewsRoom"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.13.6"

resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies += (guice)

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.13.1"
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.1"
libraryDependencies += "org.elasticsearch" % "elasticsearch" % "7.16.2"
libraryDependencies += "org.elasticsearch.client" % "elasticsearch-rest-high-level-client" % "7.16.2"
libraryDependencies += "org.springframework" % "spring-web" % "5.3.14"
libraryDependencies += "org.springframework.data" % "spring-data-elasticsearch" % "4.3.0"
libraryDependencies += "com.alibaba" % "fastjson" % "1.2.79"
libraryDependencies += "com.google.http-client" % "google-http-client-gson" % "1.41.0"
libraryDependencies += "com.github.sapher" % "youtubedl-java" % "1.1"

libraryDependencies ++= Seq(
  javaJdbc
)







