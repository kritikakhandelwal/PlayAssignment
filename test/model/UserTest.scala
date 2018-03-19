package model


import akka.Done
import org.specs2.mutable.Specification
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.Await
import scala.reflect.ClassTag
import scala.concurrent.duration.Duration


class ModelTest[T:ClassTag] {
  def fakeApp: Application = {
    new GuiceApplicationBuilder().build()
  }

  lazy val app2doo = Application.instanceCache[T]
  lazy val repository: T = app2doo(fakeApp)
}

class UserTest extends Specification {

 val repo=new ModelTest[UserInfoRepo]
  "store detail of user" in {
 val user=repo.repository.UserInfo("kritika","khandelwal","kr.kh@gmail.com")
    val storeResult=Await.result(repo.repository.store(user),Duration.Inf)
    storeResult must equalTo(Done)

  }
}

