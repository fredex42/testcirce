package models
import io.circe._, io.circe.jawn.decode
import cats.syntax.apply._
import cats.syntax.all._

/**
  * Created by localhome on 08/02/2017.
  */
object Atom {
  def deserialize(jsonContent:String) : Any = {

    val decoder:Decoder[Atom] =
//     ( Decoder[String]
//       |@| Decoder[String]
//       |@| Decoder[List[String]]
//       |@| Decoder[String]
//       |@| Decoder[String]
//       |@| Decoder[String]
//       ).map(Atom.apply)
      Decoder.instance(cursor=>{
        List(
          cursor.downField("id").success.map(x=>x.as[String]).getOrElse(Left(DecodingFailure("missing id field",cursor.history))),
          cursor.downField("atomType").success.map(x=>x.as[String]).getOrElse(Left(DecodingFailure("missing atomType field",cursor.history))),
          cursor.downField("labels").success.map(x=>x.as[List[String]]).getOrElse(Left(DecodingFailure("missing labels field",cursor.history))),
          cursor.downField("defaultHtml").success.map(x=>x.as[String]).getOrElse(Left(DecodingFailure("missing defaultHtml field",cursor.history))),
          Right("(not implemented)"),
          Right("(not implemented)")
        ).partition(_.isLeft) match {
          case (Nil, results)=>
            val realResults = results.map(_.right)
            Right(Atom.apply(realResults.head.asInstanceOf[String],
              realResults(1).asInstanceOf[String],
              realResults(2).asInstanceOf[List[String]],
              realResults(3).asInstanceOf[String],
              realResults(4).asInstanceOf[String],
              realResults(5).asInstanceOf[String]
            ))
          case (errors, results) =>
            val errorList:String = errors.map(_.left).foldLeft("")((acc, item)=>acc + item.toString + "; ")
            Left(DecodingFailure(errorList,Nil))
        }
      })
//    Decoder.instance(cursor=>{
//      for {
//        atomId <- cursor.downField("id").success.map(x=>x.as[String]).getOrElse(Left(DecodingFailure("missing id field",cursor.history)))
//        atomType <- cursor.downField("atomType").success.map(x=>x.as[String]).getOrElse(Left(DecodingFailure("missing atomType field",cursor.history)))
//        labels <- cursor.downField("labels").success.map(x=>x.as[List[String]]).getOrElse(Left(DecodingFailure("missing labels field",cursor.history)))
//        defaultHtml <- cursor.downField("defaultHtml").success.map(x=>x.as[String]).getOrElse(Left(DecodingFailure("missing defaultHtml field",cursor.history)))
//        data  <- Right("(not implemented)") //cursor.downField("data").success.map(x=>x.as[String]).getOrElse(Left(DecodingFailure("missing data field",cursor.history)))
//        contentChangeDetails  <- Right("(not implemented") //cursor.downField("contentChangeDetails").success.map(x=>x.as[String]).getOrElse(Left(DecodingFailure("missing contentChangeDetails field",cursor.history)))
//      } yield Atom.apply(atomId,atomType,labels,defaultHtml,data,contentChangeDetails)
//      println(cursor.history)
//      println(cursor.downField("id").as[String])
//      Left(DecodingFailure("test",cursor.history))
//    })

    decoder.accumulating(parser.parse(jsonContent).right.get.hcursor)

  }
}

case class Atom (id:String, atomType:String, labels: List[String], defaultHtml:String, data:String,contentChangeDetails:String){

}
