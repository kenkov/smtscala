==============================
README
==============================

What is this?
===============

This package implements basic Statistical Machine Translation (SMT) functions by Scala.

API
=====

Type synonims are defined in **jp.kenkov.smt** as

::

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

    type StartPosition = Int
    type EndPosition = Int
    type Range = (StartPosition, EndPosition)

    type PhraseRange = Set[(TargetStartPosition,
                            TargetEndPosition,
                            SourceStartPosition,
                            SourceEndPosition)]

IBM Model APIs are defined in **jp.kenkov.smt.ibmmodel** as

::

    class IBMModel1(val tCorpus: TokenizedCorpus, val loopCount: Int) extends IBMModel

        def train: MMap[(TargetWord, SourceWord), Double] = {


    class IBMModel2(val tCorpus: TokenizedCorpus, val loopCount: Int) extends IBMModel

      def train: (MMap[(TargetWord, SourceWord), Double], AlignmentProbability)

    object Alignment

        def alignment(eList: TargetList,
                      fList: SourceList,
                      e2f: Set[(SourceIndex,TargetIndex)],
                      f2e: Set[(TargetIndex, SourceIndex)]): Set[(Int, Int)]

        def viterbiAlignment(es: TargetWords,
                             fs: SourceWords,
                             t: MMap[(TargetWord, SourceWord), Double],
                             a: AlignmentProbability) : MMap[SourceIndex, TargetIndex]

        def symmetrization(es: TargetWords, fs: SourceWords, corpus: TokenizedCorpus, loopCount: Int = 1000): Alignment


Phrae extract functions are defined in **package jp.kenkov.smt.phrase** as

::

    object PhraseExtract

      def phraseExtract(es: TargetWords,
                        fs: SourceWords,
                        alignment: Alignment): Set[(TargetWords, SourceWords)] = {


