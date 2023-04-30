package space.kostya.dif

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import com.github.erosb.jsonsKema.{JsonParser, JsonValue, SchemaLoader, ValidationFailure, Validator}
import io.circe.Json

trait ValidatingParser {
  def parse(rawString: String): Validated[Exception, Json]
}

@deprecated("see unit test", "0.1.0")
object SkemaValidation extends ValidatingParser {

  val rawSchema = {
    import scala.io.Source
    val path = "dataflow_job.schema"
    val schemaStream = getClass.getClassLoader.getResourceAsStream(path)
    val schemaSource = Source.fromInputStream(schemaStream)
    val schemaString = schemaSource.mkString
    schemaSource.close()
    schemaString
  }


  val schemaJson: JsonValue = new JsonParser(rawSchema).parse()
  val schema = new SchemaLoader(schemaJson).load()
  val validator = Validator.forSchema(schema)

  override def parse(rawJson: String): Validated[Exception, Json] = {
    val skemaJson: JsonValue = new JsonParser(rawJson).parse()
    val circeJson = io.circe.parser.parse(rawJson)

    val failure: ValidationFailure = validator.validate(skemaJson)
    println(s"failure = $failure")
    if (failure == null) Valid(circeJson.right.get)
    else Invalid(new Exception(failure.toString))
  }
}