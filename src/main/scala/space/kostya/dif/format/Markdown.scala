package space.kostya.dif.format

import space.kostya.dif.json.DiffOp

object Markdown extends PrettyPrint {
  override def format(firstId: String, secondId: String, diffs: List[DiffOp]): List[String] = {
    val header = List(s"--- $firstId", s"+++ $secondId")
    val lines  = diffs.map(_.toString)
    header ++ lines
  }
}
