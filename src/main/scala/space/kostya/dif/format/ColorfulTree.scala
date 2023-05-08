package space.kostya.dif.format

import space.kostya.dif.format.PrettyPrint

import space.kostya.dif.json.DiffOp
import space.kostya.dif.json._
import space.kostya.dif.format.Trie._

class ColorfulTree(val noColor: Boolean) extends PrettyPrint with Colors {
  override def format(firstId: String, secondId: String, diffs: List[DiffOp]): List[String] = {
    val seed: Trie = Trie.Empty
    val trie: Trie = diffs.foldLeft(seed) { case (acc, op) =>
      val t = Trie.fromPath(op.path, op)
      Trie.merge(acc, t)
    }

    render(trie)
  }

  def render(trie: Trie, indent: Int = -2, step: Int = 2): List[String] = trie match {
    case Node("/", children) =>
      children.flatMap(c => render(c, indent, step))
    case Node(token, children) =>
      val space = " " * (indent + step)
      val line  = List(s"$space$token")
      line ++ children.flatMap(c => render(c, indent + step, step))
    case Empty => List(" <> ")
    case Leaf(token, op) =>
      val space = " " * (indent + step)
      List(s"$space$token ${render(op)}")
  }

  def render(op: DiffOp): String = op match {
    case Add(_, value)                  => s"${faded("<none>")} -> ${green(value.toString)}"
    case Remove(_, value)               => s"${redStriked(value)} -> ${faded("<none>")}"
    case Replace(_, oldValue, newValue) => s"${redStriked(oldValue)} -> ${green(newValue)}"
    case Move(from, to)                 => s"${color("blue", ">>")} $from -> $to"
    case Copy(from, to)                 => s"${color("blue", ">>")} $from -> $to"
    case Test(path, value)              => s"${color("cyan", "?")} ${path.mkString("/")} = $value"
  }

}

trait Colors {

  def noColor: Boolean

  def color(color: String, text: String): String =
    if (noColor) text else s"\u001b[${color}m$text\u001b[0m"

  def strikeThrough(text: String): String = if (noColor) text else s"\u001b[9m$text\u001b[0m"

  def red(text: String): String = color("31", text)

  def redStriked(text: String): String = color("31", strikeThrough(text))

  def green(text: String): String = color("32", text)

  def faded(text: String): String = color("90", text)
}
