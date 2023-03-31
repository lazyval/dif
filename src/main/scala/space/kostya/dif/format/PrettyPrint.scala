package space.kostya.dif.format

import space.kostya.dif.comp.Op

trait PrettyPrint {
  def format(firstId: String, secondId: String, diffs: List[Op]): List[String]
}
