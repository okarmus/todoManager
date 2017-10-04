package security.web

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.JsValue

import scala.util.{Failure, Success, Try}

case class RegistrationForm(login: String, email: String, repeatedEmail: String)


case class UserData(name: String, age: Int)

object RegistrationForm {

  private val form = Form(
    mapping(
      "login" -> text,
      "email" -> email,
      "repeatedEmail" -> email)
    (RegistrationForm.apply)(RegistrationForm.unapply))

  def deserializeForm(jsonBody: JsValue): Try[RegistrationForm] = {
    Try(form.bind(jsonBody).get)
      .flatMap(validate)
      .transform(
        Success(_),
        _ => Failure(InvalidRequestBodyFormatException("Wrong body format exception")))
  }

  private def validate(form: RegistrationForm) : Try[RegistrationForm] =
      form match {
        case f if  f.email.equals(f.repeatedEmail) => Success(form)
        case _ => Failure(InvalidRequestBodyFormatException("Emails does not match"))   //TODO you could think about rewriting that so it will actually return this as a response
      }
}
