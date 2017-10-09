package security.application.activate

import security.domain.User

class ActivationQueue {



  def push(user: User) : Unit = {
    println("user has been pushed to activation queue")
  }


}
