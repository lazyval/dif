package space.kostya.dif.google

import com.google.dataflow.v1beta3.{GetJobMetricsRequest, Job, JobMetrics, MetricsV1Beta3Client}
import space.kostya.dif.ResourceOps

object JobMetricsClient {
  def getJobMetrics(job: Job): JobMetrics = {
    val jobMetrics = ResourceOps.tryWithResource(MetricsV1Beta3Client.create()) { client =>
      val request: GetJobMetricsRequest = GetJobMetricsRequest
        .newBuilder()
        .setProjectId(job.getProjectId)
        .setJobId(job.getId)
        .build()

      client.getJobMetrics(request)
    }
    jobMetrics
  }
}
