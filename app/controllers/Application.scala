package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.{User, UserDAO}
import play.api.cache.Cache
import play.api.Play.current

object Register extends Controller {

  val availableRoles = Seq(("ADMIN", "Administrator"), ("TESTER", "Tester"))

  val registerForm = Form[User](
    mapping(
      "login" -> text,
      "password" -> text,
      "roles" -> seq(text)
    ) {
      (login, password, roles) => User(login = login, password = password.encode(), roles = roles)
    } {
      user => Some((user.login, "", user.roles))
    }
  )

  def registerPage = Action { implicit request =>
    Ok(views.html.register(registerForm))
  }

  def processRegister = Action { implicit request =>
    registerForm.bindFromRequest.fold(
      formWithErrors => Redirect(routes.Register.registerPage).flashing("message" -> "Form errors!"),
      user => {
        UserDAO.save(user)
        Redirect(routes.Main.index).flashing("message" -> "Your account has been registered.")
      }
    )
  }
}

object UserSession extends Controller with Secured {

  val loginForm = Form(
    tuple(
      "login" -> text,
      "password" -> text
    ) verifying (result => result match {
      case (login, password) => UserDAO.findByLoginAndPassword(login, password.encode).isDefined
    })
  )

  def loginPage = Action { implicit request =>
    Ok(views.html.login())
  }

  def processLogin = Action { implicit request =>
    val loginError = Redirect(routes.UserSession.loginPage).flashing("message" -> "Login or password incorrect")
    loginForm.bindFromRequest.fold(
      formWithErrors => loginError,
      form => {
        UserDAO.findByLogin(form._1) match {
          case Some(user: User) => {
            Cache.set(form._1, user, 10000)
            Redirect(routes.Main.index).withSession("user-login" -> form._1)
          }
          case None => loginError
        }
      }
    )
  }

  def logout = WithAuthentication { user => _ =>
    Cache.remove(user.login)
    Redirect(routes.Main.index).withNewSession.flashing("message" -> "You have been logged out!")
  }
}

object Main extends Controller with Secured {

  def index = Action { implicit request =>
    Ok(views.html.index())
  }

  def loggedUserArea = WithAuthentication { user => implicit request =>
    Ok(views.html.logged(user))
  }

  def adminArea = HasRole("ADMIN") { user => implicit request =>
    Ok(views.html.admin(user))
  }
}