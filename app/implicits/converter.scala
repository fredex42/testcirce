package implicits

import io.circe.{Decoder, DecodingFailure}
import models._

/**
  * Created by localhome on 10/02/2017.
  */
object converter {
//  implicit val decoder:Decoder[Atom] =
//    Decoder.instance(cursor=>{
//      Seq(
//        cursor.downField("id").success.map(x=>x.as[String]).getOrElse(Left(DecodingFailure("missing id field",cursor.history))),
//        cursor.downField("atomType").success.map(x=>x.as[String]).getOrElse(Left(DecodingFailure("missing atomType field",cursor.history))),
//        cursor.downField("labels").success.map(x=>x.as[List[String]]).getOrElse(Left(DecodingFailure("missing labels field",cursor.history))),
//        cursor.downField("defaultHtml").success.map(x=>x.as[String]).getOrElse(Left(DecodingFailure("missing defaultHtml field",cursor.history))),
//        Right("(not implemented)"),
//        Right("(not implemented)")
//      ).partition(_.isLeft) match {
//        case (Nil, results)=>
//          val realResults = results.map(_.right)
//          Right(Atom.apply(realResults.head.get.asInstanceOf[String],
//            realResults(1).get.asInstanceOf[String],
//            realResults(2).get.asInstanceOf[List[String]],
//            realResults(3).get.asInstanceOf[String],
//            realResults(4).get.asInstanceOf[AtomData],
//            realResults(5).get.asInstanceOf[ContentChangeDetails]
//          ))
//        case (errors, results) =>
//          val errorList:String = errors.map(_.left).foldLeft("")((acc, item)=>acc + item.get.toString + "; ")
//          Left(DecodingFailure(errorList,Nil))
//      }
//    })
}
