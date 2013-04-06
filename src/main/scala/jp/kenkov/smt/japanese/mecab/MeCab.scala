package jp.kenkov.smt.japanese.mecab
import org.chasen.mecab.{MeCab, Tagger, Node}

object Keitaiso {
  System.loadLibrary("MeCab");

  type Words = List[String]

  def parseKeitaiso(surface: String, chasenData: String): Keitaiso = {
    var data = chasenData.split(",").toList
    /* for kind of number */
    data.length match {
      case 9 =>
      case x if x < 9 => data ++= (for (i <- 1 to (9 - x)) yield "*").toList
    }
    new Keitaiso(surface,
                 data(0),
                 data(1),
                 data(2),
                 data(3),
                 data(4),
                 data(5),
                 data(6),
                 data(7),
                 data(8))
  }

  def stringToKeitaisos(str: String): List[Keitaiso] = {

    val tagger = new Tagger;
    var node:Node = tagger.parseToNode(str);

    var keitaisos = List[Keitaiso]()
    while(node != null){
        keitaisos = keitaisos :+ parseKeitaiso(node.getSurface, node.getFeature)
        node = node.getNext();
    }
    keitaisos dropRight 1 drop 1
  }

  def stringToWords(str: String): Words = {
    stringToKeitaisos(str) map { _.hyousoukei }
  }
}

class Keitaiso(val hyousoukei: String,
               val hinsi: String,
               val hinsi1: String,
               val hinsi2: String,
               val hinsi3: String,
               val katuyoukei: String,
               val katuyougata: String,
               val genkei: String,
               val yomi: String,
               val hatuon: String) {
  /*
  MeCab 形式
  表層形\t品詞,品詞細分類1,品詞細分類2,品詞細分類3,活用形,活用型,原形,読み,発音

  を取り扱うクラス
  */
  override def toString: String = {
    "Keitaiso(" + List(hyousoukei, hinsi, hinsi1, hinsi2, hinsi3,
                       katuyoukei, katuyougata, genkei, yomi, hatuon).mkString(",") + ")"
  }

  override def equals(that: Any): Boolean = that match {
    case other: Keitaiso =>
      other.hyousoukei == hyousoukei &&
      other.hinsi == hinsi &&
      other.hinsi1 == hinsi1 &&
      other.hinsi2 == hinsi2 &&
      other.hinsi3 == hinsi3 &&
      other.katuyoukei == katuyoukei &&
      other.katuyougata == katuyougata &&
      other.genkei == genkei &&
      other.yomi == yomi &&
      other.hatuon == hatuon
    case _ => false
  }
}

object Test {
  def main(args: Array[String]) {
    val words = List("今日は初めてなの。",
                     "やさしくしてねっ")
    words foreach { x => println(Keitaiso.stringToKeitaisos(x)) }
    words foreach { x => println(Keitaiso.stringToWords(x)) }
  }
}
