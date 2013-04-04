package jp.kenkov.smt.db
// Use H2Driver to connect to an H2 database
import scala.slick.driver.SQLiteDriver.simple._
// Use the implicit threadLocalSession
import Database.threadLocalSession
//
import scala.collection.mutable.{Map => MMap}
import jp.kenkov.smt.{_}
import jp.kenkov.smt.db.{Sentence, WordProb, WordAlignment}




class DBIBMModel(val targetMethod: TargetSentence => TargetWords,
                 val sourceMethod: SourceSentence => SourceWords,
                 val dbPath: DBPath,
                 val loopCount: Int = 1000) {

  def create(): Unit = {
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
    }
  }
}


/*
object Main {

  def main(args: Array[String]) {
    Database.forURL("jdbc:sqlite::memory:", driver="org.sqlite.JDBC") withSession {
      //// create a table
      try {
        Person.ddl.drop
      } catch {
        case ex: java.sql.SQLException => println("person table does not exist.")
      } finally {
        Person.ddl.create
        println("create person table")
      }
      //// insert an item
      Person.ins.insertAll(("kenkov", 17),
                           ("hoge", 18),
                           ("fuga", 19))
      //// print
      // Query(Person) foreach {
      //   case (id, name, age) => println(id, name, age + 12)
      // }

      val allQuery = for { p <- Person } yield p
      println(Query(Query(Person).length).first)
      println(allQuery.list)
      println(Query(Person).filter(_.age === 20).firstOption)
    }
  }
}
*/
