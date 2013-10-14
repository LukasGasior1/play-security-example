package controllers

import play.api.mvc._
import scala.Some
import models.User
import play.api.cache.Cache
import play.api.Play.current

trait Secured {

  private def onUnauthorized(request: RequestHeader) =
    Results.Redirect(routes.Main.index).flashing("message" -> "Unauthorized!")

  def WithAuthentication(f: => User => Request[AnyContent] => Result) =
    Action( request =>
      request.session.get("user-login").flatMap(Cache get _) match {
        case Some(user: User) => f(user)(request)
        case None => onUnauthorized(request)
      }
    )

  def HasRole(role: String)(f: => User => Request[AnyContent] => Result) =
    WithAuthentication { user => request =>
      if (user.hasRole(role))
        f(user)(request)
      else
        onUnauthorized(request)
    }

  def HasAllRoles(roles: String*)(f: => User => Request[AnyContent] => Result) =
    WithAuthentication { user => request =>
      if (user.hasAllRoles(roles:_*))
        f(user)(request)
      else
        onUnauthorized(request)
    }

  def HasAnyRole(roles: String*)(f: => User => Request[AnyContent] => Result) =
    WithAuthentication { user => request =>
      if (user.hasAnyRole(roles:_*))
        f(user)(request)
      else
        onUnauthorized(request)
    }
}
