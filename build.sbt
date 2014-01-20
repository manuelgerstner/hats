import play.Project._

libraryDependencies ++= Seq(
  javaCore,
  jdbc,
  anorm,
  cache,
  "com.typesafe" %% "play-plugins-mailer" % "2.1-RC2"
)

sources in doc in Compile := List()

scalacOptions += "-feature"

scalacOptions += "-language:postfixOps"

play.Project.playScalaSettings
