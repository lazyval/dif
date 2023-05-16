package space.kostya.dif

import space.kostya.dif.model.{JobDescription, JobSummary}

import scala.util.Try

trait Api {
  // for now, return a list, but it's really an infinite iterator, which might take filters
  def listJobs(): Try[List[JobSummary]]
  def describeJob(jobId: String): Try[JobDescription]
}
