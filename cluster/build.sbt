name := "cluster"

libraryDependencies ++=Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.10",
  "com.typesafe.akka" %% "akka-cluster" % "2.4.10",
  "com.typesafe.akka" %% "akka-cluster-metrics" % "2.4.10",
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.10"
)

outputPath in assembly := baseDirectory.value.getAbsoluteFile.getParentFile /
  "assembly" / "cluster.jar"
