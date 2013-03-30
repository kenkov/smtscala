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
    type SourcePosition = Int
    type TargetPosition = Int
    type AlignmentProbability = MMap[(SourcePosition, TargetPosition, TargetLength, SourceLength), Double]
    // for alignment
    type SourceList = List[Any]
    type TargetList = List[Any]
    type SourceIndex = Int
    type TargetIndex = Int
    type Position = Int
    type Alignment = Set[(Position, Position)]

    def mkTokenizedCorpus(corpus: Corpus): TokenizedCorpus = {
      corpus.map {
        case (es, fs) => (es.split("[ ]+").toList, fs.split("[ ]+").toList)
      }
    }
  }
}
