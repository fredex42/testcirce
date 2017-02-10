package implicits

import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import io.circe.{Decoder, Encoder, Json}
import cats._
import cats.data.Validated
import models._

import Validated.{invalid, valid}
import cats.instances.all._
import shapeless.{Lazy, |¬|}
/**
  * Created by localhome on 10/02/2017.
  */
object macroconverter {
  implicit def decodeStruct[A <: Generic]:Decoder[A] = macro MacroConverterImpl.decodeStruct[A]
}

class MacroConverterImpl(val c:blackbox.Context) {
  import c.universe._

  def decodeStruct[A: c.WeakTypeTag](x: c.Tree): c.Tree = {
    val A = weakTypeOf[A]

    val apply = getApplyMethod(A)

    val params = apply.paramLists.head.zipWithIndex.map { case (param, i) =>
      val name = param.name
      val tpe = param.typeSignature
      val fresh = c.freshName(name)

      val implicitDecoder: c.Tree = getImplicitDecoder(tpe)

      // Note: we don't simply call `cursor.get[$tpe](...)` because we want to avoid allocating HistoryOp instances.
      // See https://github.com/travisbrown/circe/issues/329 for details.
      val decodeParam =
      q"""cursor.downField(${name.toString}).success.map(x=>x.as[$tpe]($implicitDecoder)).getOrElse(Left(DecodingFailure(${name.toString},cursor.history)))"""

      val applyParam =
        q"""realResults($i).get.asInstanceOf[$tpe]"""

      (applyParam, decodeParam)
    }

    params.foreach(param=>println(s"${param._1} => ${param._2}"))

    val finalcode = q"""{
      _root_.io.circe.Decoder.instance((cursor: _root_.io.circe.HCursor) =>
       Seq(..${params.map(_._2)}).partition(_.isLeft) match {
          case (Nil, results)=>
              val realResults = results.map(_.right)
              Right(Atom.apply(..${params.map(_._1)}))
          case (errors, results) =>
              val errorList:String = errors.map(_.left).foldLeft("")((acc, item)=>acc + item.get.toString + "; ")
              Left(DecodingFailure(errorList,Nil))
       }
      )
    }"""
    println(finalcode)
    println("------------------------")
    finalcode
  }


  private def getApplyMethod(tpe: Type): MethodSymbol = {
    tpe.companion.member(TermName("apply")) match {
      case symbol if symbol.isMethod && symbol.asMethod.paramLists.size == 1 => symbol.asMethod
      case _ => c.abort(c.enclosingPosition, "Not a valid Scrooge class: could not find the companion object's apply method")
    }
  }

  private def getUnionMemberClasses(unionType: Type): Iterable[Symbol] = {
    unionType.companion.members.filter { member =>
      if (member.isClass && member.name.toString != "UnknownUnionField") {
        member.asClass.baseClasses.contains(unionType.typeSymbol)
      } else false
    }
  }

  private def getImplicitDecoder(tpe: Type): c.Tree = {
    val decoderForType = appliedType(weakTypeOf[Decoder[_]].typeConstructor, tpe)

    val normalImplicitDecoder = c.inferImplicitValue(decoderForType)
    if (normalImplicitDecoder.nonEmpty) {
      // Found an implicit, no need to use Lazy.
      // We want to avoid Lazy as much as possible, because extracting its `.value` incurs a runtime cost.
      normalImplicitDecoder
    } else {
      // If we couldn't find an implicit, try again with shapeless `Lazy`.
      // This is to work around a problem with diverging implicits.
      // If you try to summon an implicit for heavily nested type, e.g. `Decoder[Option[Seq[String]]]` then the compiler sometimes gives up.
      // Wrapping with `Lazy` fixes this issue.
      val lazyDecoderForType = appliedType(weakTypeOf[Lazy[_]].typeConstructor, decoderForType)
      val implicitLazyDecoder = c.inferImplicitValue(lazyDecoderForType)
      if (implicitLazyDecoder.isEmpty) c.abort(c.enclosingPosition, s"Could not find an implicit Decoder[$tpe] even after resorting to Lazy")

      // Note: In theory we could use the `implicitLazyDecoder` that we just found, but... for some reason it crashes the compiler :(
      q"_root_.scala.Predef.implicitly[_root_.shapeless.Lazy[_root_.io.circe.Decoder[$tpe]]].value"
    }
  }

  private def getImplicitEncoder(tpe: Type): c.Tree = {
    val encoderForType = appliedType(weakTypeOf[Encoder[_]].typeConstructor, tpe)
    val normalImplicitEncoder = c.inferImplicitValue(encoderForType)
    if (normalImplicitEncoder.nonEmpty) {
      normalImplicitEncoder
    } else {
      val lazyEncoderForType = appliedType(weakTypeOf[Lazy[_]].typeConstructor, encoderForType)
      val implicitLazyEncoder = c.inferImplicitValue(lazyEncoderForType)
      if (implicitLazyEncoder.isEmpty) c.abort(c.enclosingPosition, s"Could not find an implicit Encoder[$tpe] even after resorting to Lazy")

      q"_root_.scala.Predef.implicitly[_root_.shapeless.Lazy[_root_.io.circe.Encoder[$tpe]]].value"
    }
  }

}