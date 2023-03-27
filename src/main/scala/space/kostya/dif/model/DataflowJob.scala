package space.kostya.dif.model

import com.google.dataflow.v1beta3.*
import com.google.protobuf.Timestamp
import space.kostya.dif.ResourceOps
import space.kostya.dif.google.JobMetricsClient

import java.time.{LocalDateTime, ZoneId, ZoneOffset}
import scala.jdk.CollectionConverters.*

// wrap google java classes into scala case classes
// this allows easier conversion to all the available formats and scalacheck generators
case class DataflowJob(
    id: String,
    name: String,
    projectId: String,
    `type`: String,
    location: String,
    createTime: LocalDateTime,
    startTime: LocalDateTime,
    endTime: Option[LocalDateTime],
    currentState: String,
    requestedState: String,
    environment: Environment,
    labels: Map[String, String],
    metrics: JobMetrics
)

object DataflowJob {
  private[DataflowJob] inline given Conversion[Timestamp, LocalDateTime] with {
    def apply(timestamp: Timestamp): LocalDateTime = {
      val instant = timestamp.toInstant(ZoneOffset.UTC)
      instant.atZone(ZoneOffset.UTC).toLocalDateTime
    }
  }

  given Conversion[Job, DataflowJob] with {
    def apply(job: Job): DataflowJob = {
      DataflowJob(
        id = job.getId,
        name = job.getName,
        projectId = job.getProjectId,
        `type` = job.getType.toString,
        location = job.getLocation,
        createTime = job.getCreateTime,
        startTime = job.getStartTime,
        endTime = None,
        currentState = job.getCurrentState.toString,
        requestedState = job.getRequestedState.toString,
        environment = job.getEnvironment,
        // shouldn't make an actual call here, but it's ok for now
        metrics = JobMetricsClient.getJobMetrics(job),
        labels = job.getLabelsMap.asScala.toMap
      )
    }
  }

}
