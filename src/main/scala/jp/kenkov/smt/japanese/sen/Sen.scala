package jp.kenkov.smt.japanese.sen
import net.java.sen.{SenFactory}
import java.util.Locale
import scala.collection.JavaConverters._


class Keitaiso(val hyousoukei: String,
               val genkei: String,
               val pos: String,
               val conjugationalForm: String,
               val conjugationalType: String,
               val pronunciations: List[String],
               val readings: List[String],
               val length: Int,
               val start: Int,
               val cost: Double) {

  override def toString: String = {
    "Keitaiso(" + List(hyousoukei, genkei, pos, conjugationalForm, conjugationalType,
                       pronunciations, readings, length, start, cost).mkString(", ") + ")"
  }

  override def equals(that: Any): Boolean = that match {
    case other: Keitaiso =>
      other.hyousoukei == hyousoukei &&
      other.genkei == genkei &&
      other.pos == pos &&
      other.pronunciations == pronunciations &&
      other.readings == readings &&
      other.length == length
    case _ => false
  }
}

object Keitaiso {
  type Words = List[String]

  def stringToKeitaisos(input: String): List[Keitaiso] = {
    val tagger = SenFactory.getStringTagger(null)
    val token = tagger.analyze(input).asScala.toList
    // for (t <- token) yield
    token map { t =>
      val mor = t.getMorpheme()
      new Keitaiso(
        t.getSurface(),
        mor.getBasicForm(),
        mor.getPartOfSpeech(),
        mor.getConjugationalForm(),
        mor.getConjugationalType(),
        mor.getPronunciations().asScala.toList,
        mor.getReadings().asScala.toList,
        t.getLength(),
        t.getStart(),
        t.getCost())
    }
  }

  def stringToWords(input: String): Words = {
    val tagger = SenFactory.getStringTagger(null)
    val token = tagger.analyze(input).asScala.toList
    for (t <- token) yield t.toString
  }
}

object SenTest {
  def main(args: Array[String]) {
    val words = List("今日は初めてなの。",
                     "やさしくしてねっ")
    words foreach { x => println(Keitaiso.stringToKeitaisos(x)) }
    words foreach { x => println(Keitaiso.stringToWords(x)) }
  }
}
