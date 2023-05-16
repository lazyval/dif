package space.kostya.dif.google

import com.typesafe.scalalogging.LazyLogging
import space.kostya.dif.Api
import space.kostya.dif.model.{JobDescription, JobEnvironment, JobSummary}
import io.circe.Decoder.Result
import io.circe.DecodingFailure.Reason.CustomReason
import io.circe.{Decoder, DecodingFailure}
import io.circe.generic.auto.*
import io.circe.parser.*
import space.kostya.dif.json.Parser

import java.time
import java.time.LocalDateTime
import scala.util.Try

// stab for client based on google java apis use
// spent too much time on this, trying to fix propagation of credentials
// will switch over to process based wrapper over gcloud cli for now
class GcpClient(projectId: String, region: String) extends Api with LazyLogging {

  import scala.sys.process._

  private def describeCmd(jobId: String) = {
    // <!> taking the raw input is unsafe due to potential injection, but
    // assuming we're running in trusted environment
    Seq(
      "gcloud dataflow jobs describe",
      s"$jobId --project=$projectId",
      "--format=json",
      s"--region $region --full"
    ).mkString(" ")
  }

  override def listJobs(): Try[List[JobSummary]] = ???

  override def describeJob(jobId: String): Try[JobDescription] = Try {
    val cmd = describeCmd(jobId)
    cmd.!!
  }.flatMap(output => Parser.readDescription(output))
}