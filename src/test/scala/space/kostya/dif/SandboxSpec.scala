package space.kostya.dif
import org.scalatest.TryValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import space.kostya.dif.model.{JobDescription, JobSummary}

import scala.util.{Failure, Success, Try}

class SandboxSpec extends AnyFlatSpec with Matchers with TryValues {
  "sandbox" should "parse files from the local resource dif" in {
    val result: Try[List[JobSummary]] = Sandbox.FromResources.listJobs()
    result.success.value should have size (5)
  }

  it should "find the job by id" in {
    val id                          = "2022-05-30_05_34_56-1234567890123456789"
    val result: Try[JobDescription] = Sandbox.FromResources.describeJob(id)
    result.success.value.id should be(id)
  }

  it should "return failure if no job can be found" in {
    Sandbox.FromResources.describeJob("foo").isSuccess should be(false)
  }
}
