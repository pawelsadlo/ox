package ox.flow.csv.codec

import org.apache.commons.csv.CSVFormat


trait CsvCodec[T]:
  def decode(from: Option[String]): T

object CsvCodec:
  given CsvCodec[Int] {
    override def decode(from: Option[String]): Int = from.fold(0,_.toInt)
  }

  given [T] => CsvCodec[T] => CsvCodec[Option[T]] {
    override def decode(from: Option[String]): Option[T] = from match {
      case None => None
      case some => Option(summon[CsvCodec[T]].decode(some))
    }
  }

