package space.kostya.dif
import io.circe.Decoder.Result
import io.circe.DecodingFailure.Reason.CustomReason
import io.circe.{Decoder, DecodingFailure, HCursor}
import space.kostya.dif.model.{JobDescription, JobSummary}

import java.io.FileNotFoundException
import java.nio.file.Path
import java.nio.file.Files
import java.time.{LocalDateTime, OffsetDateTime}
import java.time.format.DateTimeFormatter
import scala.util.{Failure, Success, Try}
import scala.jdk.CollectionConverters.*

open class Sandbox(resourceDir: String) extends Api {
  given Decoder[LocalDateTime] = Decoder.decodeString.emapTry { str =>
    // dataflow time is decoded with 'Z' in the end => OffsetDateTime
    Try(OffsetDateTime.parse(str).toLocalDateTime)
  }

  override def listJobs(): Try[List[JobSummary]] = {
    try {
      val jobs = readJobs().map { job =>
        JobSummary(job.id, job.name, job.`type`, job.currentState, job.createTime, job.projectId)
      }
      Success(jobs)
    } catch {
      case ex: Exception => Failure(ex)
    }
  }

  override def describeJob(jobId: String): Try[JobDescription] = {
    val jobs = readJobs()

    jobs.find(_.id == jobId) match {
      case Some(job) => Try(job)
      case None =>
        val msg = s"Job $jobId not found in $resourceDir (checked ${jobs.size} jobs)"
        Try(throw new RuntimeException(msg))
    }
  }

  private def resources(prefix: String): List[Path] = {
    val uri = getClass.getClassLoader.getResource(resourceDir).toURI
    val path = if (uri.getScheme.equals("jar")) {
      val fs =
        try {
          java.nio.file.FileSystems.getFileSystem(uri)
        } catch {
          case _: java.nio.file.FileSystemNotFoundException =>
            java.nio.file.FileSystems.newFileSystem(uri, Map.empty[String, String].asJava)
        }
      fs.getPath(resourceDir)
    } else {
      java.nio.file.Paths.get(uri)
    }
    val iterator = Files.walk(path).iterator().asScala
    val jsons    = iterator.filter(_.getFileName.toString.endsWith(".json"))
    jsons.filter(_.getFileName.toString.startsWith(prefix)).toList
  }

  @throws[io.circe.DecodingFailure]("if job json is invalid")
  @throws[FileNotFoundException]("when directory is located, but individual file is not")
  @throws[RuntimeException](s"if files cannot be located at $resourceDir")
  private def readJobs(): List[JobDescription] = {
    resources(prefix = "job_description_").flatMap { path =>
      val json = Files.lines(path).iterator().asScala.mkString
      import io.circe.generic.auto._
      import io.circe.parser._

      decode[JobDescription](json) match {
        case Left(ex: DecodingFailure) =>
          val msg = s"Failed to decode $path, original cause is ${ex.reason}"
          throw ex.withReason(CustomReason(msg))
        case Left(ex)   => throw ex
        case Right(job) => List(job)
      }
    }
  }

}
object Sandbox {
//  val Default = new Sandbox("src/main/resources")
  val FromResources = new Sandbox("json_sandbox")
}
