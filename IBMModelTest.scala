package jp.kenkov.smt.test
import org.scalatest.FunSuite
import scala.collection.mutable.{Map => MMap}
import jp.kenkov.smt.{_}

import jp.kenkov.smt.ibmmodel.{IBMModel1, IBMModel2, ViterbiAlignment}

class IBMModel1Test extends FunSuite {

  test("ibmmodel1 test for loop 0") {
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


class ViterbiAlignmentTest extends FunSuite {

  test("viterbi alignment test") {
    val corpus: List[(TargetSentence, SourceSentence)] =
      List(("the house", "das Haus"),
           ("the book", "das Buch"),
           ("a book", "ein Buch"))
    val tCorpus = mkTokenizedCorpus(corpus)
    val (t, a) = new IBMModel2(tCorpus, 10).train
    val es: TargetWords = List("the", "house")
    val fs: SourceWords = List("das", "Haus")
    val ans = new ViterbiAlignment(es, fs, t, a).calculate
    expect(ans) {
      MMap(0 -> 0, 1 -> 1)
    }
  }
}
