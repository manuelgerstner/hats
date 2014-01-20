import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    // val appName         = "play-easymail-usage"
    // val appVersion      = "1.0-SNAPSHOT"
    val name = "SixThinkingHats"
    val version = "0.1-SNAPSHOT"
    
    val appDependencies = Seq(
    	javaCore,
      	"com.feth" %% "play-easymail" % "0.5-SNAPSHOT"
    )

    val main = play.Project(name, version, appDependencies).settings(
      resolvers += Resolver.url("play-easymail (release)", url("http://joscha.github.com/play-easymail/repo/releases/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.url("play-easymail (snapshot)", url("http://joscha.github.com/play-easymail/repo/snapshots/"))(Resolver.ivyStylePatterns)
    )

}
