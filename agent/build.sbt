

//scalaVersion := "2.11.8"
name := "trackedfuture"
//organization := "com.github.rssh"
//version := "0.1"

libraryDependencies += "org.ow2.asm" % "asm" % "5.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
assemblyShadeRules in assembly := Seq(
      ShadeRule.rename("org.objectweb.asm.**" -> "trackedfuture.org.objectweb.asm.@1").inAll
)
artifact in (Compile, assembly) := (artifact in (Compile, assembly)).value.copy(`classifier` = Some("assembly"))
  
addArtifact(artifact in (Compile, assembly), assembly)


//assemblyJarName in assembly := s"${name}-agent-${version}"

exportJars := true

packageOptions in (Compile, packageBin) += {
  val file = new java.io.File("agent/src/main/resource/META-INF/MANIFEST.MF")
  val manifest = Using.fileInputStream(file)( in => new java.util.jar.Manifest(in) )
  Package.JarManifest(manifest) 
}
