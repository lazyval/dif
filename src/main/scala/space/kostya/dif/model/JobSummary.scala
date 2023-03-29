package space.kostya.dif.model

case class JobSummary(
    id: String,
    name: String,
    `type`: String,
    currentState: String,
    projectId: String
)
