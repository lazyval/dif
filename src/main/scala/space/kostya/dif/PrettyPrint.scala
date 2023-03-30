package space.kostya.dif

import space.kostya.dif.comp._

trait PrettyPrint {
  def format(firstId: String, secondId: String, diffs: List[Op]): List[String]
}

object PlainText extends PrettyPrint {
  override def format(firstId: String, secondId: String, diffs: List[Op]): List[String] = {
    val header = List(s"$firstId|$secondId|")
    val lines  = diffs.map(strOp)
    header ++ lines
  }
  def strOp(op: Op): String = op match {
    case Add(path, value)  => s"$path -> <none>|+$value|"
    case Remove(path, old) => s"$path -> -$old|<none>|"
    case Replace(path, value, old) =>
      s"$path -> -$old|+$value|"
    case Move(from, to) => s"$from => $to|"
    case Copy(from, to) => s"$from => $to|"
  }
}

object Markdown extends PrettyPrint {
  override def format(firstId: String, secondId: String, diffs: List[Op]): List[String] = {
    val header = List(s"--- $firstId", s"+++ $secondId")
    val lines  = diffs.map(_.toString)
    header ++ lines
  }
}

object GitLike extends PrettyPrint {
  override def format(firstId: String, secondId: String, diffs: List[Op]): List[String] = {
    ???
  }
}

object Tablify extends PrettyPrint {
  override def format(firstId: String, secondId: String, diffs: List[Op]): List[String] = {
    ???
  }
}

object ColorfulTree extends PrettyPrint {
  // recursively builds a tree of diffs based on the path values, where each / represents a level in the tree
  // then prints the tree with colors, where each level is indented by 2 spaces
  override def format(firstId: String, secondId: String, diffs: List[Op]): List[String] = {
    val tree  = asTree(diffs, Node("", List.empty, None))
    val lines = ???
    lines
  }

  sealed trait Tree
  case class Node(path: String, children: List[Tree], op: Option[Op]) extends Tree {
    def add(child: Tree): Node = copy(children = child :: children)

    // prints out the tree (the path field), where root will be printed first,
    // then its children, with indentation of 2 spaces
    // and a └── or ├── prefix for the last and non-last children respectively
    override def toString: String = ???
  }

  case class Leaf(path: String, op: Op) extends Tree {
    override def toString: String = s"$op"
  }

  def asTree(diffs: List[Op], acc: Tree): Tree = diffs match {
    case Nil => acc
    case op :: tail =>
      val path            = op.path
      val (parentPath, _) = path.splitAt(path.lastIndexOf('/'))
      val parent = acc match {
        // if the parent path is the same as the current path, then we add the current op as a child
        case Node(p, children, originalOp) if p == parentPath =>
          Node(p, children, originalOp).add(Leaf(path, op))
        // else, check all the children of the current node for the parent path
        case Node(p, children, originalOp) =>
          Node(path, children.map(asTree(List(op), _)), originalOp)
        case Leaf(p, originalOp) =>
          Node(p, List(Leaf(path, op)), Some(originalOp))
      }
      asTree(tail, parent)
  }

}
