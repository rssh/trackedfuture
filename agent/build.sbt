

scalaVersion := "2.11.8"
name := "trackedfuture"
organization := "com.github.rssh"
version := "0.1"

libraryDependencies += "org.ow2.asm" % "asm" % "5.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"

exportJars := true

packageOptions in (Compile, packageBin) += {
  val file = new java.io.File("agent/src/main/resource/META-INF/MANIFEST.MF")
  val manifest = Using.fileInputStream(file)( in => new java.util.jar.Manifest(in) )
  Package.JarManifest(manifest) 
}
