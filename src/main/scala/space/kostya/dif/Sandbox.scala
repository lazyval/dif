package space.kostya.dif

import space.kostya.dif.model.{JobDescription, JobSummary}
import space.kostya.dif.json.Parser
import io.circe.DecodingFailure.Reason.CustomReason

import java.io.FileNotFoundException
import java.nio.file.Path
import java.nio.file.Files
import java.time.{LocalDateTime, OffsetDateTime}
import java.time.format.DateTimeFormatter
import scala.util.{Failure, Success, Try}
import com.typesafe.scalalogging.LazyLogging
import scala.jdk.CollectionConverters.*

open class Sandbox(resourceDir: String) extends Api with LazyLogging {
  override def listJobs(): Try[List[JobSummary]] = {
    try {
      val jobs = readJobs().map { job =>
        JobSummary(job.id, job.name, job.`type`, job.currentState, job.createTime, job.projectId)
      }
      logger.trace(s"Successfully read ${jobs.size} jobs from $resourceDir")
      Success(jobs)
    } catch {
      case ex: Exception =>
        logger.error(s"Failed to read jobs from $resourceDir", ex)
        Failure(ex)
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

  @throws[io.circe.DecodingFailure]("if job json is invalid")
  @throws[FileNotFoundException]("when directory is located, but individual file is not")
  private def readJobs(): List[JobDescription] = try {
    jsons(prefix = "job_description_").flatMap { path =>
      val json        = Files.lines(path).iterator().asScala.mkString
      val maybeParsed = Parser.readDescription(rawJson = json)
      maybeParsed.recoverWith { case ex: io.circe.DecodingFailure =>
        val msg = s"Failed to parse job description from $path"
        throw ex.withReason(reason = CustomReason(msg))
      }.toOption
    }
  } catch {
    case ex: FileNotFoundException =>
      val msg = s"Failed to locate job description files at $resourceDir"
      throw new RuntimeException(msg, ex)
  }

  /** Paths of JSONs matching a prefix, or nothing if no match in resource dir
    *
    * {{{
    * scala> Sandbox.FromResources.jsons(prefix="job_desc").size
    * res1: Int = 6
    *
    * scala> Sandbox.FromResources.jsons(prefix="job_description_1").size
    * res2: Int = 1
    *
    * scala> Sandbox.FromResources.jsons(prefix="jb").size
    * res3: Int = 0
    * }}}
    */
  def jsons(prefix: String): List[Path] = {
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
}

object Sandbox {
  val FromResources = new Sandbox("json_sandbox")
}
