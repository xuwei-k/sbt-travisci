val sbttravisci = project in file(".")

organization := "com.dwijnand"
        name := "sbt-travisci"
    licenses := Seq(("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0")))
 description := "An sbt plugin to integrate with Travis CI"
  developers := List(Developer("dwijnand", "Dale Wijnand", "dale wijnand gmail com", url("https://dwijnand.com")))
   startYear := Some(2016)
    homepage := scmInfo.value map (_.browseUrl)
     scmInfo := Some(ScmInfo(url("https://github.com/dwijnand/sbt-travisci"), "scm:git:git@github.com:dwijnand/sbt-travisci.git"))

   sbtPlugin := true

       maxErrors := 15
triggeredMessage := Watched.clearWhenTriggered

scalacOptions ++= Seq("-encoding", "utf8")
scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint")
scalacOptions  += "-Xfuture"
scalacOptions  += "-Yno-adapted-args"
scalacOptions  += "-Ywarn-dead-code"
scalacOptions  += "-Ywarn-numeric-widen"
scalacOptions  += "-Ywarn-value-discard"

libraryDependencies += "org.yaml" % "snakeyaml" % "1.17"

             fork in Test := false
      logBuffered in Test := false
parallelExecution in Test := true

scriptedSettings
scriptedLaunchOpts ++= Seq("-Xmx1024M", "-XX:MaxPermSize=256M", "-Dplugin.version=" + version.value)
scriptedBufferLog := true

def toSbtPlugin(m: ModuleID) = Def.setting(
  Defaults.sbtPluginExtra(m, (sbtBinaryVersion in update).value, (scalaBinaryVersion in update).value)
)
mimaPreviousArtifacts := Set(toSbtPlugin("com.dwijnand" % "sbt-travisci" % "1.1.0").value)

TaskKey[Unit]("verify") := Def.sequential(test in Test, scripted.toTask(""), mimaReportBinaryIssues).value

cancelable in Global := true

ScriptedPlugin.scripted := {
  val args = ScriptedPlugin.asInstanceOf[{
    def scriptedParser(f: File): complete.Parser[Seq[String]]
  }].scriptedParser(sbtTestDirectory.value).parsed
  val prereq: Unit = scriptedDependencies.value
  try {
    if((sbtVersion in pluginCrossBuild).value == "1.0.0-M6") {
      ScriptedPlugin.scriptedTests.value.asInstanceOf[{
        def run(
          x1: File,
          x2: Boolean,
          x3: Array[String],
          x4: File,
          x5: Array[String],
          x6: java.util.List[File]
        ): Unit
      }].run(
        sbtTestDirectory.value,
        scriptedBufferLog.value,
        args.toArray,
        sbtLauncher.value,
        scriptedLaunchOpts.value.toArray,
        new java.util.ArrayList()
      )
    } else {
      ScriptedPlugin.scriptedTests.value.asInstanceOf[{
        def run(
          x1: File,
          x2: Boolean,
          x3: Array[String],
          x4: File,
          x5: Array[String]
        ): Unit
      }].run(
        sbtTestDirectory.value,
        scriptedBufferLog.value,
        args.toArray,
        sbtLauncher.value,
        scriptedLaunchOpts.value.toArray
      )
    }
  } catch { case e: java.lang.reflect.InvocationTargetException => throw e.getCause }
}
