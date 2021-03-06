package controllers

import cats.data.Validated.{Invalid, Valid}
import play.api._
import play.api.mvc._
import models._
import play.api.http.Writeable
import implicits.macroconverter._
import io.circe._
import io.circe.parser.decode

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def jsontest = Action { request=>
    parser.parse(request.body.asText.get) match {
      case Right(jsondata)=>
        jsondata.as[Atom] match {
          case Right(atom)=>Ok(atom.describe)
          case Left(errors)=>BadRequest(errors.toString)
        }
      case Left(errors)=>BadRequest(errors.toString)
    }
  }
}