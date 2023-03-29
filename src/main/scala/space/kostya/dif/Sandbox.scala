package space.kostya.dif
import space.kostya.dif.model.{JobDescription, JobSummary}

import scala.util.Try

open class Sandbox(resourceDir: String) extends Api {
  override def listJobs(): Try[List[JobSummary]] = {
    val json = scala.io.Source.fromResource(s"$resourceDir/job_list.json").mkString
    import io.circe.generic.auto._
    import io.circe.parser._
    decode[List[JobSummary]](json).toTry
  }

  override def describeJob(jobId: String): JobDescription = ???
}
object Sandbox {
//  val Default = new Sandbox("src/main/resources")
  val Default = new Sandbox("json_sandbox")
}
