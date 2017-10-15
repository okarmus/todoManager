package security.application.activation.email

import java.util.concurrent.TimeUnit
import javax.inject.Inject

import akka.actor.ActorSystem

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

class ActivationEmailSendTask @Inject()(actorSystem: ActorSystem, activationEmailSendDAO: PostgresActivationEmailSendDAO, mailer: Mailer)(implicit executionContext: ExecutionContext) {

  actorSystem.scheduler.schedule(initialDelay = 15 seconds, interval = 15 seconds) {
    activationEmailSendDAO.readNotSentActivation()
      .map { user =>
        mailer.sendActivationMail(user)
        user                            //TODO should be more like a map not this
      }.foreach{
      user => activationEmailSendDAO.markActivationAsSent(user.login)
    }
  }

  implicit class intToFiniteDuration(value: Int) {

    import TimeUnit._

    def seconds(): FiniteDuration = FiniteDuration(value, SECONDS)
  }

}

