package jp.kenkov.smt.phrase
import jp.kenkov.smt.{_}

object HierarchicalPhraseExtract {
  def splitN[T](n: Int, lst: List[T]): List[(List[T], List[T], List[T])]  = {
    val diff = lst.length - n
    diff match {
      case x if x < 0 => List()
      case _ => for (i <- (0 to diff).toList) yield (lst take i, lst.drop(i).take(n), lst drop (i + n))
    }
  }

  def minInd(lst: List[Int]): Int = {
    lst.filter(_ < 0) match {
      case List() => 0
      case l => l min
    }
  }
  // def extract(xRange: Range, yRange: Range, phrases: PhraseRange): Set[List[Int]] = {
  def extract(phrases: PhraseRange): Set[(List[Int], List[Int])] = {
    var st: Set[(List[Int], List[Int])] = Set()
    var initPhrase = phrases.map { case (tS, tE, sS, sE) => ((tS to tE).toList, (sS to sE).toList) }
    // initial step
    st ++= initPhrase
    // loop
    var cLoop: Set[(List[Int], List[Int])] = st
    do {
      var tmp: Set[(List[Int], List[Int])] = Set()
      for ((fst, snd) <- cLoop) {
        for ((fstIni, sndIni) <- initPhrase) {
          val fstSplit = splitN(fstIni.length, fst)
          val sndSplit = splitN(sndIni.length, snd)
          val fstAns = fstSplit find ((x: (List[Int], List[Int], List[Int])) => (x._2 == fstIni) && !(x._1.isEmpty && x._3.isEmpty))
          val sndAns = sndSplit find ((x: (List[Int], List[Int], List[Int])) => (x._2 == sndIni) && !(x._1.isEmpty && x._3.isEmpty))

          (fstAns, sndAns) match {
            case (None, None) => tmp ++= Set()
            case (Some(x), Some(y)) => tmp += ((x._1 ++ List(minInd(fst) - 1) ++ x._3,
                                                y._1 ++ List(minInd(snd) - 1) ++ y._3))
            case _ => tmp ++= Set()
          }
        }
      }
      cLoop = tmp
      st ++= tmp
    } while (cLoop != Set())
    st
  }
}
