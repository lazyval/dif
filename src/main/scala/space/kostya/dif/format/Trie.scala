package space.kostya.dif.format

import scala.annotation.tailrec
import scala.collection.immutable.List
import space.kostya.dif.json.DiffOp

sealed trait Trie {
  def token: String
}

object Trie {
  def empty: Trie = Empty

  def fromPath(path: String, op: DiffOp): Trie = {
    val tokens = path.drop(1).split("/")

    tokens.foldRight(Trie.empty) {
      case (token, Empty)                => Leaf(token, op)
      case (token, Leaf(prev, op))       => Node(token, List(Leaf(prev, op)))
      case (token, Node(prev, children)) => Node(token, List(Node(prev, children)))
    }
  }
  @annotation.tailrec
  def merge(left: Trie, right: Trie, prev: Trie = Empty): Trie = (left, right) match {
    case (Empty, other) => other
    case (other, Empty) => merge(Empty, other, prev)
    case (Leaf(x, op), Leaf(y, _)) =>
      if (x == y) {
        throw new IllegalStateException(s"Leaf $left cannot share key with $right")
      } else {
        Node(prev.token, List(left, right))
      }
    case (Leaf(x, op), Node(key, _)) =>
      if (x == key) {
        throw new IllegalStateException(s"Leaf $left cannot share key with non-leaf $right")
      } else {
        Node(prev.token, List(left, right))
      }
    case (_, _: Leaf) => merge(right, left, prev)
    case (Node(aStr, aChildren), Node(bStr, bChildren)) =>
      if (aStr == "/") {
        Node(aStr, mergeChildren(aChildren, List(right), prev))
      } else if (bStr == "/") {
        Node(bStr, mergeChildren(bChildren, List(left), prev))
      } else {
        Node(aStr, mergeChildren(aChildren, bChildren, prev))
      }
  }

  def mergeChildren(xs: List[Trie], ys: List[Trie], prev: Trie): List[Trie] = {
    val repetitionsPerToken = (xs ++ ys).groupBy(_.token).values.map(_.toList).toList
    repetitionsPerToken.flatMap {
      case List(uniq)          => List(uniq)
      case List(first, second) => List(merge(first, second, prev))
      case illegal =>
        val msg = s"merge is done for two sides, so at most 2 keys are expected, but got $illegal"
        throw new IllegalStateException(msg)
    }
  }
  case object Empty extends Trie {
    override val token: String = "/"
  }
  case class Leaf(token: String, value: DiffOp)        extends Trie
  case class Node(token: String, children: List[Trie]) extends Trie
}
