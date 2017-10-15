package security.application.activation.email

import com.google.inject.AbstractModule

class EmailModule extends AbstractModule {
  override def configure() = {
    bind(classOf[ActivationEmailSendTask]).asEagerSingleton()
  }
}
