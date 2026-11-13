package ox.flow.csv.format.typed
import ox.flow.csv.format.typed.PredefinedFormats
import ox.flow.csv.format.typed.PredefinedFormats.*
case class Foo(a:Option[String])
case class Bar(a:String)


object Tests extends App{
  val tt = PredefinedFormats.PostgreSqlCsv[Foo]

  val uu = Rfc4180[Foo]
}
