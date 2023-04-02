package space.kostya.dif.model

import java.time.LocalDateTime

case class JobSummary(
    id: String,
    name: String,
    `type`: String,
    currentState: String,
    createTime: LocalDateTime,
    projectId: String
)
