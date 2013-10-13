package controllers

import play.api.mvc._
import scala.Some

object Security {
  def hasRole(role: String)(implicit request: Request[AnyContent]) =
    request.session.get("roles") match {
      case Some(role: String) => role.split(",").contains(role)
      case None => false
    }

}
