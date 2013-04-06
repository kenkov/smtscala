package jp.kenkov.smt.japanese.sen
import net.java.sen.{StringTagger, Token}
import java.util.Locale


class Keitaiso(val hyousoukei: String,
               val genkei: String,
               val pos: String,
               val pronunciation: String,
               val reading: String,
               val length: Int,
               val start: Int,
               val end: Int) {

  override def toString: String = {
    "Keitaiso(" + List(hyousoukei, genkei, pos, pronunciation,
                       reading, length, start, end).mkString(", ") + ")"
  }

  override def equals(that: Any): Boolean = that match {
    case other: Keitaiso =>
      other.hyousoukei == hyousoukei &&
      other.genkei == genkei &&
      other.pos == pos &&
      other.pronunciation == pronunciation &&
      other.reading == reading &&
      other.length == length
    case _ => false
  }
}

object Keitaiso {
  type Words = List[String]

  def stringToKeitaisos(input: String): List[Keitaiso] = {
    val tagger = StringTagger.getInstance(Locale.JAPANESE)
    val token = tagger.analyze(input).toList
    for (t <- token) yield
      new Keitaiso(
        t.toString(),
        t.getBasicString(),
        t.getPos(),
        t.getPronunciation(),
        t.getReading(),
        t.length(),
        t.start(),
        t.end())
  }

  def stringToWords(input: String): Words = {
    val tagger = StringTagger.getInstance(Locale.JAPANESE)
    val token = tagger.analyze(input).toList
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
