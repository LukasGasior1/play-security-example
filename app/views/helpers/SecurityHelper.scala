package views.helpers

import play.api.templates.Html
import play.api.cache.Cache
import play.api.mvc.{AnyContent, Request}
import models.User
import play.api.Play.current

object SecurityHelper {

  def user(implicit request: Request[AnyContent]): Option[User] =
    for {
      login <- request.session.get("user-login")
      user <- Cache.get(login)
    } yield user.asInstanceOf[User]

  def isNotLogged(html: Html)(implicit request: Request[AnyContent]) =
    user match {
      case Some(user: User) => ""
      case None => html
    }

  def isLogged(html: Html)(implicit request: Request[AnyContent]) =
    user match {
      case Some(user: User) => html
      case None => ""
    }

  def hasRole(role: String)(html: Html)(implicit request: Request[AnyContent]) =
    user match {
      case Some(user: User) if user.hasRole(role) => html
      case None => ""
    }

  def hasAllRoles(roles: String*)(html: Html)(implicit request: Request[AnyContent]) =
    user match {
      case Some(user: User) if user.hasAllRoles(roles:_*) => html
      case None => ""
    }

  def hasAnyRole(roles: String*)(html: Html)(implicit request: Request[AnyContent]) =
    user match {
      case Some(user: User) if user.hasAnyRole(roles:_*) => html
      case None => ""
    }
}
