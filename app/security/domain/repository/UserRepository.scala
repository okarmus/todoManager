package security.domain.repository

import security.domain.User

trait UserRepository {

  def findByLogin(login: String) : Option[User]
  def storeUser(user: User) : Unit
}

object InMemoryRepository extends UserRepository {
  var users: List[User] = List()

  override def findByLogin(login: String): Option[User] = users.find((u: User) => u.login.equals(login))

  override def storeUser(user: User) : Unit = this.users = user :: users
}
