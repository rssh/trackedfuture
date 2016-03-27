

lazy val root = (project in file(".")).aggregate(agent,example).settings(
                  aggregate in run := false
                )

lazy val agent = project.in(file("agent"))

lazy val example = project.in(file("example")).dependsOn(agent)


