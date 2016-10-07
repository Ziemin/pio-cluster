
name := "pio"

version in ThisBuild := "0.0.1"

scalaVersion in ThisBuild := "2.11.8"

javacOptions in (ThisBuild, compile) ++= Seq("-source", "1.8", "-target", "1.8",
  "-Xlint:deprecation", "-Xlint:unchecked")

lazy val conf = file(".") / "conf"

lazy val root = project in file(".") aggregate(
  cluster)

lazy val cluster = (project in file("cluster")).
  settings(unmanagedClasspath in Test += conf)
