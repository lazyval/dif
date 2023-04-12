package space.kostya.dif

import org.jline.reader.*
import org.jline.reader.impl.*
import org.jline.reader.impl.completer.{AggregateCompleter, ArgumentCompleter, StringsCompleter}
import org.jline.terminal.*
import org.jline.terminal.impl.*
import space.kostya.dif.format.{ColorfulTree, HumanDates}
import space.kostya.dif.model.JobDescription
import space.kostya.dif.model.JobSummary
import space.kostya.dif.comp.Op
import com.typesafe.scalalogging.LazyLogging

import java.time.LocalDateTime
import scala.util.{Failure, Success}

object Cli extends LazyLogging {
  val ListCommand: String    = "list"
  val CompareCommand: String = "compare"

  def main(args: Array[String]): Unit = {
    val terminal: Terminal = TerminalBuilder.builder().system(true).build()

    val compareCompleter: Completer = comparisonCompleter(Sandbox.FromResources)
    val listCompleter: Completer    = new ArgumentCompleter(new StringsCompleter(ListCommand))

    val reader: LineReader = LineReaderBuilder
      .builder()
      .terminal(terminal)
      .completer(new AggregateCompleter(listCompleter, compareCompleter))
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
          case ListCommand =>
            Sandbox.FromResources.listJobs() match {
              case Failure(error) => println(s"Error: $error")
              case Success(jobs)  => jobs.foreach(println)
            }
          case CompareCommand =>
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

  private def comparisonCompleter(api: Api): Completer = {
    // TODO string completer eats the order, so sorting candidates won't help
    // what would help is to implement a custom completer
    val listJobs = api
      .listJobs()
      .getOrElse(List.empty)

    val jobCompletions = listJobs.map { summary =>
      val now = LocalDateTime.now()
      require(
        now.isAfter(summary.createTime),
        s"Job create time (${summary.createTime} is in the future"
      )
      val state = summary.currentState match
        case "JOB_STATE_DONE"      => "✅"
        case "JOB_STATE_FAILED"    => "❌"
        case "JOB_STATE_CANCELLED" => "🚫"
        case "JOB_STATE_RUNNING"   => "⏳"
        case _                     => "❓"
      val when        = HumanDates.approxDifference(earlier = summary.createTime, later = now)
      val description = s"$when ago ${summary.name} $state"
      new Candidate(summary.id, summary.id, null, description, null, null, true)
    }

    new ArgumentCompleter(
      new StringsCompleter(CompareCommand),
      new StringsCompleter(jobCompletions: _*),
      new StringsCompleter(jobCompletions: _*)
    )
  }
}
