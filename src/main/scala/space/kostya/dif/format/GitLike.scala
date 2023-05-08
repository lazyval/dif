package space.kostya.dif.format

import space.kostya.dif.json.DiffOp

object GitLike extends PrettyPrint {
  override def format(firstId: String, secondId: String, diffs: List[DiffOp]): List[String] = {
    ???
  }
}
