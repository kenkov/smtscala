package jp.kenkov.smt.ibmmodel

import scala.collection.mutable.{Map => MMap}
import jp.kenkov.smt.{_}


abstract class IBMModel {
  // def train: MMap[(TargetWord, SourceWord), Double]

  def sourceKeys(tCorpus: TokenizedCorpus): Set[SourceWord] = {
    var fKeys: Set[SourceWord] = Set()
    tCorpus.foreach {
      case (es, fs) => fs.foreach(fKeys += _)
    }
    fKeys
  }
}

class IBMModel1(val tCorpus: TokenizedCorpus, val loopCount: Int) extends IBMModel {

  def train: MMap[(TargetWord, SourceWord), Double] = {
    // set fkeys
    val fKeys: Set[SourceWord] = sourceKeys(tCorpus)
    // set default value
    val defaultValue: Double = 1.0 / fKeys.size
    // initialize the returned collection
    val t: MMap[(String, String), Double] =
      scala.collection.mutable.Map().withDefaultValue(defaultValue)

    for (i <- 1 to loopCount) {
      // initialize vars
      val count: MMap[(String, String), Double] =
        scala.collection.mutable.Map().withDefaultValue(0.0)
      val total: MMap[String, Double] =
        scala.collection.mutable.Map().withDefaultValue(0.0)
      val sTotal: MMap[String, Double] =
        scala.collection.mutable.Map().withDefaultValue(0.0)

      // main algorithm
      for ((es, fs) <- this.tCorpus) {
        for (e <- es) {
          sTotal(e) = 0.0
          for (f <- fs)
            sTotal(e) += t((e, f))
        }
        for (e <- es) {
          for (f <- fs) {
            count((e, f)) += t((e, f)) / sTotal(e)
            total(f) += t((e, f)) / sTotal(e)
          }
        }
      }
      for ((e, f) <- count.keys) {
        val v = count((e, f)) / total(f)
        t((e, f)) = count((e, f)) / total(f)
        // println(v, t((e, f)))
      }
    }
    // return the value
    t
  }
}

class IBMModel2(val tCorpus: TokenizedCorpus, val loopCount: Int) extends IBMModel {

  def train: (MMap[(TargetWord, SourceWord), Double], AlignmentProbability) = {
    val fKeys: Set[String] = sourceKeys(tCorpus)
    // IBMModel1 training
    val t: MMap[(TargetWord, SourceWord), Double] = new IBMModel1(tCorpus, loopCount).train

    // alignment
    val a: AlignmentProbability = MMap().withDefault {
      case (i, j, lengthE, lengthF) => 1.0 / (lengthF + 1)
    }

    for (i <- 1 to loopCount) {
      val count: MMap[(TargetWord, SourceWord), Double] = MMap().withDefaultValue(0.0)
      val total: MMap[SourceWord, Double] = MMap().withDefaultValue(0.0)
      val countA: MMap[(SourcePosition, TargetPosition, TargetLength, SourceLength), Double] = MMap().withDefaultValue(0.0)
      val totalA: MMap[(TargetPosition, TargetLength, SourceLength), Double] = MMap().withDefaultValue(0.0)
      val sTotal: MMap[TargetWord, Double] = MMap().withDefaultValue(0.0)

      for ((es: TargetWords, fs: SourceWords) <- tCorpus) {
        val lengthE = es.length
        val lengthF = fs.length
        // compute normalization
        for ((e, j) <- es.zipWithIndex.map{case (k, i) => (k, i+1)}) {
          sTotal(e) = 0
          for ((f, i) <- fs.zipWithIndex.map{case (k, i) => (k, i+1)}) {
            sTotal(e) += t((e, f)) * a((i, j, lengthE, lengthF))
          }
        }
        for ((e, j) <- es.zipWithIndex.map{case (k, i) => (k, i+1)}) {
          for ((f, i) <- fs.zipWithIndex.map{case (k, i) => (k, i+1)}) {
            val c = t((e, f)) * a((i, j, lengthE, lengthF)) / sTotal(e)
            count((e, f)) += c
            total(f) += c
            countA((i, j, lengthE, lengthF)) += c
            totalA((j, lengthE, lengthF)) += c
          }
        }
      }
      for (((e, f), i) <- count.keys.zipWithIndex) {
        t((e, f)) = count((e, f)) / total(f)
        // if (i % 1000 == 0)
        //   println(e, f, t((e, f)))
      }
      for ((i, j, lengthE, lengthF) <- countA.keys) {
        a((i, j, lengthE, lengthF)) = countA((i, j, lengthE, lengthF)) / totalA((j, lengthE, lengthF))
      }
    }
    (t, a)
  }
}

object Alignment {

  def viterbiAlignment(es: TargetWords,
                       fs: SourceWords,
                       t: MMap[(TargetWord, SourceWord), Double],
                       a: AlignmentProbability) : MMap[TargetIndex, SourceIndex] = {
    val maxA: MMap[TargetIndex, SourceIndex] = MMap().withDefaultValue(0)
    val lengthE = es.length
    val lengthF = fs.length

    for ((e, j) <- es.zipWithIndex.map{case (k, i) => (k, i+1)}) {
      var currentMax: (Int, Double) = (0, -1)
      for ((f, i) <- fs.zipWithIndex.map{case (k, i) => (k, i+1)}) {
        val v = t((e, f)) * a((i, j, lengthE, lengthF))
        if (currentMax._2 < v) {
          currentMax = (i, v)
        }
      }
      maxA(j) = currentMax._1
    }
    maxA
  }

