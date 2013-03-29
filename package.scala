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
    // for alignment
    type SourceLength = Int
    type TargetLength = Int
    type SourcePosition = Int
    type TargetPosition = Int
    type Alignment = MMap[(SourcePosition, TargetPosition, TargetLength, SourceLength), Double]

    def mkTokenizedCorpus(corpus: Corpus): TokenizedCorpus = {
      corpus.map {
        case (es, fs) => (es.split(" ").toList, fs.split(" ").toList)
      }
    }
  }
}
