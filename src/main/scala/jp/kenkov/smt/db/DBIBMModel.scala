package jp.kenkov.smt.db
// Use H2Driver to connect to an H2 database
import scala.slick.driver.SQLiteDriver.simple._
// Use the implicit threadLocalSession
import Database.threadLocalSession
//
import scala.collection.mutable.{Map => MMap}
import jp.kenkov.smt.{_}
import jp.kenkov.smt.db.{_}
import jp.kenkov.smt.db.Table.{_}
import jp.kenkov.smt.ibmmodel.{_}


class DBIBMModel(val dbPath: DBPath,
                 val target: Int = 2,
                 val source: Int = 1,
                 val targetMethod: TargetSentence => TargetWords = x => x.split("[ ]+").toList,
                 val sourceMethod: SourceSentence => SourceWords = x => x.split("[ ]+").toList,
                 val loopCount: Int = 5) {
  /*
   * this class require an appropreate sentence table in a database
   */

  def create(): Unit = {
    val tCorpus = DBSMT.mkTokenizedCorpus(dbPath,
                                          target=target,
                                          source=source,
                                          targetMethod=targetMethod,
                                          sourceMethod=sourceMethod)
    val ibmModel2 = new IBMModel2(tCorpus, loopCount)
    val (t: MMap[(TargetWord, SourceWord), Double], a: AlignmentProbability) = ibmModel2.train
    // start sqlite session
    Database.forURL("jdbc:sqlite:%s".format(dbPath),
                    driver="org.sqlite.JDBC") withSession {
      //// create WordAlignment and WordProb tables
      try {
        (wordAlignmentTable(target=target, source=source).ddl ++ wordProbTable(target=target, source=source).ddl).drop
      } catch {
        case ex: java.sql.SQLException => println("WordProb and WordAlignment tables does not exist.")
      } finally {
        (wordProbTable(target=target, source=source).ddl ++ wordAlignmentTable(target=target, source=source).ddl).create
        println("create WordProb and WordAlignment tables")
      }

      for (((tWord, sWord), prob) <- t) {
        wordProbTable(target=target, source=source).ins.insert(tWord, sWord, prob)
        // println(tWord, sWord, prob)
      }
      for (((sWIndex, tWIndex, tLen, sLen), prob) <- a) {
        wordAlignmentTable(target=target, source=source).ins.insert(sWIndex, tWIndex, tLen, sLen, prob)
        // println(sWIndex, tWIndex, tLen, sLen, prob)
      }
    }
  }
}

object DBAlignment {
  def getT(dbPath: DBPath,
           target: Int,
           source: Int,
           e: TargetWord,
           f: SourceWord,
           defaultValue: Probability = 1e-10): Probability = {
    Database.forURL("jdbc:sqlite:%s".format(dbPath),
                    driver="org.sqlite.JDBC") withSession {
      // val q = Query(WordProb).filter(_.targetWord === e).filter(_.sourceWord === f)
      val q =  for { wp <- wordProbTable(target=target, source=source) if wp.targetWord === e && wp.sourceWord === f} yield wp
      q.firstOption match {
        case Some((id, tWord, sWord, prob)) => prob
        case None => defaultValue
      }
    }
  }

  def getA(dbPath: DBPath,
           target: Int,
           source: Int,
           sourcePosition :SourcePosition,
           targetPosition :TargetPosition,
           targetLength :TargetLength,
           sourceLength :SourceLength,
           defaultValue: Probability = 1e-10): Probability = {
    Database.forURL("jdbc:sqlite:%s".format(dbPath),
                    driver="org.sqlite.JDBC") withSession {
      val q =  for { wa <- wordAlignmentTable(target=target, source=source) if
        wa.sourcePosition === sourcePosition &&
        wa.targetPosition === targetPosition &&
        wa.targetLength === targetLength &&
        wa.sourceLength === sourceLength } yield wa

      q.firstOption match {
        case Some((id, sP, tP, tL, sL, prob)) => prob
        case None => defaultValue
      }
    }
  }

  def dbViterbiAlignment(dbPath: DBPath,
                         target: Int,
                         source: Int,
                         es: TargetWords,
                         fs: SourceWords,
                         initialValue: Double = 1e-10): MMap[Int, Int] = {
    val maxA: MMap[Int, Int] = MMap().withDefaultValue(0)
    val lengthE = es.length
    val lengthF = fs.length

    for ((e, j) <- es.zipWithIndex.map{case (k, i) => (k, i+1)}) {
      var currentMax: (Int, Double) = (0, -1)
      for ((f, i) <- fs.zipWithIndex.map{case (k, i) => (k, i+1)}) {
        val v = getT(dbPath, target, source, e, f) * getA(dbPath, target, source, i, j, lengthE, lengthF)
        if (currentMax._2 < v) {
          currentMax = (i, v)
        }
      }
      maxA(j) = currentMax._1
    }
    maxA
  }

  def symmetrization(dbPath: DBPath,
                     target: Int,
                     source: Int,
                     es: TargetWords,
                     fs: SourceWords): Alignment = {
    val f2e = dbViterbiAlignment(dbPath, target, source, es, fs)
    val e2f = dbViterbiAlignment(dbPath, source, target, fs, es)
    jp.kenkov.smt.ibmmodel.Alignment.alignment(es, fs, e2f.toSet, f2e.toSet)
  }
}


object Main {

  def train(originalDBPath: DBPath,
            toDBPath: DBPath,
            target:Int,
            source: Int): Unit = {
    var corpus = List[(TargetSentence, SourceSentence)]()

    Database.forURL("jdbc:sqlite:%s".format(originalDBPath), driver="org.sqlite.JDBC") withSession {
      val q = Query(sentenceTable(target=target, source=source))
      // set corpus
      corpus = q.list.map {
        case (id, tS, sS) => (tS, sS)
      }
    }
    Database.forURL("jdbc:sqlite:%s".format(toDBPath), driver="org.sqlite.JDBC") withSession {
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

      // train IBMModel2
      (new DBIBMModel(toDBPath, target=target, source=source, loopCount=5)).create()
    }
  }
  /*
  def testCreateDB(target: Int, source: Int) {
    val originalDBPath = "testdb/:jec_basic:"
    val dbPath = "testdb/:DBIBMModelMain:"
    train(originalDBPath, dbPath, target=target, source=source)
  }
  */

  def dbSymmetrizationTest(): Alignment = {
    val originDB = "testdb/jec/:jec:"
    val db = "testdb/jec/:train_jec:"
    train(originDB, db, target=2, source=1)
    train(originDB, db, target=1, source=2)
    val es = "I am a teacher".split("[ ]+").toList
    val fs = "私 は 先生 です".split("[ ]+").toList
    val sym = DBAlignment.symmetrization(dbPath=db,
                                         target=1,
                                         source=2,
                                         es=es,
                                         fs=fs)
    sym
  }
  def main(args: Array[String]) {
    println(dbSymmetrizationTest)
  }
}


object DBTest {

  object People extends Table[(Int, String, Int)]("person") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def age = column[Int]("age")
    def * = id ~ name ~ age
    def ins = name ~ age returning id
  }

  def main(args: Array[String]) {
    Database.forURL("jdbc:sqlite:%s".format(":memory:"), driver="org.sqlite.JDBC") withSession {
      // create table
      People.ddl.create
      // insert item
      People.ins.insertAll(
        ("kenkov", 21),
        ("kenkov2", 22)
      )
      // query
      val q = Query(People).filter(_.age === 21)
      // set corpus
      println(q.firstOption)
    }
  }
}
