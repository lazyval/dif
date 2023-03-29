package space.kostya.dif

import io.circe.JsonObject
import org.scalacheck.Arbitrary
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import space.kostya.dif.model.JobSummary
import space.kostya.dif.comp._

class ComparsionSpec extends AnyFlatSpec with Matchers {
  import Differ.jobSummaryComparison
  "comparison" should "return empty list on identical classes" in {
    val randomSummary: JobSummary = JobSummary(
      id = "xxx",
      name = "yyy",
      `type` = "BATCH",
      currentState = "FINISHED",
      projectId = "sandbox"
    )

    jobSummaryComparison.vs(randomSummary, randomSummary) shouldBe empty
  }
  it should "yield right modification ops" in {
    val x: JobSummary = JobSummary(
      id = "xxx",
      name = "yyy",
      `type` = "BATCH",
      currentState = "FINISHED",
      projectId = "sandbox"
    )
    val y: JobSummary = x.copy(name = "zzz", currentState = "FAILED")

    jobSummaryComparison.vs(x, y) shouldBe (
      List(
        Replace("/name", "zzz"),
        Replace("/currentState", "FAILED")
      )
    )
  }

  it should "yield replace, even if values have common prefix" in {
    val original    = "bq-job_1020"
    val replacement = "bq-job_1021"

    val x: JobSummary = JobSummary(
      id = original,
      name = "yyy",
      `type` = "BATCH",
      currentState = "FINISHED",
      projectId = "sandbox"
    )
    val y: JobSummary = x.copy(id = replacement)

    jobSummaryComparison.vs(x, y) shouldBe (
      List(
        Replace("/id", replacement)
      )
    )
  }
}
