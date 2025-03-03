lazy val root = project.in(file("."))
  .settings(
    name := "scala3-compiletime-tasty",
    organization := "com.example",
    scalaVersion := "3.6.3",
    version := "0.1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      "ch.epfl.scala" %% "tasty-query" % "1.5.0" % Test,
      "com.lihaoyi" %% "pprint" % "0.9.0" % Test,
      "org.typelevel" %% "cats-core" % "2.13.0",
    ),
    Test / sourceGenerators += Def.task {
      val file = (Test / sourceManaged).value / "Classpath.scala"
      val paths = (Runtime / fullClasspath).value.map(_.data)
      IO.write(
        file,
        s"""
        |package example
        |
        |import java.nio.file.Paths
        |
        |object Classpath {
        |  val paths = List(
        |    ${paths.map(p => s"""Paths.get("$p")""").mkString(",\n    ")}
        |  )
        |}
        |""".stripMargin.trim
      )
      Seq(file)
    },
  )
