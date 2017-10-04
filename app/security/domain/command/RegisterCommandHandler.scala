package security.domain.command

import security.domain.{NotActiveUser, User}
import security.domain.repository.InMemoryRepository

import scala.util.{Failure, Success, Try}

case class RegisterCommand(login: String, email: String, repeatedEmail: String)


object RegisterCommandHandler {
  import InMemoryRepository._
  def handle(command: RegisterCommand) : Try[String] = {
    findByLogin(command.login)
        .map(_ => Failure(LoginAlreadyExistsException("Provided login is already used")))
        .getOrElse(newUser(command))
  }

  private def newUser(command: RegisterCommand): Try[String] = {
    storeUser(command)
    Success("User has been successfully stored")
  }

  implicit def commandToUser(command: RegisterCommand) : User = NotActiveUser(command.login, command.email)

}

case class LoginAlreadyExistsException(message: String) extends RuntimeException(message)