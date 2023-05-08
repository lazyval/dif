package space.kostya.dif.json

import org.scalatest.TryValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import space.kostya.dif.json.SkemaValidation

class SkemaValidationSpec extends AnyFlatSpec with Matchers {
  "validator based on skema lib" should "return exception for empty json" in {
    val json = "{}"
    val result = SkemaValidation.parse(json)
    result.isInvalid should equal(true)
  }
}
