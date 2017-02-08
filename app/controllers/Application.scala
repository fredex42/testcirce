package controllers

import cats.data.Validated.{Invalid, Valid}
import play.api._
import play.api.mvc._
import models._
import play.api.http.Writeable


object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def jsontest = Action { request=>
    Atom.deserialize(request.body.asText.get) match {
      case Valid(atom)=>Ok(atom.describe)
      case Invalid(error)=>BadRequest(error.toString)
    }
//
//    Ok(request.body.asText.getOrElse("No request body!"))
  }
}