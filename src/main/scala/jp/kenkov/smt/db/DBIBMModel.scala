package jp.kenkov.smt.db
// Use H2Driver to connect to an H2 database
import scala.slick.driver.SQLiteDriver.simple._
// Use the implicit threadLocalSession
import Database.threadLocalSession
//
import scala.collection.mutable.{Map => MMap}
import jp.kenkov.smt.{_}
import jp.kenkov.smt.db.{_}
import jp.kenkov.smt.ibmmodel.{_}


class DBIBMModel(val dbPath: DBPath,
                 val targetMethod: TargetSentence => TargetWords = x => x.split("[ ]+").toList,
                 val sourceMethod: SourceSentence => SourceWords = x => x.split("[ ]+").toList,
                 val loopCount: Int = 1000) {
  /*
   * this class require an appropreate sentence table in a database
   */

  def create(): Unit = {
    val tCorpus = DBSMT.mkTokenizedCorpus(dbPath,
                                          targetMethod=targetMethod,
                                          sourceMethod=sourceMethod)
    val ibmModel2 = new IBMModel2(tCorpus, loopCount)
    val (t: MMap[(TargetWord, SourceWord), Double], a: AlignmentProbability) = ibmModel2.train
    // start sqlite session
    Database.forURL("jdbc:sqlite:%s".format(dbPath),
                    driver="org.sqlite.JDBC") withSession {
      //// create WordAlignment and WordProb tables
      try {
        (WordAlignment.ddl ++ WordProb.ddl).drop
      } catch {
        case ex: java.sql.SQLException => println("WordProb and WordAlignment tables does not exist.")
      } finally {
        (WordProb.ddl ++ WordAlignment.ddl).create
        println("create WordProb and WordAlignment tables")
      }

      for (((tWord, sWord), prob) <- t) {
        WordProb.ins.insert(tWord, sWord, prob)
        // println(tWord, sWord, prob)
      }
      for (((sWIndex, tWIndex, tLen, sLen), prob) <- a) {
        WordAlignment.ins.insert(sWIndex, tWIndex, tLen, sLen, prob)
        // println(sWIndex, tWIndex, tLen, sLen, prob)
      }
    }
  }
}

/*
object DBAlignment {
  def dbViterbiAlignment(dbPath: DBPath,
                         es: TargetWords,
                         fs: SourceWords,
                         initialValue: Double): MMap[Int, Int] = {
    val maxA: MMap[Int, Int] = MMap().withDefaultValue(0)
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
}
*/


object Main {

  def main(args: Array[String]) {
    val originalDBPath = "testdb/:jec_basic:"
    val dbPath = "testdb/:DBIBMModelMain:"
    var corpus = List[(TargetSentence, SourceSentence)]()

    Database.forURL("jdbc:sqlite:%s".format(originalDBPath), driver="org.sqlite.JDBC") withSession {
      val q = Query(Sentence)
      // set corpus
      corpus = q.list.map {
        case (id, tS, sS) => (tS, sS)
      }
    }
    Database.forURL("jdbc:sqlite:%s".format(dbPath), driver="org.sqlite.JDBC") withSession {
      //// create a table
      /*
      try {
        Sentence.ddl.drop
      } catch {
        case ex: java.sql.SQLException => println("sentence table does not exist.")
      } finally {
        Sentence.ddl.create
        println("create sentence table")
      }
      */
      // insert sentence pairs to sentence table
      corpus foreach { case (es, fs) => Sentence.ins.insert(es, fs) }

      (new DBIBMModel(dbPath, loopCount=100)).create()
    }
  }
}
