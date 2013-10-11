name := "play-security-example"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.mongodb" %% "casbah" % "2.6.3",
  "joda-time" % "joda-time" % "2.3",
  "com.novus" %% "salat" % "1.9.3"
)     

play.Project.playScalaSettings
