import play.Project._

libraryDependencies ++= Seq(
  javaCore,
  jdbc,
  anorm,
  cache
)

sources in doc in Compile := List()

scalacOptions += "-feature"

scalacOptions += "-language:postfixOps"

play.Project.playScalaSettings