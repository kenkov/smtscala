package jp.kenkov.smt.japanese
import org.chasen.mecab.{MeCab, Tagger, Node}

object Keitaiso {

  type Words = List[String]

  def parseKeitaiso(surface: String, chasenData: String): Keitaiso = {
    val data = chasenData.split(",").toList
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
    System.loadLibrary("MeCab");

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

}



object Test {
  def main(args: Array[String]) {
    val words = List("今日は初めてなの。",
                     "やさしくしてねっ")
    words foreach { x => println(Keitaiso.stringToKeitaisos(x)) }
    words foreach { x => println(Keitaiso.stringToWords(x)) }
  }
}
