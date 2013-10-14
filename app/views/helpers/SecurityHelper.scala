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
    if (!user.isDefined) html

  def isLogged(html: Html)(implicit request: Request[AnyContent]) =
    if (user.isDefined) html

  def hasRole(role: String)(html: Html)(implicit request: Request[AnyContent]) =
    for (user <- user if user.hasRole(role)) yield html

  def hasAllRoles(roles: String*)(html: Html)(implicit request: Request[AnyContent]) =
    for (user <- user if user.hasAllRoles(roles:_*)) yield html

  def hasAnyRole(roles: String*)(html: Html)(implicit request: Request[AnyContent]) =
    for (user <- user if user.hasAnyRole(roles:_*)) yield html
}
