name := "testcirce"

version := "1.0"

lazy val `testcirce` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq( jdbc , cache , ws   , specs2 % Test )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies +=   "io.circe"  %% "circe-parser"  %  "0.6.1"

// https://mvnrepository.com/artifact/com.chuusai/shapeless_2.9.2
libraryDependencies += "com.chuusai" %% "shapeless" % "2.3.2"
