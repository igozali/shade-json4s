// Copyright (C) 2019 Lecida Inc
// All Rights Reserved.
//
// NOTICE:  All information contained herein is, and remains the property of
// Lecida Inc. The intellectual and technical concepts contained herein are
// proprietary to Lecida Inc and may be covered by U.S. and Foreign Patents,
// patents in process, and are protected by trade secret or copyright law.
// Dissemination or reproduction of this material is strictly forbidden unless
// prior written permission is obtained from Lecida Inc.

val commonSettings = Seq(
  organization := "com.acme",
  version := "1.0.0",
  scalaVersion := "2.11.12",
  test in assembly := {}
)

// Inspiration for shading JARs taken from:
// https://github.com/wsargent/shade-with-sbt-assembly/
lazy val shaded = (project in file("shaded/libs"))
  .settings(commonSettings)
  .settings(
    name := "my-shaded-lib",
    libraryDependencies ++= Seq(
      "org.json4s" %% "json4s-native" % "3.7.0-M2"
    )
  )
  .settings(
    assemblyOption in assembly ~= { _.copy(includeScala = false) },
    assemblyJarName in assembly := s"my-shaded-lib-${version.value}.jar",
    assemblyShadeRules in assembly := Seq(
      ShadeRule.rename("org.json4s.**" -> "acme.shade.@0").inAll
    )
  )

lazy val root = (project in file ("."))
  .settings(commonSettings)
  .settings(
    name := "shadejson4s",
    mainClass in assembly := Some("com.acme.test.Main"),
    unmanagedJars in Compile ++= Seq(
      shaded.base / "target" / s"scala-${scalaBinaryVersion.value}" / s"my-shaded-lib-${version.value}.jar"
    ),
    update := (update dependsOn (shaded / assembly)).value
  )
