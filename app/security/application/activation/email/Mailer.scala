package security.application.activation.email

import courier.{Mailer => CourierMailer, _}

import scala.concurrent.Await


class Mailer {

  private val mailer = CourierMailer("smtp.gmail.com", 587)
    .auth(true)
    .as("todomanager42@gmail.com", "") //TODO the password should be in vault
    .startTtls(true)()


  def sendActivationMail(user: ActivationUser): Unit = {

    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.concurrent.duration._

    val future = mailer(Envelope.from("todoManager@no-reply.com".addr)
      .to(user.email.addr)
      .subject("Todo Manager - activate your account")
      .content(content(user)))
    Await.ready(future, 5.seconds)
  }

  private def content(user: ActivationUser) : String = {
     s"""
       |Hi ${user.login}!
       |
       |Welcome to Todo Manager application
       |Please click below link to activate your account
       |http://localhost:9000/${user.activationToken}/activate
       |
       |Best regards,
       |Todo Manager crew
     """.stripMargin
  }

  implicit def StringToText(string: String) : Text = Text(string)

}
