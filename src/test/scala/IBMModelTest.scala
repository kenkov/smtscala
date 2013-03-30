package jp.kenkov.smt.test
import org.scalatest.FunSuite
import scala.collection.mutable.{Map => MMap}
import jp.kenkov.smt.{_}

import jp.kenkov.smt.ibmmodel.{IBMModel1, IBMModel2, Alignment}

class IBMModel1Test extends FunSuite {

  test("ibmmodel1 test for loop initialize") {
    val corpus: Corpus = List(("the house", "das Haus"),
                              ("the book", "das Buch"),
                              ("a book", "ein Buch"))
    val ans: MMap[(TargetWord, SourceWord), Double] = MMap((("house", "Haus"), 0.5),
                                                             (("book", "ein"), 0.5),
                                                             (("the", "das"), 0.5),
                                                             (("the", "Buch"), 0.25),
                                                             (("book", "Buch"), 0.5),
                                                             (("a", "ein"), 0.5),
                                                             (("book", "das"), 0.25),
                                                             (("the", "Haus"), 0.5),
                                                             (("house", "das"), 0.25),
                                                             (("a", "Buch"), 0.25))
    val train = new IBMModel1(mkTokenizedCorpus(corpus), loopCount=1).train
    expect(ans) { train }
  }

  test("ibmmodel1 test for loop 1") {
    val corpus: Corpus = List(("the house", "das Haus"),
                              ("the book", "das Buch"),
                              ("a book", "ein Buch"))
    val ans: MMap[(TargetWord, SourceWord), Double] = MMap(("house", "Haus") -> 0.5714285714285715,
                                                             ("book", "ein") -> 0.4285714285714286,
                                                             ("a", "Buch") -> 0.18181818181818182,
                                                             ("book", "das") -> 0.18181818181818182,
                                                             ("house", "das") -> 0.18181818181818182,
                                                             ("the", "das") -> 0.6363636363636364,
                                                             ("the", "Haus") -> 0.4285714285714286,
                                                             ("the", "Buch") -> 0.18181818181818182,
                                                             ("a", "ein") -> 0.5714285714285715,
                                                             ("book", "Buch") -> 0.6363636363636364)
    val train = new IBMModel1(mkTokenizedCorpus(corpus), loopCount=2).train
    expect(ans) { train }
  }
}


class AlignmentTest extends FunSuite {

  test("viterbi alignment test") {
    val corpus: List[(TargetSentence, SourceSentence)] =
      List(("the house", "das Haus"),
           ("the book", "das Buch"),
           ("a book", "ein Buch"))
    val tCorpus = mkTokenizedCorpus(corpus)
    val (t, a) = new IBMModel2(tCorpus, 10).train
    val es: TargetWords = List("the", "house")
    val fs: SourceWords = List("das", "Haus")
    val ans = Alignment.viterbiAlignment(es, fs, t, a)
    expect(ans) {
      MMap(1 -> 1, 2 -> 2)
    }
  }

  test("_alignment test 1") {

    val eList = "michael assumes that he will stay in the house".split("[ ]+").toList
    val fList = "michael geht davon aus , dass er im haus bleibt".split("[ ]+").toList
    val e2f = Set((1, 1), (2, 2), (2, 3), (2, 4), (3, 6),
                  (4, 7), (7, 8), (9, 9), (6, 10))
    val f2e = Set((1, 1), (2, 2), (3, 6), (4, 7), (7, 8),
                  (8, 8), (9, 9), (5, 10), (6, 10))
    val ans = Set((1, 1),
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
    expect (ans) {
    Alignment._alignment(eList, fList, e2f, f2e)
    }
  }

  test("_alignment test 2") {
    val es = "私 は 先生 です".split("[ ]+").toList
    val fs = "I am a teacher".split("[ ]+").toList
    val e2f = Set((1,2), (3,4), (1,1), (2,3))
    val f2e = Set((2,3), (4,3), (1,1), (3,4))
    val ans = Set((3,4), (1,1), (2,3), (1,2), (4,3))

    expect (ans) {
      Alignment._alignment(es, fs, e2f, f2e)
    }
  }

  test("alignment test") {
    val es = "私 は 先生 です".split("[ ]+").toList
    val fs = "I am a teacher".split("[ ]+").toList
    val e2f = Set((2,1), (4,3), (1,1), (3,2))
    val f2e = Set((2,3), (4,3), (1,1), (3,4))
    val ans = Set((3,4), (1,1), (2,3), (1,2), (4,3))

    expect (ans) {
      Alignment.alignment(es, fs, e2f, f2e)
    }
  }

  test("symmetrization test") {
    val corpus: Corpus = List(("僕 は 男 です", "I am a man"),
                              ("私 は 女 です", "I am a girl"),
                              ("私 は 先生 です", "I am a teacher"),
                              ("彼女 は 先生 です", "She is a teacher"),
                              ("彼 は 先生 です", "He is a teacher"))

    val tCorpus = mkTokenizedCorpus(corpus)
    val es = "私 は 先生 です".split("[ ]+").toList
    val fs = "I am a teacher".split("[ ]+").toList
    val syn = Alignment.symmetrization(es, fs, tCorpus, loopCount=1000)
    val ans = Set((1, 1), (1, 2), (2, 3), (3, 4), (4, 3))
    expect (ans) {
      syn
    }
  }
}
