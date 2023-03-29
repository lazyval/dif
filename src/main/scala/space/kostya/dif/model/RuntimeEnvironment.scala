package space.kostya.dif.model

case class RuntimeEnvironment(
    maxWorkers: Int,
    numWorkers: Int,
    zone: String,
    serviceAccountEmail: String,
    tempLocation: String,
    machineType: String,
    network: String,
    subnetwork: String,
    additionalExperiments: List[String],
    additionalUserLabels: Map[String, String],
    kmsKeyName: String,
    additionalWorkerHc: String,
    workerRegion: String,
    workerZone: String,
    enableStreamingEngine: Boolean,
    enableStreamingEngineProfiling: Boolean
)
