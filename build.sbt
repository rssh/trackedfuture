
ThisBuild/version := "0.5.0"
ThisBuild/versionScheme := Some("semver-spec")

lazy val commonSettings = Seq(
  organization := "com.github.rssh",
  scalaVersion := "3.0.1"
)


lazy val root = (project in file(".")).aggregate(agent,example).settings(
                  run/aggregate := false,
                  publishArtifact := false
                ).disablePlugins(sbtassembly.AssemblyPlugin)

lazy val agent = project.in(file("agent")).settings(commonSettings: _*)
                     .settings(
                        name:="trackedfuture",
                        assembly / assemblyShadeRules := Seq(
                           ShadeRule.rename("org.objectweb.asm.**" -> "trackedfuture.org.objectweb.asm.@1").inAll
                        ),
                        assembly / assemblyOption := (assembly / assemblyOption).value.copy(includeScala = false),
                        assembly / artifact := {
                           val art = ( assembly / artifact).value
                           art.withClassifier(Some("assembly"))
                        },
                        addArtifact( assembly / artifact, assembly),
                        exportJars := true,
                        compile/packageBin/packageOptions += {
                          val file = new java.io.File("agent/src/main/resource/META-INF/MANIFEST.MF")
                          val manifest = sbt.io.Using.fileInputStream(file)( in => new java.util.jar.Manifest(in) )
                          Package.JarManifest(manifest)
                        },
                        libraryDependencies += "org.ow2.asm" % "asm" % "9.1",
                        libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % "test"
                     )

lazy val example = project.in(file("example")).
                         settings(commonSettings).
                         settings(
                           name := "trackedfuture-example",
                           publish/skip := true,
                           fork := true,
                           libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % "test",
                           javaOptions ++= Seq("--add-opens", "java.base/java.lang=ALL-UNNAMED"),
                           // test assembly here:
                           javaOptions += s"-javaagent:../agent/target/scala-3.0.1/trackedfuture_3-${version.value}.jar"
                           // test published assembly:
                           //javaOptions += s"""-javaagent:${System.getProperty("user.home")}/.ivy2/local/com.github.rssh/trackedfuture_2.13/${version.value}/jars/trackedfuture_2.11-assembly.jar"""
                         ).
                         dependsOn(agent).
                         disablePlugins(sbtassembly.AssemblyPlugin)


