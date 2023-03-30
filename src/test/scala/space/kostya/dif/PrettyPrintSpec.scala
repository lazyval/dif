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
}
