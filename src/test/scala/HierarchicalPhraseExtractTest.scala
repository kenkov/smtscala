package jp.kenkov.smt.test

import org.scalatest.FunSuite
import scala.collection.mutable.{Map => MMap}
import jp.kenkov.smt.{_}

import jp.kenkov.smt.phrase.{HierarchicalPhraseExtract}

class HierarchicalPhraseExtractTest extends FunSuite {

  test("splitN test for item more than N") {
    val lst = 1 to 5 toList
    val ans = List((List(), List(1, 2, 3), List(4, 5)),
                   (List(1), List(2, 3, 4), List(5)),
                   (List(1, 2), List(3, 4, 5), List()))
    expect (ans) {
      HierarchicalPhraseExtract.splitN(3, lst)
    }
  }

  test("splitN test for item less than N") {
    val lst = 1 to 5 toList
    val ans = List()
    expect (ans) {
      HierarchicalPhraseExtract.splitN(7, lst)
    }
  }

  test("splitN test for item equal N") {
    val lst = 1 to 5 toList
    val ans = List((List(), List(1, 2, 3, 4, 5), List()))
    expect (ans) {
      HierarchicalPhraseExtract.splitN(5, lst)
    }
  }

  test("extract test 2x2") {
    val pRange: PhraseRange = Set((1, 1, 1, 1), (2, 2, 2, 2), (1, 2, 1, 2))

    val ans = Set((List(1, 2), List(1, 2)),
                  (List(1), List(1)),
                  (List(2), List(2)),
                  (List(1, -1), List(1, -1)),
                  (List(-1, 2), List(-1, 2)),
                  (List(-2, -1), List(-2, -1)),
                  (List(-1, -2), List(-1, -2)))
    expect (ans) {
      HierarchicalPhraseExtract.extract(pRange)
    }
  }

  test("extract test 3x5") {
    val pRange: PhraseRange = Set((1, 1, 2, 3), (1, 3, 1, 5), (3, 3, 5, 5), (1, 2, 1, 4))

    val ans = Set(((1 to 3).toList, (1 to 5).toList),
                  (List(1), List(2, 3)),
                  (List(3), List(5)),
                  (List(1, 2), (1 to 4).toList),
                  // first iteration
                  (List(-1, 2, 3), List(1, -1, 4, 5)),
                  (List(1, 2, -1), List(1, 2, 3, 4, -1)),
                  (List(-1, 3), List(-1, 5)),
                  (List(-1, 2), List(1, -1, 4)),
                  // second iteration
                  (List(-1, 2, -2), List(1, -1, 4, -2)),
                  (List(-2, 2, -1), List(1, -2, 4, -1)),
                  (List(-2, -1), List(-2, -1)),
                  (List(-1, -2), List(-1, -2)))
    expect (ans) {
      HierarchicalPhraseExtract.extract(pRange)
    }
  }
}
