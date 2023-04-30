package space.kostya.dif

import org.scalatest.TryValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

// it looks like Skema library focused on validation of property types
// in this case I expected to see exception for empty json: {}
// because json doesn't have properties defined in the schema
// yet it returns "null" as a validation result, which seems to encode "all good"
class SkemaValidationSpec extends AnyFlatSpec with Matchers {
  "validator based on skema lib" should "return exception for empty json" in {
    val json = "{}"
    val result = SkemaValidation.parse(json)
    result.isInvalid should equal(true)
  }
}
