package jp.kenkov.smt.test
// scalatest
import org.scalatest.FunSuite
import scala.collection.mutable.{Map => MMap}
// scalacheck
import org.scalacheck.Properties
import org.scalacheck.Prop._
// smt library
import jp.kenkov.smt.{_}
import jp.kenkov.smt.db.{DBSMT}
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
