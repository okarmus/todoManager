package security.domain.repository

import javax.inject.Inject

import play.api.db.Database
import security.domain.User

trait RegistrationRepository {

  def contains(login: String) : Boolean
  def storeUser(user: User) : Unit
}

object InMemoryRegistrationRepository extends RegistrationRepository {
  var users: List[User] = List()

  override def contains(login: String): Boolean = users.contains((u: User) => u.login.equals(login))

  override def storeUser(user: User) : Unit = this.users = user :: users
}

class PostgresRegistrationRepository @Inject() (db: Database) extends RegistrationRepository {
  private val findByLoginQuery = "select * from users where login = '%s'"
  private val insertIntoUsers = "insert into users(login) values ('%s') on conflict do nothing"  //TODO optimistic locking should be implemented
  private val insertIntoActivationUsers = "insert into activation_users(login, email, activation_token) values %values% on conflict do nothing"

  override def contains(login: String): Boolean = {
    db.withConnection{ conn =>
      conn.createStatement()
        .executeQuery(String.format(findByLoginQuery,login))
        .next() //TODO this should be done in a better way
    }
  }

  override def storeUser(user: User): Unit = {
    db.withTransaction{ trans =>
      trans.createStatement().execute(String.format(insertIntoUsers, user.login))
    }
  }
}

object PostgresRegistrationRepository {

}

