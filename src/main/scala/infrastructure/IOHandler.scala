package infrastructure

import domain.ScoredSentence
import java.nio.file.{Files, Path, Paths}
import scala.io.Source
import scala.jdk.CollectionConverters.*
import scala.util.Using

object IOHandler {
  def loadStopwords(resourceName: String): Either[String, Set[String]] = {
    val stream = Option(getClass.getResourceAsStream(s"/$resourceName"))
      .toRight(s"No se pudo encontrar el recurso de stopwords: $resourceName")

    stream.flatMap { inputStream =>
      Using(Source.fromInputStream(inputStream, "UTF-8")) { source =>
        source
          .getLines()
          .map(_.trim.toLowerCase)
          .filter(line => line.nonEmpty && !line.startsWith("#"))
          .toSet
      }.toEither.left.map(_ => s"No se pudo leer el recurso de stopwords: $resourceName")
    }
  }

  def loadTexts(directory: String): Either[String, LazyList[String]] = {
    val dirPath = Paths.get(directory)
    if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
      Left("El directorio indicado no existe o no es un directorio.")
    } else {
      val filesResult = Using(Files.list(dirPath)) { stream =>
        stream
          .iterator()
          .asScala
          .filter(path => Files.isRegularFile(path) && path.getFileName.toString.endsWith(".txt"))
          .toList
          .sortBy(_.getFileName.toString)
      }.toEither.left.map(_ => "No se pudo listar el directorio.")

      filesResult.flatMap { files =>
        if (files.isEmpty) {
          Left("No se encontraron archivos .txt en el directorio.")
        } else {
          val texts = files.foldLeft(Right(List.empty[String]): Either[String, List[String]]) {
            (acc, path) => acc.flatMap(list => readFile(path).map(content => list :+ content))
          }
          texts.map(list => LazyList.from(list))
        }
      }
    }
  }

  def printSummary(summary: List[ScoredSentence]): Unit = {
    summary.foreach { scored =>
      println(scored.sentence.original.text)
    }
  }

  private def readFile(path: Path): Either[String, String] = {
    Using(Source.fromFile(path.toFile, "UTF-8")) { source =>
      source.mkString
    }.toEither.left.map(_ => s"No se pudo leer el archivo: ${path.getFileName}")
  }
}
