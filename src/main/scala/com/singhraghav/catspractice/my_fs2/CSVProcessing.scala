package com.singhraghav.catspractice.my_fs2

import cats.effect._
import fs2.io.file._
import fs2._

import java.io.{BufferedReader, FileReader}
import java.nio.file.{Paths, Files => JFIles}
import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.jdk.CollectionConverters.ListHasAsScala
import scala.util.{Try, Using}
object CSVProcessing extends IOApp.Simple {

  case class LegoSet(id: String, name: String, year: Int, themeId: Int, numParts: Int)

  def parseLegoSet(line: String): Option[LegoSet] = Try{
    val splitted = line.split(",")
    LegoSet(id = splitted(0), name = splitted(1), year = splitted(2).toInt, themeId = splitted(3).toInt, numParts = splitted(4).toInt)
  }.toOption
  def readLegoSetImperative(filename: String, p: LegoSet => Boolean, limit: Int): List[LegoSet] = {
    var reader: BufferedReader = null
    val legoSets: ListBuffer[LegoSet] = ListBuffer.empty
    var counter = 0
    try {
      reader = new BufferedReader(new FileReader(filename))
      var line = reader.readLine()
      while (line != null && counter < limit) {
        parseLegoSet(line).filter(p).foreach{ l =>
          legoSets.append(l)
          counter += 1
        }
        line = reader.readLine()
      }
      legoSets.toList.take(limit)
    } finally {
      reader.close()
    }
  }

  def readLegoSetList(filename: String, p: LegoSet => Boolean, limit: Int): List[LegoSet] = {
    JFIles.readAllLines(Paths.get(filename))
      .asScala
      .flatMap(parseLegoSet)
      .toList
      .filter(p)
      .take(limit)
  }

  def readLegoSetIterator(filename: String, p: LegoSet => Boolean, limit: Int): List[LegoSet] = {
    Using(Source.fromFile(filename)) { source =>
      source
        .getLines()
        .flatMap(parseLegoSet)
        .filter(p)
        .take(limit)
        .toList
    }.get
  }

  def readLegoSetStream(filename: String, p: LegoSet => Boolean, limit: Int): IO[List[LegoSet]] = {
    Files[IO]
      .readAll(Path(filename))
      .through(text.utf8.decode)
      .through(text.lines)
      .map(parseLegoSet)
      .evalTap(IO.println)
      .unNone
      .filter(p)
      .take(limit)
      .compile
      .toList
  }
  override def run: IO[Unit] = {
    val filename = "sets.csv"
    readLegoSetStream(filename, (lego: LegoSet) => lego.id.startsWith("SW"), 5).flatMap(IO.println)
  }
}
