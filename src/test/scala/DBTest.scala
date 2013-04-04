package jp.kenkov.smt.test
// scalatest
import org.scalatest.FunSuite
import scala.collection.mutable.{Map => MMap}
// scalacheck
import org.scalacheck.Properties
import org.scalacheck.Prop._
// smt library
import jp.kenkov.smt.{_}
import jp.kenkov.smt.db.{DBSMT, DBAlignment, DBIBMModel}
import jp.kenkov.smt.db.Table.{_}
// for DB
import scala.slick.driver.SQLiteDriver.simple._
import Database.threadLocalSession

/*
object DBSMTTest extends Properties("DBSMTTest") {

  property("mkTokenized Corpus test") = forAll {
    (corpus: Corpus) =>
      val dbVer: TokenizedCorpus = initCorpusDB(corpus, ":test1:")
      val nonDBVer: TokenizedCorpus = jp.kenkov.smt.mkTokenizedCorpus(corpus)
      (nonDBVer == dbVer) :| ("dbVer: " + dbVer + " " + "nonDBVer: " + nonDBVer + " " + (dbVer == nonDBVer))
  }
}
*/

class DBSMTTestSuite extends FunSuite {

  def initCorpusDB(corpus: Corpus, dbPath: DBPath): TokenizedCorpus = {
    // start sqlite session
    Database.forURL("jdbc:sqlite:%s".format(dbPath),
                    driver="org.sqlite.JDBC") withSession {
      try {
        sentenceTable().ddl.drop
      } catch {
        case ex: java.sql.SQLException => // println("Sentence table does not exist.")
      } finally {
        sentenceTable().ddl.create
        // println("Create a sentence table")
      }
      //// insert an item
      for (pair <- corpus)
        sentenceTable().ins.insert(pair)
    }
    DBSMT.mkTokenizedCorpus(dbPath, target=2, source=1)
  }


  test("mkTokenizedCorpus test for db List()") {
    val corpus: Corpus = List()
    expect(initCorpusDB(corpus, "testdb/:mkTokenizedCorpusTest1:")) {
      jp.kenkov.smt.mkTokenizedCorpus(corpus)
    }
  }

  test("mkTokenizedCorpus test for db List(\"\", \"\")") {
    val corpus: Corpus = List(("", ""))
    expect(initCorpusDB(corpus, "testdb/:mkTokenizedCorpusTest2:")) {
      jp.kenkov.smt.mkTokenizedCorpus(corpus)
    }
  }
}

class DBAlignmentTest extends FunSuite {

  def prepareCorpus(corpus: Corpus,
                    dbPath: DBPath,
                    target:Int,
                    source: Int): Unit = {
    /*
     * prepare a sentence table
     */

    Database.forURL("jdbc:sqlite:%s".format(dbPath), driver="org.sqlite.JDBC") withSession {
      // copy sentence table
      try {
        sentenceTable(target=target, source=source).ddl.drop
      } catch {
        case ex: java.sql.SQLException => println("sentence tables does not exist.")
      } finally {
        sentenceTable(target=target, source=source).ddl.create
        println("create sentence tables")
      }
      corpus foreach { case (es, fs) => sentenceTable(target=target, source=source).ins.insert(es, fs) }
    }
  }
  def train(dbPath: DBPath,
            target: Int,
            source: Int,
            loopCount: Int) {
      // train IBMModel2
      (new DBIBMModel(dbPath, target=target, source=source, loopCount=loopCount)).create()
  }

  test("symmetrization test") {
    val corpus: Corpus = List(("僕 は 男 です", "I am a man"),
                              ("私 は 女 です", "I am a girl"),
                              ("私 は 先生 です", "I am a teacher"),
                              ("彼女 は 先生 です", "She is a teacher"),
                              ("彼 は 先生 です", "He is a teacher"))
    val dbPath = "testdb/:DBAlignmentSymmetrization:"
    // initialize corpus
    prepareCorpus(corpus, dbPath, target=1, source=2)
    // training
    train(dbPath, target=1, source=2, loopCount=5)
    train(dbPath, target=2, source=1, loopCount=5)
    // sentences
    val es = "私 は 先生 です".split("[ ]+").toList
    val fs = "I am a teacher".split("[ ]+").toList
    // alignment symmetrization
    val sym = DBAlignment.symmetrization(dbPath=dbPath,
                                         target=1,
                                         source=2,
                                         es=es,
                                         fs=fs)
    val ans = Set((1, 1), (1, 2), (2, 3), (3, 4), (4, 3))
    expect(ans) { sym }
  }
}
