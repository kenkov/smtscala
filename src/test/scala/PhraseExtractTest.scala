package jp.kenkov.smt.test
import org.scalatest.FunSuite
import scala.collection.mutable.{Map => MMap}
import jp.kenkov.smt.{_}

import jp.kenkov.smt.phrase.{PhraseExtract}

class PhraseExtraceTest extends FunSuite {

  test("extract test 3x5") {
    // next alignment matrix is like
    // 
    // | |x|x| | |
    // |x| | |x| |
    // | | | | |x|
    //
    val es = (1 to 3).toList.map(_.toString)
    val fs = (1 to 5).toList.map(_.toString)
    val alignment = Set((2, 1),
                        (1, 2),
                        (1, 3),
                        (2, 4),
                        (3, 5))
    val ans = Set((1, 1, 2, 3), (1, 3, 1, 5), (3, 3, 5, 5), (1, 2, 1, 4))
    expect (ans) {
      PhraseExtract.extract(es, fs, alignment)
    }
  }

  test("extract test 9x8") {
    // next alignment matrix is like
    //
    // |x| | | | | | | | | |
    // | |x|x|x| | | | | | |
    // | | | | | |x| | | | |
    // | | | | | | |x| | | |
    // | | | | | | | | | |x|
    // | | | | | | | | | |x|
    // | | | | | | | |x| | |
    // | | | | | | | |x| | |
    // | | | | | | | | |x| |
    //
    val es = "michael assumes that he will stay in the house".split("[ ]+").toList
    val fs = "michael geht davon aus , dass er im haus bleibt".split("[ ]+").toList
    val alignment = Set((1, 1),
                        (2, 2),
                        (2, 3),
                        (2, 4),
                        (3, 6),
                        (4, 7),
                        (5, 10),
                        (6, 10),
                        (7, 8),
                        (8, 8),
                        (9, 9))

    val ans = Set((1, 1, 1, 1),
                  (1, 2, 1, 4),
                  (1, 2, 1, 5),
                  (1, 3, 1, 6),
                  (1, 4, 1, 7),
                  (1, 9, 1, 10),
                  (2, 2, 2, 4),
                  (2, 2, 2, 5),
                  (2, 3, 2, 6),
                  (2, 4, 2, 7),
                  (2, 9, 2, 10),
                  (3, 3, 5, 6),
                  (3, 3, 6, 6),
                  (3, 4, 5, 7),
                  (3, 4, 6, 7),
                  (3, 9, 5, 10),
                  (3, 9, 6, 10),
                  (4, 4, 7, 7),
                  (4, 9, 7, 10),
                  (5, 6, 10, 10),
                  (5, 9, 8, 10),
                  (7, 8, 8, 8),
                  (7, 9, 8, 9),
                  (9, 9, 9, 9))

    expect (ans) {
      PhraseExtract.extract(es, fs, alignment)
    }
  }

  test ("phrase extrace test 1") {
    // next alignment matrix is like
    // 
    // |x| | | | | | | | | |
    // | |x|x|x| | | | | | |
    // | | | | | |x| | | | |
    // | | | | | | |x| | | |
    // | | | | | | | | | |x|
    // | | | | | | | | | |x|
    // | | | | | | | |x| | |
    // | | | | | | | |x| | |
    // | | | | | | | | |x| |
    // 
    val es = "michael assumes that he will stay in the house".split("[ ]+").toList
    val fs = "michael geht davon aus , dass er im haus bleibt".split("[ ]+").toList
    val alignment = Set((1, 1),
                        (2, 2),
                        (2, 3),
                        (2, 4),
                        (3, 6),
                        (4, 7),
                        (5, 10),
                        (6, 10),
                        (7, 8),
                        (8, 8),
                        (9, 9))
    val ans = Set((List("assumes"),
                   List("geht", "davon", "aus")),
                  (List("assumes"),
                   List("geht", "davon", "aus", ",")),
                  (List("assumes", "that"),
                   List("geht", "davon", "aus", ",", "dass")),
                  (List("assumes", "that", "he"),
                   List("geht", "davon", "aus", ",", "dass", "er")),
                  (List("assumes", "that", "he", "will", "stay", "in", "the", "house"),
                   List("geht", "davon", "aus", ",", "dass", "er", "im", "haus", "bleibt")),
                  (List("he"),
                   List("er")),
                  (List("he", "will", "stay", "in", "the", "house"),
                   List("er", "im", "haus", "bleibt")),
                  (List("house"),
                   List("haus")),
                  (List("in", "the"),
                   List("im")),
                  (List("in", "the", "house"),
                   List("im", "haus")),
                  (List("michael"),
                   List("michael")),
                  (List("michael", "assumes"),
                   List("michael", "geht", "davon", "aus")),
                  (List("michael", "assumes"),
                   List("michael", "geht", "davon", "aus", ",")),
                  (List("michael", "assumes", "that"),
                   List("michael", "geht", "davon", "aus", ",", "dass")),
                  (List("michael", "assumes", "that", "he"),
                   List("michael", "geht", "davon", "aus", ",", "dass", "er")),
                  (List("michael", "assumes", "that", "he", "will", "stay", "in", "the", "house"),
                   List("michael", "geht", "davon", "aus", ",", "dass", "er", "im", "haus", "bleibt")),
                  (List("that"),
                   List(",", "dass")),
                  (List("that"),
                   List("dass")),
                  (List("that", "he"),
                   List(",", "dass", "er")),
                  (List("that", "he"),
                   List("dass", "er")),
                  (List("that", "he", "will", "stay", "in", "the", "house"),
                   List(",", "dass", "er", "im", "haus", "bleibt")),
                  (List("that", "he", "will", "stay", "in", "the", "house"),
                   List("dass", "er", "im", "haus", "bleibt")),
                  (List("will", "stay"),
                   List("bleibt")),
                  (List("will", "stay", "in", "the", "house"),
                   List("im", "haus", "bleibt")))
    expect(ans) {
      PhraseExtract.phraseExtract(es, fs, alignment)
    }
  }
}
