import domain.Engine
import infrastructure.IOHandler

@main def main(args: String*): Unit = {
  if (args.length != 1) {
    println("Uso: sbt run <directorio>")
    sys.exit(1)
  }

  val directory = args.head

  val result = for {
    stopwords <- IOHandler.loadStopwords("stopwords-es.txt")
    textsLazy <- IOHandler.loadTexts(directory)
  } yield (stopwords, textsLazy)

  result match {
    case Left(error) =>
      println(error)
      sys.exit(1)

    case Right((stopwords, textsLazy)) =>
      val texts = textsLazy.toList
      val rawSentences = texts.flatMap(Engine.segmentText)
      val tokenized = rawSentences
        .map(sentence => Engine.tokenizeSentence(sentence, stopwords))
        .filter(_.tokens.nonEmpty)

      if (tokenized.isEmpty) {
        println("No hay contenido para resumir.")
        sys.exit(1)
      }

      val idf = Engine.computeIdf(tokenized)
      val scored = tokenized.map(sentence => Engine.scoreSentence(sentence, idf))
      val withIndex = scored.zipWithIndex
      val top = withIndex.sortBy { case (scoredSentence, index) =>
        (-scoredSentence.score, index)
      }.take(10)
      val ordered = top.sortBy(_._2).map(_._1)

      IOHandler.printSummary(ordered)
  }
}
