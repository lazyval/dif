package space.kostya.dif

import diffson.jsonpatch.Operation
import io.circe.generic.semiauto.deriveDecoder
import io.circe.{Decoder, Encoder, Json, JsonObject}
import space.kostya.dif.json.DiffOp
import space.kostya.dif.model.JobSummary
import space.kostya.dif.model.*

trait Comparison[T] {
  def vs(x: T, y: T): List[DiffOp]
}
object Differ {
  import diffson._
  import diffson.lcs._
  import diffson.circe._
  import diffson.jsonpatch._
  import diffson.jsonpatch.lcsdiff._
  import diffson.jsonpatch.JsonDiff

  given Lcs[Json] = new Patience[Json]()

  given circeDiffer: Diff[Json, JsonPatch[Json]] =
    new JsonDiff[Json](diffArray = true, rememberOld = true)

  given jobSummaryComparison: Comparison[JobSummary] = new Comparison[JobSummary] {

    def vs(x: JobSummary, y: JobSummary): List[DiffOp] = {
      import io.circe.syntax._
      import io.circe.generic.auto._
      import io.circe.generic.semiauto._

      val xJson: io.circe.Json = x.asJson
      val yJson: io.circe.Json = y.asJson

      // use diffson-circe to convert the diff right away
      // then consume the result as Dif classes
      // it's an extra copy, but helps to avoid dealing with internal diffson types
      val originalDiff = circeDiffer.diff(xJson, yJson).asJson
      // import to shadow circe auto / semiauto decoders
      import space.kostya.dif.json.opDecoder
      originalDiff.as[List[DiffOp]] match {
        case Right(ops) => ops
        case Left(err)  => throw new Exception(err.message)
      }
    }
  }
}
