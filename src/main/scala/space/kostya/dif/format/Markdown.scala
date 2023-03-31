package space.kostya.dif.format

import space.kostya.dif.comp.Op

object Markdown extends PrettyPrint {
  override def format(firstId: String, secondId: String, diffs: List[Op]): List[String] = {
    val header = List(s"--- $firstId", s"+++ $secondId")
    val lines  = diffs.map(_.toString)
    header ++ lines
  }
}