  def _alignment(eList: TargetList,
                 fList: SourceList,
                 e2f: Set[(TargetIndex,SourceIndex)],
                 f2e: Set[(TargetIndex, SourceIndex)]): Set[(Int, Int)] = {
    val neighboring = Set((-1, 0), (0, -1), (1, 0), (0, 1),
                          (-1, -1), (-1, 1), (1, -1), (1, 1))
    val m = eList.length
    val n = fList.length
    var ali: Set[(Int, Int)] = e2f intersect f2e
    var setLen = ali.size
    // marge with neighborhood
    do {
      setLen = ali.size
      for (eIndex <- 1 to m) {
        for (fIndex <- 1 to n) {
          if (ali contains (eIndex, fIndex)) {
            for ((eDiff, fDiff) <- neighboring) {
              val eNew = eIndex + eDiff
              val fNew = fIndex + fDiff
              if (!ali.isEmpty) {
                if ((e2f union f2e) contains (eNew, fNew)) {
                  ali += (eNew -> fNew)
                }
              } else {
                val eIndexes = ali.map { case (i, _) => i }
                val fIndexes = ali.map { case (_, j) => j}
                if ((!(eIndexes contains eNew) || !(fIndexes contains fNew)) &&
                    ((e2f union f2e) contains (eNew, fNew))) {
                  ali += (eNew -> fNew)
                }
              }
            }
          }
        }
      }
    } while (setLen != ali.size)

    // Finalize
    for (eIndex <- 1 to m) {
      for (fIndex <- 1 to n) {
        if (!ali.isEmpty) {
          if ((e2f union f2e) contains (eIndex, fIndex)) {
            ali += (eIndex -> fIndex)
          }
        } else {
          val eIndexes = ali.map { case (i, _) => i }
          val fIndexes = ali.map { case (_, j) => j}
          if ((!(eIndexes contains eIndex) || !(fIndexes contains fIndex)) &&
              ((e2f union f2e) contains (eIndex, fIndex))) {
            ali += (eIndex -> fIndex)
          }
        }
      }
    }
    ali
  }

  def alignment(eList: TargetList,
                fList: SourceList,
                e2f: Set[(SourceIndex,TargetIndex)],
                f2e: Set[(TargetIndex, SourceIndex)]): Set[(Int, Int)] = {
    val _e2f = for ((i, j) <- e2f) yield (j, i)
    _alignment(eList, fList, _e2f, f2e)
  }

  def symmetrization(es: TargetWords,
                     fs: SourceWords,
                     f2eTrain: (MMap[(TargetWord, SourceWord), Double], AlignmentProbability),
                     e2fTrain: (MMap[(SourceWord, TargetWord), Double], AlignmentProbability)): Alignment = {
    val (t, a) = f2eTrain
    val f2e = viterbiAlignment(es, fs, t, a)

    val (e2ft, e2fa) = e2fTrain
    val e2f = viterbiAlignment(fs, es, e2ft, e2fa)
    alignment(es, fs, e2f.toSet, f2e.toSet)
  }
}

/*
object IBMModel1Test {
  def testIBMModel1(corpus: List[(TargetSentence, SourceSentence)], loopCount: Int = 1000) {
    val tCorpus: TokenizedCorpus = mkTokenizedCorpus(corpus)
    // print the result
    val model = new IBMModel1(tCorpus, loopCount)
    println(model.tCorpus)
    println(model.loopCount)
    val ans = model.train
    println(ans)
    println()
    ans.foreach {
      case (k, v) => println("%15s -> %f".format(k, v))
    }
  }

  def testIBMModel2(corpus: List[(TargetSentence, SourceSentence)], loopCount: Int = 1000) {
    val tCorpus: TokenizedCorpus = mkTokenizedCorpus(corpus)
    // print the result
    val model = new IBMModel2(tCorpus, loopCount)
    println(model.tCorpus)
    println(model.loopCount)
    val ans = model.train
    println(ans)
    println()
    // ans.foreach {
    //   case (k, v) => println("%15s -> %f".format(k, v))
    // }
  }

  def test1() {
    val tokenizedCorpus: List[(TargetSentence, SourceSentence)] =
      List(("the house", "das Haus"),
           ("the book", "das Buch"),
           ("a book", "ein Buch"))
    testIBMModel1(tokenizedCorpus, 1000)
  }

  def test2() {
    val corpus = List(("X で は ない か と つくづく 疑問 に 思う",
                       "I often wonder if it might be X."),
                      ("X が いい な と いつも 思い ます",
                       "I always think X would be nice."),
                      ("それ が ある よう に いつも 思い ます",
                       "It always seems like it is there."))
    testIBMModel1(corpus, 10000)
  }

  def test3() {
    val sentences: List[(TargetSentence, SourceSentence)] =
      List(("the house", "das Haus"),
           ("the book", "das Buch"),
           ("a book", "ein Buch"))
    testIBMModel2(sentences, 1000)
  }

  def test4() {
    val corpus: List[(TargetSentence, SourceSentence)] =
      List(("the house", "das Haus"),
           ("the book", "das Buch"),
           ("a book", "ein Buch"))
    val tCorpus = mkTokenizedCorpus(corpus)
    val (t, a) = new IBMModel2(tCorpus, 1000).train
    println(t)
    println(a)
    val es: TargetWords = List("the", "house")
    val fs: SourceWords = List("das", "Haus")
    val ans = Alignment.viterbiAlignment(es, fs, t, a)
    println(ans)
  }

  def main(args: Array[String]) {
    test4()
  }
}
*/
