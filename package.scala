package jp.kenkov {
  package object smt {
    type SourceSentence = String
    type TargetSentence = String
    type SourceWord = String
    type TargetWord = String
    type Corpus = List[(TargetSentence, SourceSentence)]
    type TokenizedCorpus = List[(List[TargetWord], List[SourceWord])]
    // for alignment
    type SourceLength = Int
    type TargetLength = Int
    type SourcePosition = Int
    type TargetPosition = Int

    def mkSentence(corpus: Corpus): TokenizedCorpus = {
      corpus.map {
        case (es, fs) => (es.split(" ").toList, fs.split(" ").toList)
      }
    }
  }
}
