package status

import javax.inject._

import play.api.db._
import play.api.mvc._

import scala.util.{Failure, Success, Try}

class SystemStatusController @Inject()(cc: ControllerComponents, db: Database) extends AbstractController(cc) {

  def status() = Action { implicit request: Request[AnyContent] =>

    tryToConnect() match {
      case Success(1) => Ok("Healthy") //TODO instead return some object with enum and timestamp
      case Failure(throwable) => InternalServerError("Can not connect to db " + throwable)
    }
  }

  private def tryToConnect(): Try[Int] = {
    db.withConnection { conn =>
      Try.apply {
        conn.createStatement().executeQuery("SELECT 1 as test")
      }
        .flatMap { rs =>
          rs.next() match {
            case true => Success(rs.getInt("test"))
            case _ => Failure(new RuntimeException("Can not connect to db"))
          }
        }
    }
  }
}
