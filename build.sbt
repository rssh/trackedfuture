
lazy val commonSettings = Seq(
  version := "0.3",
  organization := "com.github.rssh",
  scalaVersion := "2.11.8"
)

lazy val root = (project in file(".")).aggregate(agent,example).settings(
                  aggregate in run := false
                ).disablePlugins(sbtassembly.AssemblyPlugin)

lazy val agent = project.in(file("agent")).settings(commonSettings: _*)

lazy val example = project.in(file("example")).
                         settings(commonSettings).
                         dependsOn(agent).
                         disablePlugins(sbtassembly.AssemblyPlugin)


