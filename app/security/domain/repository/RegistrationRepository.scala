package security.domain.repository

import java.util.UUID
import javax.inject.Inject

import play.api.db.Database
import security.domain.User

trait RegistrationRepository {

  def contains(login: String): Boolean

  def storeUser(user: User): Unit
}

case class PostgresRegistrationRepository @Inject()(db: Database) extends RegistrationRepository {
  private val findByLoginQuery = "select * from users where login = '%s'"
  private val insertIntoUsers = "insert into users(login) values ('%s') on conflict do nothing" //TODO optimistic locking should be implemented
  private val insertIntoActivationUsers = "insert into activation_users(login, email, activation_token) values %values% on conflict do nothing"

  override def contains(login: String): Boolean = {
    db.withConnection { conn => conn.createStatement().executeQuery(String.format(findByLoginQuery, login)).next() }    //TODO it could be done in pmuch better way with functions etc !!
  }

  override def storeUser(user: User): Unit = {
    db.withTransaction { trans =>
      trans.createStatement().execute(String.format(insertIntoUsers, user.login))
      trans.createStatement().execute(activationUsersInsert(user))
    }
  }

  private def activationUsersInsert(user: User) : String = {
    val values = (user.login, user.email, UUID.randomUUID().toString)
      .productIterator
        .map{ "'" + _ + "'"}
      .mkString("(", ",", ")")

    insertIntoActivationUsers.replaceAll("%values%", values)
  }
}

