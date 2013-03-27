package jp.kenkov {
  package object smt {
    type Source = String
    type Target = String
    type Corpus = List[(Target, Source)]
    type TokenizedCorpus = List[(List[Target], List[Source])]

    def mkSentence(corpus: Corpus): TokenizedCorpus = {
      corpus.map {
        case (es, fs) => (es.split(" ").toList, fs.split(" ").toList)
      }
    }
  }
}
