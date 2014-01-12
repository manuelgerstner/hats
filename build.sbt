name := "SixThinkingHats"

version := "0.1-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)

scalacOptions += "-feature"

scalacOptions += "-language:postfixOps"

play.Project.playScalaSettings
