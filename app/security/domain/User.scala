package security.domain

import java.util.UUID

import scala.util.{Failure, Success, Try}

trait User {
  def login: String
  def email: String

  val isActive: Boolean
}

case class ActiveUser(login: String, email: String, password: String) extends User {
  //TODO password should be validated and should not be stored as a plain text !!

  implicit def uuidToString(uuid: UUID) : String = uuid.toString

  override val isActive: Boolean = true

  def login(password: String) : Try[String] = {
    import UUID._
    password match {
      case this.password => Success(randomUUID)
      case _ => Failure(IncorrectPasswordException("Provided password is incorrect"))
    }
  }
}

case class NotActiveUser(login: String, email: String) extends User {
  override val isActive: Boolean = false
}


case class IncorrectPasswordException(private val message: String = "", private val cause: Throwable = None.orNull) extends RuntimeException(message, cause)

