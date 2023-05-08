package space.kostya.dif.json

import io.circe.{Decoder, Json}
import io.circe.generic.semiauto.deriveDecoder

// operations from https://tools.ietf.org/html/rfc6902
// slimmer version of diffson operations
sealed trait DiffOp { def path: String }
case class Replace(path: String, value: String, old: String) extends DiffOp
case class Add(path: String, value: String)                  extends DiffOp
case class Remove(path: String, old: String)                 extends DiffOp
case class Move(from: String, path: String)                  extends DiffOp
case class Copy(from: String, path: String)                  extends DiffOp
case class Test(path: String, value: Json)                   extends DiffOp
given diffsonConversion: Conversion[diffson.jsonpatch.Operation[Json], DiffOp] = {
  case diffson.jsonpatch.Replace(path, value, old) =>
    val original = old.map(_.toString).getOrElse("")
    Replace(path.toString, value.toString, original)
  case diffson.jsonpatch.Add(path, value) => Add(path.toString, value.toString)
  case diffson.jsonpatch.Remove(path, old) =>
    val original = old.map(_.toString).getOrElse("")
    Remove(path.toString, original)
  case diffson.jsonpatch.Move(from, path)  => Move(from.toString, path.toString)
  case diffson.jsonpatch.Copy(from, path)  => Copy(from.toString, path.toString)
  case diffson.jsonpatch.Test(path, value) => Test(path.toString, value)
}

given opDecoder: Decoder[DiffOp] = (c: io.circe.HCursor) => {
  val op = c.downField("op").as[String].getOrElse("")
  op match {
    case "replace" => c.as[Replace]
    case "add"     => c.as[Add]
    case "remove"  => c.as[Remove]
    case "move"    => c.as[Move]
    case "copy"    => c.as[Copy]
    case "test"    => c.as[Test]
    case _         => Left(io.circe.DecodingFailure("Unknown op", c.history))
  }
}

inline given ReplaceDecoder: Decoder[Replace] = deriveDecoder[Replace]
inline given AddDecoder: Decoder[Add]         = deriveDecoder[Add]
inline given RemoveDecoder: Decoder[Remove]   = deriveDecoder[Remove]
inline given MoveDecoder: Decoder[Move]       = deriveDecoder[Move]
inline given CopyDecoder: Decoder[Copy]       = deriveDecoder[Copy]
inline given TestDecoder: Decoder[Test]       = deriveDecoder[Test]
