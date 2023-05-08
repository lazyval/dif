package space.kostya.dif.json

import io.circe.Decoder.Result
import io.circe.{Decoder, DecodingFailure, HCursor}
import space.kostya.dif.model.JobDescription
import scala.util.{Failure, Success, Try}
import java.time.{LocalDateTime, OffsetDateTime}
import java.time.format.DateTimeFormatter

object Parser {
  given Decoder[LocalDateTime] = Decoder.decodeString.emapTry { str =>
    // dataflow time is decoded with 'Z' in the end => OffsetDateTime
    Try(OffsetDateTime.parse(str).toLocalDateTime)
  }

  def readDescription(rawJson: String): Try[JobDescription] = {
    import io.circe.generic.auto._
    import io.circe.parser._

    decode[JobDescription](rawJson).toTry
  }
}
