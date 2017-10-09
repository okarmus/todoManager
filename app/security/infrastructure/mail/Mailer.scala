package security.infrastructure.mail

import courier.{Mailer => CourierMailer, _}

import scala.concurrent.Await


object Mailer {
  private val mailer = CourierMailer("smtp.gmail.com", 587)
    .auth(true)
    .as("todomanager42@gmail.com", "") //TODO the password should be in vault
    .startTtls(true)()


  def sendActivationMail(toEmail: String, content: String): Unit = { //TODO do something with async call

    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.concurrent.duration._

    val future = mailer(Envelope.from("todoManager@no-reply.com".addr)
      .to(toEmail.addr)
      .subject("Todo Manager - activate your account")
      .content(Text(content))
    )

    Await.ready(future, 5.seconds)

  }


  def main(args: Array[String]): Unit = {

    Mailer.sendActivationMail("mateusz.okarmus@gmail.com", "This is a sample message for activation e-mail")

  }
}
