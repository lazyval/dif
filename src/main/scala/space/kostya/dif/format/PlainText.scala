package space.kostya.dif.format

import space.kostya.dif.json.{Add, Copy, Move, DiffOp, Remove, Replace, Test}

object PlainText extends PrettyPrint {
  override def format(firstId: String, secondId: String, diffs: List[DiffOp]): List[String] = {
    val header = List(s"$firstId|$secondId|")
    val lines  = diffs.map(strOp)
    header ++ lines
  }
  def strOp(op: DiffOp): String = op match {
    case Add(path, value)  => s"$path -> <none>|+$value|"
    case Remove(path, old) => s"$path -> -$old|<none>|"
    case Replace(path, value, old) =>
      s"$path -> -$old|+$value|"
    case Move(from, to)    => s"$from => $to|"
    case Copy(from, to)    => s"$from => $to|"
    case Test(path, value) => s"$path ? $value|"
  }
}
