package space.kostya.dif.google

import com.google.dataflow.v1beta3.{Environment, Job}
import space.kostya.dif.Api
import space.kostya.dif.model.{JobDescription, JobEnvironment, JobSummary}

import java.time.LocalDateTime
import scala.util.Try

// stab for client based on google java apis use
// spent too much time on this, trying to fix propagation of credentials
// will switch over to process based wrapper over gcloud cli for now
class GcpClient(projectId: String) extends Api {
  override def listJobs(): Try[List[JobSummary]]               = ???
  override def describeJob(jobId: String): Try[JobDescription] = ???
}

given Conversion[Job, JobSummary] with {
  def apply(job: Job): JobSummary = {
    JobSummary(
      id = job.getId,
      name = job.getName,
      projectId = job.getProjectId,
      `type` = job.getType.toString,
      currentState = job.getCurrentState.toString
    )
  }
}
