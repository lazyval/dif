package space.kostya.dif.format

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import space.kostya.dif.comp.Replace
import space.kostya.dif.format.Trie.{Node, Empty, Leaf}

class TrieSpec extends AnyFlatSpec with Matchers {
  "trie" should "start as empty" in {
    val initial = Trie.empty

    initial should equal(Empty)
  }

  it should "insert values which haven't appeared previously" in {
    val initial = Trie.empty
    val op      = Replace("/foo/bar/baz", "<none>", "<none>")
    val result  = Trie.fromPath(op.path, op)

    val expectation = {
      val baz = Leaf("baz", op)
      val bar = Node("bar", children = List(baz))
      val foo = Node("foo", children = List(bar))
      foo
    }

    initial should equal(Trie.Empty)
    result should equal(expectation)
  }

  it should "correctly merge two plain tries" in {
    val initial = Trie.empty
    val first   = Replace("/foo/bar/baz", "<none>", "<none>")
    val second  = Replace("/foo/baz/bar", "<none>", "<none>")

    val firstRes  = Trie.fromPath(first.path, first)
    val secondRes = Trie.fromPath(second.path, second)
    val res       = Trie.merge(firstRes, secondRes)

    val expectation: Trie = {
      val baz    = Leaf("baz", first)
      val bar    = Leaf("bar", second)
      val barbaz = Node("bar", children = List(baz))
      val bazbar = Node("baz", children = List(bar))
      val foo    = Node("foo", children = List(barbaz, bazbar))
      foo
    }

    initial should equal(Trie.Empty)
    res should equal(expectation)
  }

  it should "correctly merge three tries" in {
    val initial = Trie.empty
    val first   = Replace("/foo/bar/baz", "<none>", "<none>")
    val second  = Replace("/foo/baz/bar", "<none>", "<none>")
    val third   = Replace("/foo/baz/ziz", "<none>", "<none>")

    val firstRes  = Trie.fromPath(first.path, first)
    val secondRes = Trie.fromPath(second.path, second)
    val thirdRes  = Trie.fromPath(third.path, third)
    val res = {
      val pair = Trie.merge(firstRes, secondRes)
      Trie.merge(pair, thirdRes, Empty)
    }

    val expectation: Trie = {
      val barbaz = Node("bar", children = List(Leaf("baz", first)))
      val baz    = Node("baz", children = List(Leaf("bar", second), Leaf("ziz", third)))
      val foo    = Node("foo", children = List(barbaz, baz))
      foo
    }

    initial should equal(Trie.Empty)
    res should equal(expectation)
  }

  it should "correctly multiple values of different nesting" in {
    val initial = Trie.empty
    val first   = Replace("/xxx", "<none>", "<none>")
    val second  = Replace("/foo/baz/bar", "<none>", "<none>")
    val third   = Replace("/foo/bar/baz", "<none>", "<none>")

    val firstRes  = Trie.fromPath(first.path, first)
    val secondRes = Trie.fromPath(second.path, second)
    val thirdRes  = Trie.fromPath(third.path, third)
    val res = {
      val pair = Trie.merge(firstRes, secondRes)
      Trie.merge(pair, thirdRes)
    }

    val expectation: Trie = {
      val xxx = Leaf("xxx", first)

      val foo = Node(
        "foo",
        children = List(
          Trie.fromPath("/bar/baz", third),
          Trie.fromPath("/baz/bar", second)
        )
      )
      Node("/", children = List(xxx, foo))
    }

    initial should equal(Trie.Empty)
    res should equal(expectation)
  }

  it should "correctly join multiple values when short inserted last" in {
    val initial = Trie.empty
    val first   = Replace("/foo/bar/baz", "<none>", "<none>")
    val second  = Replace("/foo/baz/bar", "<none>", "<none>")
    val third   = Replace("/xxx", "<none>", "<none>")

    val firstRes  = Trie.fromPath(first.path, first)
    val secondRes = Trie.fromPath(second.path, second)
    val thirdRes  = Trie.fromPath(third.path, third)
    val res = {
      val pair = Trie.merge(firstRes, secondRes)
      Trie.merge(pair, thirdRes)
    }

    val expectation: Trie = {
      val xxx = Leaf("xxx", third)
      val foo = Node(
        "foo",
        children = List(
          Trie.fromPath("/bar/baz", first),
          Trie.fromPath("/baz/bar", second)
        )
      )
      Node("/", children = List(xxx, foo))
    }

    initial should equal(Trie.Empty)
    res should equal(expectation)
  }

  it should "correctly join multiple values when they are on the very first level" in {
    val initial = Trie.empty
    val first   = Replace("/name", "<none>", "<none>")
    val second  = Replace("/id", "<none>", "<none>")

    val firstRes  = Trie.fromPath(first.path, first)
    val secondRes = Trie.fromPath(second.path, second)
    val res       = Trie.merge(firstRes, secondRes)

    val expectation: Trie = {
      Node(
        "/",
        children = List(
          Trie.fromPath("/name", first),
          Trie.fromPath("/id", second)
        )
      )
    }

    initial should equal(Trie.Empty)
    res should equal(expectation)
  }
}
