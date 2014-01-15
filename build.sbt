name := "SixThinkingHats"

version := "0.1-SNAPSHOT"

libraryDependencies ++= Seq(
  javaCore,
  jdbc,
  anorm,
  cache,
  "com.typesafe" %% "play-plugins-mailer" % "2.2.0"
)

sources in doc in Compile := List()

scalacOptions += " -feature"

play.Project.playScalaSettings
