package space.kostya.dif.model

case class JobEnvironment(
    tempStoragePrefix: String,
    clusterManagerApiService: String,
    experiments: List[String],
    serviceOptions: List[String],
    serviceKmsKeyName: String,
    dataset: String,
    serviceAccountEmail: String,
    flexResourceSchedulingGoal: Int,
    workerRegion: String,
    workerZone: String,
    shuffleMode: Int
)
