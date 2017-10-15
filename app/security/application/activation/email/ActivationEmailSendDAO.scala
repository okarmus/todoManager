package security.application.activation.email

import java.sql.ResultSet
import javax.inject.Inject

import play.api.db.Database


case class ActivationUser(login: String, email: String, activationToken: String, activationSent: Boolean)


trait ActivationEmailSendDAO {

  def readNotSentActivation(): Option[ActivationUser]

  def markActivationAsSent(login: String): Unit

}

case class PostgresActivationEmailSendDAO @Inject()(db: Database) extends ActivationEmailSendDAO {

  val selectUserWithoutActivationSent = "select login, email, activation_token, activation_sent from activation_users where activation_sent=false limit 1"
  val updateAsActivationSent = "update activation_users set activation_sent = true where login = '%login%'"


  override def readNotSentActivation(): Option[ActivationUser] = findOne(
    selectUserWithoutActivationSent,
    rs => ActivationUser(
      rs.getString("login"),
      rs.getString("email"),
      rs.getString("activation_token"),
      rs.getBoolean("activation_sent")
    )
  )


  override def markActivationAsSent(login: String): Unit = db.withConnection { conn =>
    conn.createStatement().execute(updateAsActivationSent.replaceAll("%login%", login))
  }


  //TODO this should probably be moved as some trait and used in another daos/repositories!! !! !!
  private def findOne[A](sql: String, f: ResultSet => A): Option[A] = {
    db.withConnection { conn =>
      val result = conn.createStatement().executeQuery(sql)
      if (result.next()) Some(f(result)) else None
    }
  }
}


