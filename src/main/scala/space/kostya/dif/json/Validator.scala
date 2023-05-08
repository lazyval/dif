package space.kostya.dif.json

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import com.github.erosb.jsonsKema.*
import io.circe.Json
import space.kostya.dif.ResourceOps

trait Validator {
  def parse(rawString: String): Validated[Exception, Json]
}

object SkemaValidator extends Validator {

  val rawSchema = {
    import scala.io.Source
    val path         = "dataflow_job.schema"
    val schemaStream = getClass.getClassLoader.getResourceAsStream(path)
    ResourceOps.tryWithResource(Source.fromInputStream(schemaStream)) { schemaSource =>
      schemaSource.mkString
    }
  }

  val schemaJson: JsonValue = new JsonParser(rawSchema).parse()
  val schema                = new SchemaLoader(schemaJson).load()
  val validator             = Validator.forSchema(schema)

  override def parse(rawJson: String): Validated[Exception, Json] = {
    val skemaJson: JsonValue = new JsonParser(rawJson).parse()
    val circeJson            = io.circe.parser.parse(rawJson)

    val failure: ValidationFailure = validator.validate(skemaJson)
    if (failure == null) Valid(circeJson.right.get)
    else Invalid(new Exception(failure.toString))
  }
}
