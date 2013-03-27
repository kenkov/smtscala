// object Types {
//   type Source = String
//   type Target = String
//   type TokenizedCorpus = List[(List[Target], List[Source])]
// }

package jp.kenkov.smt.ibmmodel
import scala.collection.mutable.{Map => MMap}
import ibm.{_}

class IBMModel1(corpus: TokenizedCorpus, loopCount: Int) {

  private def targetKeys(): Set[Source] = {
    var fKeys: Set[Source] = Set()
    corpus.foreach {
      case (es, fs) => fs.foreach(fKeys += _)
    }
    fKeys
  }

  def train(): MMap[(Target, Source), Double] = {
    // set fkeys
    val fKeys: Set[String] = targetKeys()
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
      for ((es, fs) <- corpus) {
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
      for ((e, f) <- count.keys)
        t((e, f)) = count((e, f)) / total(f)
    }
    // return the value
    t
  }
}

object IBMModel1Test {
  def test(sentences: List[(Target, Source)], loopCount: Int = 1000) {
    val corpus: TokenizedCorpus = sentences.map {
      case (es, fs) => (es.split(" ").toList, fs.split(" ").toList)
    }
    // print the result
    val ans = (new IBMModel1(corpus, loopCount)).train()
    println(ans)
    println()
    ans.foreach {
      case (k, v) => println("%15s -> %f".format(k, v))
    }
  }

  def test1() {
    val sentences: List[(Target, Source)] =
      List(("the house", "das Haus"),
           ("the book", "das Buch"),
           ("a book", "ein Buch"))
    test(sentences, 1000)
  }

  def test2() {
    val sentences = List(("X で は ない か と つくづく 疑問 に 思う",
                          "I often wonder if it might be X."),
                         ("X が いい な と いつも 思い ます",
                          "I always think X would be nice."),
                         ("それ が ある よう に いつも 思い ます",
                          "It always seems like it is there."))
    test(sentences, 10000)
  }

  def main(args: Array[String]) {
    test2()
  }
}
