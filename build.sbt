name := "SixThinkingHats"

version := "0.1-SNAPSHOT"

libraryDependencies ++= Seq(
  javaCore,
  jdbc,
  anorm,
  cache
)

publishArtifact in (Compile, packageDoc) := false

scalacOptions += " -feature"

play.Project.playScalaSettings