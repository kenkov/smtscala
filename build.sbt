name := "smt"

version := "0.1"

scalaVersion := "2.10.1"

fork := true

javaOptions ++= Seq("-Dfile.encoding=UTF-8",
                    "-Xmx1536M",
                    "-Xss1M",
                    "-XX:+CMSClassUnloadingEnabled",
                    "-XX:MaxPermSize=384M")

scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation")

javacOptions ++= Seq("-encoding", "UTF-8")


outputStrategy := Some(StdoutOutput)

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test",
  // use the right Slick version here:
  "com.typesafe.slick" %% "slick" % "1.0.0",
  "org.slf4j" % "slf4j-nop" % "1.7.5",
  "org.xerial" % "sqlite-jdbc" % "3.7.2",
  // scalacheck
  "org.scalacheck" %% "scalacheck" % "1.10.0" % "test"
)
