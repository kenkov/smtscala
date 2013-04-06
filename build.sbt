name := "smt"

version := "0.1"

scalaVersion := "2.10.1"

fork := true

javaOptions ++= Seq("-Dfile.encoding=UTF-8",
                    "-Xmx4000M",
                    "-XX:+CMSClassUnloadingEnabled",
                    "-XX:MaxNewSize=2000M",
                    "-XX:MaxPermSize=2000M",
                    // for sen
                    /// This line should be set by the environment variable $SEN_HOME,
                    /// but I don't know how to use vir envs in build.sbt
                    "-Dsen.home=sen",
                    /// to erase the "情報" line
                    "-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.NoOpLog")

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
