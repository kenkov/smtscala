package jp.kenkov.smt.db
// Use H2Driver to connect to an H2 database
import scala.slick.driver.SQLiteDriver.simple._
// Use the implicit threadLocalSession
import Database.threadLocalSession
//
import scala.collection.mutable.{Map => MMap}
import jp.kenkov.smt.{_}


object Table {

  def sentenceTable(target: Int = 1, source: Int = 2) = {
    (target, source) match {
      case (1, 2) =>
        object Sentence extends Table[(Int, TargetSentence, SourceSentence)]("sentence") {
          def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
          def targetSentence = column[TargetSentence]("sentence1")
          def sourceSentence = column[SourceSentence]("sentence2")
          def * = id ~ targetSentence ~ sourceSentence
          def ins = targetSentence ~ sourceSentence returning id
        }
        Sentence
      case (2, 1) =>
        object Sentence extends Table[(Int, TargetSentence, SourceSentence)]("sentence") {
          def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
          def targetSentence = column[TargetSentence]("sentence2")
          def sourceSentence = column[SourceSentence]("sentence1")
          def * = id ~ targetSentence ~ sourceSentence
          def ins = targetSentence ~ sourceSentence returning id
        }
        Sentence
    }
  }

  def wordProbTable(target: Int = 1, source: Int = 2) = {
    val tableName = "from%sto%swordprob".format(source, target)

    object WordProb extends Table[(Int, TargetWord, SourceWord, Probability)](tableName) {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def targetWord = column[TargetWord]("target_word")
      def sourceWord = column[SourceWord]("source_word")
      def prob = column[Probability]("prob")
      def * = id ~ targetWord ~ sourceWord ~ prob
      def ins = targetWord ~ sourceWord ~ prob returning id
    }

    WordProb
  }

  def wordAlignmentTable(target: Int = 1, source: Int = 2) = {
    val tableName = "from%sto%swordalignment".format(source, target)

    object WordAlignment extends Table[(Int, SourcePosition, TargetPosition, TargetLength, SourceLength, Probability)](tableName) {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def sourcePosition = column[SourcePosition]("source_position")
      def targetPosition = column[TargetPosition]("target_position")
      def targetLength = column[TargetLength]("target_length")
      def sourceLength = column[SourceLength]("source_length")
      def prob = column[Probability]("prob")
      def * = id ~ sourcePosition ~ targetPosition ~ targetLength ~ sourceLength ~ prob
      def ins = sourcePosition ~ targetPosition ~ targetLength ~ sourceLength ~ prob returning id
    }
    WordAlignment
  }
}
