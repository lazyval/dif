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
    // wrapping labels into option, otherwise circe gets confused
    // when labels is not defined in the json
    labels: Option[Map[String, String]]
    //    environment: JobEnvironment,
    //    metrics: JobMetrics
)
