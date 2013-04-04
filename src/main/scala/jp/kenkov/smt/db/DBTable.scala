package jp.kenkov.smt.db
// Use H2Driver to connect to an H2 database
import scala.slick.driver.SQLiteDriver.simple._
// Use the implicit threadLocalSession
import Database.threadLocalSession
//
import scala.collection.mutable.{Map => MMap}
import jp.kenkov.smt.{_}


object Sentence extends Table[(Int, TargetSentence, SourceSentence)]("sentence") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def targetSentence = column[TargetSentence]("target_sentence")
  def sourceSentence = column[SourceSentence]("source_sentence")
  def * = id ~ targetSentence ~ sourceSentence
  def ins = targetSentence ~ sourceSentence returning id
}

object WordProb extends Table[(Int, TargetWord, SourceWord, Probability)]("wordprob") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def targetWord = column[TargetWord]("target_word")
  def sourceWord = column[SourceWord]("source_word")
  def prob = column[Probability]("prob")
  def * = id ~ targetWord ~ sourceWord ~ prob
  def ins = targetWord ~ sourceWord ~ prob returning id
}

object WordAlignment extends Table[(Int, SourcePosition, TargetPosition, TargetLength, SourceLength, Probability)]("wordalign") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def sourcePosition = column[SourcePosition]("source_position")
  def targetPosition = column[TargetPosition]("target_position")
  def targetLength = column[TargetLength]("target_length")
  def sourceLength = column[SourceLength]("source_length")
  def prob = column[Probability]("prob")
  def * = id ~ sourcePosition ~ targetPosition ~ targetLength ~ sourceLength ~ prob
  def ins = sourcePosition ~ targetPosition ~ targetLength ~ sourceLength ~ prob returning id
}

