package space.kostya.dif.model
import com.google.dataflow.v1beta3.{JobMetrics => JJobMetrics}

case class JobMetrics(
    jobName: String,
    jobID: String,
    jobType: String,
    startTime: Long,
    endTime: Long,
    state: String,
    mapProgress: Double,
    reduceProgress: Double,
    mapCounters: Map[String, Long],
    reduceCounters: Map[String, Long]
)
