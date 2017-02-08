package controllers

import play.api._
import play.api.mvc._
import models._


object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def jsontest = Action { request=>
    val content = Atom.deserialize(request.body.asText.get)
    println(content)
    Ok(request.body.asText.getOrElse("No request body!"))
  }
}