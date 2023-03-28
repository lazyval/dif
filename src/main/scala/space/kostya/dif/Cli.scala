package space.kostya.dif



// have a CLI that will read two required parameters: ids of the jobs to compare
// then it will output the result of the comparison. The output type can be modified with a flag, either a raw json-patch output, or a markdown table.
object Cli {
  case class Config(first: String = "", second: String = "", markdown: Boolean = false)

  case class Diff(first: Job, second: Job)

  case class Job(id: String, name: String, status: String, duration: Long)

  def main(args: Array[String]): Unit = {
    val parser = new scopt.OptionParser[Config]("dif") {
      head("dif", "0.1")

      opt[String]('a', "first").required().action((x, c) =>
        c.copy(first = x)).text("first job id")

      opt[String]('b', "second").required().action((x, c) =>
        c.copy(second = x)).text("second job id")

      opt[Unit]('m', "markdown").action((_, c) =>
        c.copy(markdown = true)).text("output in markdown format")

      help("help").text("prints this usage text")
    }

    parser.parse(args, Config()) match {
      case Some(config) =>
        val diff = Dif.compare(config.first, config.second)
        if (config.markdown) {
          println(Dif.toMarkdown(diff))
        } else {
          println(Dif.toJson(diff))
        }
      case None =>
      // arguments are bad, error message will have been displayed
    }
  }

  object Dif {
    def compare(first: String, second: String): Diff = {
      val firstJob = ??? // Job(first)
      val secondJob = ??? // Job(second)
      val diff = ??? // Diff(firstJob, secondJob)
      diff
    }

    def toMarkdown(diff: Diff): String = {
      s"## ${diff.first.id} vs ${diff.second.id}"
    }

    def toJson(diff: Diff): String = ???
  }
}
