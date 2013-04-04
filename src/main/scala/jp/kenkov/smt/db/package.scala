package jp.kenkov.smt.db
import jp.kenkov.smt.{_}
import jp.kenkov.smt.db.Table.{_}
// Use H2Driver to connect to an H2 database
import scala.slick.driver.SQLiteDriver.simple._
// Use the implicit threadLocalSession
import Database.threadLocalSession

object DBSMT {
  def mkTokenizedCorpus(
    dbPath: DBPath,
    target: Int,
    source: Int,
    targetMethod: TargetSentence => TargetWords = x => x.split("[ ]+").toList,
    sourceMethod: SourceSentence => SourceWords = x => x.split("[ ]+").toList): TokenizedCorpus = {
    // start sqlite session
    Database.forURL("jdbc:sqlite:%s".format(dbPath),
                    driver="org.sqlite.JDBC") withSession {
      val q = for (s <- sentenceTable(target=target, source=source))
        yield (s.targetSentence,
               s.sourceSentence)
      val corpus: Corpus = q.list
      jp.kenkov.smt.mkTokenizedCorpus(corpus,
                                      targetMethod=targetMethod,
                                      sourceMethod=sourceMethod)
    }
  }
}
