name := "SixThinkingHats"

version := "0.1-SNAPSHOT"

libraryDependencies ++= Seq(
  javaCore,
  jdbc,
  anorm,
  cache
)

sources in doc in Compile := List()

scalacOptions += " -feature"

play.Project.playScalaSettings