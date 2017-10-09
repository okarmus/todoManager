package security.web

import javax.inject._

import play.api.mvc._
import security.domain.command.{LoginAlreadyExistsException, RegisterCommand, RegisterCommandHandler}

import scala.util.{Failure, Try}

@Singleton
class RegistrationController @Inject()(
                                        cc: ControllerComponents,
                                        commandHandler: RegisterCommandHandler
                                      ) extends AbstractController(cc) {

  def register() = Action { implicit request: Request[AnyContent] =>
    extractBody()
      .andThen(sendCommand())
      .andThen(buildResponse())
      .apply(request)
  }

  private def extractBody(): Request[AnyContent] => Try[RegistrationForm] = {
    import RegistrationForm._
    request =>
      request.body.asJson
        .map(deserializeForm)
        .getOrElse(Failure(new IllegalArgumentException("Request body is not provided")))
  }

  private def sendCommand(): Try[RegistrationForm] => Try[String] = {
    maybeForm =>
      maybeForm
        .map(maybeForm => RegisterCommand(maybeForm.login, maybeForm.email, maybeForm.repeatedEmail))
        .flatMap(commandHandler.handle)
  }

  private def buildResponse(): Try[String] => Result = {
    result =>
      result.toEither match {
        case Right(message) => Ok(message)
        case Left(throwable) => matchFailure(throwable)
      }
  }

  def matchFailure(throwable: Throwable): Result = {
    throwable match {
      case e: InvalidRequestBodyFormatException => BadRequest(e.message)
      case e: LoginAlreadyExistsException => BadRequest(e.message)
      case _ => InternalServerError("Unexpected exception occurred")
    }
  }
}

private case class InvalidRequestBodyFormatException(message: String) extends IllegalArgumentException(message)
