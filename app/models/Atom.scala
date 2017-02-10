package models
import io.circe._
import io.circe.jawn.decode
import cats.syntax.apply._
import cats.syntax.all._
import cats.data.{NonEmptyList, Validated}

object Atom {
  def deserialize(jsonContent:String) : Validated[Any,Atom] = {

    val decoder:Decoder[Atom] =
      Decoder.instance(cursor=>{
        Seq(
          cursor.downField("id").success.map(x=>x.as[String]).getOrElse(Left(DecodingFailure("missing id field",cursor.history))),
          cursor.downField("atomType").success.map(x=>x.as[String]).getOrElse(Left(DecodingFailure("missing atomType field",cursor.history))),
          cursor.downField("labels").success.map(x=>x.as[List[String]]).getOrElse(Left(DecodingFailure("missing labels field",cursor.history))),
          cursor.downField("defaultHtml").success.map(x=>x.as[String]).getOrElse(Left(DecodingFailure("missing defaultHtml field",cursor.history))),
          Right("(not implemented)"),
          Right("(not implemented)")
        ).partition(_.isLeft) match {
          case (Nil, results)=>
            val realResults = results.map(_.right)
            Right(Atom.apply(realResults.head.get.asInstanceOf[String],
              realResults(1).get.asInstanceOf[String],
              realResults(2).get.asInstanceOf[List[String]],
              realResults(3).get.asInstanceOf[String],
              realResults(4).get.asInstanceOf[String],
              realResults(5).get.asInstanceOf[String]
            ))
          case (errors, results) =>
            val errorList:String = errors.map(_.left).foldLeft("")((acc, item)=>acc + item.get.toString + "; ")
            Left(DecodingFailure(errorList,Nil))
        }
      })

    parser.parse(jsonContent) match {
      case Right(content)=>decoder.accumulating(content.hcursor)
      case Left(error)=>Validated.invalid(NonEmptyList(error,List()))
    }
  }
}

case class Atom (id:String, atomType:String, labels: List[String], defaultHtml:String, data:String,contentChangeDetails:String){

  def describe:String = {
    s"id: $id; atom type: $atomType; labels: $labels"
  }
}
