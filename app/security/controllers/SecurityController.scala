package security.controllers

import javax.inject._

import play.api.mvc._
import security.domain.command.{LoginAlreadyExistsException, RegisterCommand, RegisterCommandHandler}

import scala.util.{Failure, Success, Try}

@Singleton
class SecurityController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def register() = Action { implicit request: Request[AnyContent] =>
    extractBody()
      .andThen(sendCommand())
      .andThen(buildResponse())
      .apply(request)
  }

  private def extractBody(): Request[AnyContent] => Try[String] = {
    request =>
      request.body.asJson
        .flatMap(json => (json \ "login").asOpt[String])
        .map(l => Success(l))
        .getOrElse(Failure[String](InvalidRequestBodyFormatException("Invalid request body format")))
  }

  private def sendCommand(): Try[String] => Try[String] = {
    import RegisterCommandHandler._
    maybeLogin => maybeLogin.map(RegisterCommand).flatMap(handle)
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
      case e : InvalidRequestBodyFormatException => BadRequest(e.message)
      case e : LoginAlreadyExistsException => BadRequest(e.message)
      case _ => InternalServerError("Unexpected exception occurred")
    }
  }
}

private case class InvalidRequestBodyFormatException(message: String) extends IllegalArgumentException(message)
