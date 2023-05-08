package space.kostya.dif

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import space.kostya.dif.json.{Remove, Replace, Add}
import space.kostya.dif.format.ColorfulTree
import space.kostya.dif.format.PlainText

class PrettyPrintSpec extends AnyFlatSpec with Matchers {
  "pretty printer" should "rebuild list of diffs into a tree" in {
    val xs = List(
      Replace("/country/city", "aaa", "xxx"),
      Replace("/country/zip", "bbb", "xxx"),
      Replace("/country/zap", "ccc", "xxx"),
      Add("/xxx", "yyy")
    )
    val result = new ColorfulTree(noColor = true)
      .format("first", "second", diffs = xs)
    val expected: String =
      """|xxx <none> -> yyy
         |country
         |  city aaa -> xxx
         |  zip bbb -> xxx
         |  zap ccc -> xxx
         |""".stripMargin

    result shouldEqual expected.split("\n").toList
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
