package space.kostya.dif

import org.jline.reader.*
import org.jline.reader.impl.*
import org.jline.reader.impl.completer.{AggregateCompleter, ArgumentCompleter, StringsCompleter}
import org.jline.terminal.*
import org.jline.terminal.impl.*
import space.kostya.dif.format.ColorfulTree
import space.kostya.dif.model.JobDescription
import space.kostya.dif.model.JobSummary
import space.kostya.dif.comp.Op

import scala.util.{Failure, Success}

object Cli {
  def main(args: Array[String]): Unit = {
    val terminal: Terminal = TerminalBuilder.builder().system(true).build()

    val listCommand: String    = "list"
    val compareCommand: String = "compare"

    val jobCompletions = Sandbox.FromResources.listJobs().get.map { summary =>
      val description = s"${summary.name} ${summary.currentState}"
      new Candidate(summary.id, summary.id, summary.projectId, description, null, null, true)
    }

    val completer: Completer = new StringsCompleter(listCommand, compareCommand)
    val compareCompleter: Completer = new ArgumentCompleter(
      new StringsCompleter(compareCommand),
      new StringsCompleter(jobCompletions: _*),
      new StringsCompleter(jobCompletions: _*)
    )

    val reader: LineReader = LineReaderBuilder
      .builder()
      .terminal(terminal)
      .completer(new AggregateCompleter(completer, compareCompleter))
      .build()

    while (true) {
      val line: String = reader.readLine("> ")
      if (line == null) {
        return
      }

      val tokens: Array[String] = line.split("\\s+")

      if (tokens.isEmpty) {
        println("Please enter a command")
      } else {
        tokens(0) match {
          case `listCommand` =>
            Sandbox.FromResources.listJobs() match {
              case Failure(error) => println(s"Error: $error")
              case Success(jobs)  => jobs.foreach(println)
            }
          case `compareCommand` =>
            if (tokens.length < 3) {
              println("Please enter two jobs to compare")
            } else {
              runComparison(tokens(1), tokens(2))
            }
          case _ =>
            println(s"Unknown command ${tokens(0)}")
        }
      }
    }
  }

  private def runComparison(jobId1: String, jobId2: String): Unit = {
    Sandbox.FromResources.listJobs() match {
      case Failure(error) => println(s"Error: $error")
      case Success(summaries) =>
        val job1 = summaries.find(_.id == jobId1)
        val job2 = summaries.find(_.id == jobId2)

        for {
          j1 <- job1
          j2 <- job2
        } yield {
          val diff: List[Op] = Differ.jobSummaryComparison.vs(j2, j1)

          val render = new ColorfulTree(noColor = false)
            .format(jobId1, jobId2, diff)
          render.foreach(println)
        }
    }
  }
}
