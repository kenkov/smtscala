object Types {
  type Corpus = List[(List[String], List[String])]
}

class IBMModel1(corpus: Types.Corpus, loopCount: Int) {

  private def fsKeys(): Set[String] = {
    var fKeys: Set[String] = Set()
    corpus.foreach {
      case (es, fs) => fs.foreach(f => fKeys += f)
    }
    fKeys
  }

  //def train(corpus: Corpus, loopCount: Int): Map[(String, String), Double] = {
  def train(): scala.collection.mutable.Map[(String, String), Double] = {
    val fKeys = fsKeys()
    val defaultValue = 1.0 / fKeys.size

    val t: scala.collection.mutable.Map[(String, String), Double] =
      scala.collection.mutable.Map().withDefaultValue(defaultValue)

    for (i <- 1 to loopCount) {
      val count: scala.collection.mutable.Map[(String, String), Double] =
        scala.collection.mutable.Map().withDefaultValue(0.0)
      val total: scala.collection.mutable.Map[String, Double] =
        scala.collection.mutable.Map().withDefaultValue(0.0)
      val sTotal: scala.collection.mutable.Map[String, Double] =
        scala.collection.mutable.Map().withDefaultValue(0.0)

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
    t
  }
}

object IBMModelTest {
  def main(args: Array[String]) {
    val sentences: List[(String, String)] =
      List(("the house", "das Haus"),
           ("the book", "das Buch"),
           ("a book", "ein Buch"))

    val corpus: Types.Corpus = sentences.map {
      case (es, fs) => (es.split(" ").toList, fs.split(" ").toList)
    }

    println((new IBMModel1(corpus, 10000)).train())
  }
}
