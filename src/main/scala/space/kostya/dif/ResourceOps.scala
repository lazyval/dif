package space.kostya.dif

object ResourceOps {
  def tryWithResource[T <: AutoCloseable, A](resource: T)(f: T => A): A = {
    try {
      f(resource)
    } finally {
      resource.close()
    }
  }
}
