import models.{UserDAO, User}
import play.api.mvc._
import scala.Some

package object controllers {

  val sessionKey = "email"

  private def onUnauthorized(request: RequestHeader) =
    Results.Redirect(routes.Application.index).flashing("message" -> "Unauthorized!")

  def userLogin(request: RequestHeader): Option[String] =
    request.session.get(sessionKey)

  def WithAuthentication(f: => User => Request[AnyContent] => Result) =
    Security.Authenticated(userLogin, onUnauthorized) { login =>
      UserDAO.findByLogin(login) match {
        case Some(user: User) => Action(request => f(user)(request))
        case None => Action(request => onUnauthorized(request))
      }
    }

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
        onUnauthorized(request)
      else
        f(user)(request)
    }

}
