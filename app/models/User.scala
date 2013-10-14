package models

import com.novus.salat.dao.SalatDAO
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject

case class User(id: ObjectId = new ObjectId, login: String, password: String, roles: Seq[String]) {

  def hasRole(role: String) =
    roles contains role

  def hasAnyRole(roles: String*) =
    !this.roles.intersect(roles).isEmpty

  def hasAllRoles(roles: String*) =
    roles forall (this.roles.contains)
}

object UserDAO extends SalatDAO[User, ObjectId](db("users")) {

  def findByLoginAndPassword(login: String, password: String): Option[User] =
    findOne(MongoDBObject("login" -> login, "password" -> password))

  def findByLogin(login: String): Option[User] =
    findOne(MongoDBObject("login" -> login))
}