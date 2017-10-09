package security.domain

import java.util.UUID

case class ActivationUser(login: String, email: String, activationToken: UUID, activationSent: Boolean)
