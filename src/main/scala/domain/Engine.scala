package domain

import scala.math.log

object Engine {
  private val sentenceSplitRegex = "(?<=[.!?])\\s+"
  private val tokenSplitRegex = "[^\\p{L}\\p{N}]+"
  private val blockSplitRegex = "\\R+"

  def segmentText(text: String): List[RawSentence] = {
    text
      .split(blockSplitRegex)
      .flatMap(_.split(sentenceSplitRegex))
      .map(_.trim)
      .filter(_.nonEmpty)
      .toList
      .map(RawSentence.apply)
  }

  def tokenizeSentence(sentence: RawSentence, stopwords: Set[String]): TokenizedSentence = {
    val tokens = sentence.text
      .toLowerCase
      .split(tokenSplitRegex)
      .toList
      .filter(_.nonEmpty)
      .filterNot(stopwords.contains)
      .map(Token.apply)

    TokenizedSentence(sentence, tokens)
  }

  def computeIdf(sentences: List[TokenizedSentence]): IdfModel = {
    val total = sentences.size.toDouble
    val df = sentences.foldLeft(Map.empty[Token, Int]) { (acc, sentence) =>
      sentence.tokens.distinct.foldLeft(acc) { (inner, token) =>
        inner.updated(token, inner.getOrElse(token, 0) + 1)
      }
    }

    df.iterator.map { case (token, count) =>
      token -> log(total / (1.0 + count.toDouble))
    }.toMap
  }

  def scoreSentence(sentence: TokenizedSentence, idf: IdfModel): ScoredSentence = {
    val totalTokens = sentence.tokens.length.toDouble
    val counts = sentence.tokens.foldLeft(Map.empty[Token, Int]) { (acc, token) =>
      acc.updated(token, acc.getOrElse(token, 0) + 1)
    }

    val score = counts.keys.foldLeft(0.0) { (sum, token) =>
      val tf = counts(token).toDouble / totalTokens
      val idfValue = idf.getOrElse(token, 0.0)
      sum + (tf * idfValue)
    }

    ScoredSentence(sentence, score)
  }
}
