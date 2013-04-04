package jp.kenkov.smt.test
// scalatest
import org.scalatest.FunSuite
import scala.collection.mutable.{Map => MMap}
// scalacheck
import org.scalacheck.Properties
import org.scalacheck.Prop._
// smt library
import jp.kenkov.smt.{_}

/*
object DBSMTTest extends Properties("DBSMTTest") {

  property("mkTokenizedCorpus test") = forAll {
    (corpus: Corpus) =>
      val tCorpus: TokenizedCorpus = mkTokenizedCorpus(corpus)
      val manualVer = corpus.map {
        case (es, fs) => (es.split("[ ]+"), fs.split("[ ]+"))
      }
      (tCorpus == manualVer) :| ("tokenizedCorpus: " + tCorpus + " " + "manualVer: " + manualVer)
  }
}
*/
