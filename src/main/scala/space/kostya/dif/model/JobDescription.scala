package space.kostya.dif.model

import java.time.LocalDateTime

case class JobDescription(
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
    environment: JobEnvironment,
    labels: Map[String, String],
    metrics: JobMetrics
)
