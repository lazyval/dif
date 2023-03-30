package space.kostya.dif

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import space.kostya.dif.comp.Replace
import space.kostya.dif.ColorfulTree
import space.kostya.dif.ColorfulTree.Node

class PrettyPrintSpec extends AnyFlatSpec with Matchers {
  "pretty printer" should "rebuild list of diffs into a tree" in {
    val xs = List(
      Replace("/foo", "xxx", "yyy"),
      Replace("/foo/bar", "xxx", "yyy"),
      Replace("/foo/bar/baz", "xxx", "yyy")
    )
    val result = ColorfulTree.asTree(xs, Node("", List.empty, None))
    result shouldEqual
      """|/foo
         |└── /bar
         |    └── /baz
         |""".stripMargin
  }

  it should "print out all the values as is in simplified format" in {
    val xs = List(
      Replace("/foo", "yyy", "xxx"),
      Replace("/foo/bar", "yyy", "xxx"),
      Replace("/foo/bar/baz", "yyy", "xxx")
    )
    val lines: Seq[String] = PlainText.format("xxx", "yyy", xs)
    lines.mkString("\n") shouldEqual
      """|xxx|yyy|
         |/foo -> -xxx|+yyy|
         |/foo/bar -> -xxx|+yyy|
         |/foo/bar/baz -> -xxx|+yyy|""".stripMargin
  }
}
