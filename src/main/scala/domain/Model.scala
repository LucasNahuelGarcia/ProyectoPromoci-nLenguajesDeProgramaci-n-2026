package domain

opaque type Token = String

object Token {
  def apply(value: String): Token = value

  extension (token: Token)
    def value: String = token
}

final case class RawSentence(text: String)
final case class TokenizedSentence(original: RawSentence, tokens: List[Token])
final case class ScoredSentence(sentence: TokenizedSentence, score: Double)

type IdfModel = Map[Token, Double]
type TfModel = Map[Token, Double]
