package space.kostya.dif

import org.jline.reader.*
import org.jline.reader.impl.*
import org.jline.reader.impl.completer.{AggregateCompleter, ArgumentCompleter, StringsCompleter}
import org.jline.terminal.*
import org.jline.terminal.impl.*
import space.kostya.dif.format.{ColorfulTree, HumanDates}
import space.kostya.dif.model.JobDescription
import space.kostya.dif.model.JobSummary
import space.kostya.dif.json.DiffOp
import com.typesafe.scalalogging.LazyLogging

import java.time.LocalDateTime
import scala.util.{Failure, Success, Try}

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
    val api: Api = Sandbox.FromResources
    val job1 = api.describeJob(jobId1)
    val job2 = api.describeJob(jobId2)

    val deltaOutput: Try[List[String]] = for {
      j1 <- job1
      j2 <- job2
    } yield {
      val diff: List[DiffOp] = Differ.jobDescriptionComparison.vs(j2, j1)

      new ColorfulTree(noColor = false)
        .format(jobId1, jobId2, diff)
    }

    deltaOutput match {
      case Success(lines) => lines.foreach(println)
      case Failure(ex) => println(s"Failed to compare $jobId1 with $jobId2 because of $ex")
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
