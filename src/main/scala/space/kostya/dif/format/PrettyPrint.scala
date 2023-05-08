package space.kostya.dif.format

import space.kostya.dif.json.DiffOp

trait PrettyPrint {
  def format(firstId: String, secondId: String, diffs: List[DiffOp]): List[String]
}
