
scalaVersion := "2.11.8"

name := "trackedfuture-example"
version := "0.3"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"

fork := true
//javaOptions += s"-javaagent:../agent/target/scala-2.11/trackedfuture_2.11-${version.value}.jar"
// test assembly
//javaOptions += s"-javaagent:../agent/target/scala-2.11/trackedfuture-assembly-${version.value}.jar"
// test published assembly
javaOptions += s"""-javaagent:${System.getProperty("user.home")}/.ivy2/local/com.github.rssh/trackedfuture_2.11/${version.value}/jars/trackedfuture_2.11-assembly.jar"""


