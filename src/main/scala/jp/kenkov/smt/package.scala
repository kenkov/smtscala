import scala.collection.mutable.{Map => MMap}

package jp.kenkov {
  package object smt {
    type SourceSentence = String
    type TargetSentence = String
    type SourceWord = String
    type TargetWord = String
    type SourceWords = List[SourceWord]
    type TargetWords = List[TargetWord]
    type Corpus = List[(TargetSentence, SourceSentence)]
    type TokenizedCorpus = List[(List[TargetWord], List[SourceWord])]

    // for viterbi alignment
    type SourceLength = Int
    type TargetLength = Int
    type SourceWordIndex = Int
    type TargetWordIndex = Int
    type AlignmentProbability = MMap[(SourceWordIndex, TargetWordIndex, TargetLength, SourceLength), Double]

    // for alignment
    type SourceList = List[Any]
    type TargetList = List[Any]
    type SourceIndex = Int
    type TargetIndex = Int

    type SourcePosition = Int
    type TargetPosition =Int
    type Alignment = Set[(SourcePosition, TargetPosition)]

    // for phrase extract
    type SourceStartPosition = Int
    type SourceEndPosition = Int
    type TargetStartPosition = Int
    type TargetEndPosition = Int


    type PhraseRange = Set[(TargetStartPosition,
                            TargetEndPosition,
                            SourceStartPosition,
                            SourceEndPosition)]

    def mkTokenizedCorpus(corpus: Corpus): TokenizedCorpus = {
      corpus.map {
        case (es, fs) => (es.split("[ ]+").toList, fs.split("[ ]+").toList)
      }
    }
  }
}
