package security.domain.command

import javax.inject.Inject

import security.domain.repository.PostgresRegistrationRepository
import security.domain.{NotActiveUser, User}

import scala.util.{Failure, Success, Try}

case class RegisterCommand(login: String, email: String, repeatedEmail: String)

class RegisterCommandHandler @Inject()( repository : PostgresRegistrationRepository) {

  def handle(command: RegisterCommand): Try[String] = {
    repository.contains(command.login) match {          //TODO what about already used e-mail?
      case false => handleNewUser(command)
      case true => Failure(LoginAlreadyExistsException("Provided login is already used"))
    }
  }

  private def handleNewUser(command: RegisterCommand): Try[String] = {
    storeUser()
      .andThen(_ => Success("User has been successfully stored"))
      .apply(command)
  }

  private def storeUser() : RegisterCommand => User = {
    command => {
      val user = commandToUser(command)
      repository.storeUser(user)
      user
    }
  }

  private def commandToUser(command: RegisterCommand): User = NotActiveUser(command.login, command.email)

}

case class LoginAlreadyExistsException(message: String) extends RuntimeException(message)