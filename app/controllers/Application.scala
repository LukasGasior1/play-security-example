package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.{User, UserDAO}

object Application extends Controller {

  val availableRoles = Seq(("ADMIN", "Administrator"), ("TESTER", "Tester"))

  implicit class StringWithEncode(str: String) {
    def encode(): String = {
      val md = java.security.MessageDigest.getInstance("SHA-1")
      new String(md.digest(str.getBytes))
    }
  }

  val loginForm = Form(
    tuple(
      "login" -> text,
      "password" -> text
    ) verifying (result => result match {
      case (login, password) => UserDAO.findByLoginAndPassword(login, password.encode).isDefined
    })
  )

  def processLogin = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => Redirect(routes.Application.loginPage).flashing("message" -> "Login or password incorrect"),
      form => Redirect(routes.Application.index).withSession(sessionKey -> form._1)
    )
  }

  def logout = Action { _ =>
    Redirect(routes.Application.index).withNewSession.flashing("message" -> "You are now logged out!")
  }

  def index = Action { implicit request =>
    Ok(views.html.index())
  }

  def loginPage = Action { implicit request =>
    Ok(views.html.login())
  }

  def adminArea = HasRole("ADMIN") { implicit user => implicit request =>
    Ok(views.html.admin())
  }

  def loggedUserArea = WithAuthentication { implicit user => implicit request =>
    Ok(views.html.logged())
  }

  def registerPage = Action { implicit request =>
    Ok(views.html.register(registerForm))
  }

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

  def processRegister = Action { implicit request =>
    registerForm.bindFromRequest.fold(
      formWithErrors => Redirect(routes.Application.registerPage).flashing("message" -> "Form errors !"),
      user => {
        UserDAO.save(user)
        Redirect(routes.Application.index).flashing("message" -> "Your account has been registered.")
      }
    )
  }

}