package jp.kenkov.smt.phrase

import jp.kenkov.smt.{_}

object PhraseExtract {

  def phraseExtract(es: TargetWords,
                    fs: SourceWords,
                    alignment: Alignment): Set[(TargetWords, SourceWords)] = {
    val ext = extract(es, fs, alignment)
    val ind = ext map { case (x, y, z, w) => ((x, y), (z, w)) }
    return ind map { case ((eS, eE), (fS, fE)) => ((es slice (eS-1, eE)), fs slice (fS-1, fE)) }
  }

  def extract(es: TargetWords,
              fs: SourceWords,
              alignment: Alignment): PhraseRange = {

    var phrases: PhraseRange = Set()
    val lenEs = es.length

    for (eStart <-1 to lenEs) {
      for (eEnd <- 1 to lenEs) {
        var (fStart, fEnd) = (fs.length, 0)
        for {
          (e, f) <- alignment
          if eStart <= e
          if e <= eEnd
        } {
          fStart = f min fStart
          fEnd = f max fEnd
        }
        phrases ++= _extract(es, fs, eStart, eEnd, fStart, fEnd, alignment)
      }
    }
    phrases
  }

  private def _extract(es: TargetWords,
                       fs: SourceWords,
                       eStart: TargetStartPosition,
                       eEnd: TargetEndPosition,
                       fStart: SourceStartPosition,
                       fEnd: SourceEndPosition,
                       alignment: Alignment): PhraseRange = {
    var ex: PhraseRange = Set()
    val alignSecond = alignment.unzip._2

    // check fEnd
    if (fEnd == 0) ex
    // check loop
    else if (alignment exists { case (e, f) => ((fStart <= f) && (f <= fEnd)) && ((e < eStart) || (e > eEnd)) })
      ex
    else {
      var fS = fStart
      do {
        var fE = fEnd
        do {
          ex += ((eStart, eEnd, fS, fE))
          fE += 1
        } while (!((alignSecond contains fE) || fE > fs.length))
        fS -= 1
      } while (!((alignSecond contains fS) || fS < 1))
      ex
    }
  }
}
