package model

import javax.inject.Inject

import akka.Done
import play.api.cache.AsyncCacheApi

import scala.concurrent.Future

class UserInfoRepo @Inject() (cache:AsyncCacheApi) {
  case class UserInfo(name:String,lname:String,email:String)
  def store(userInfo:UserInfo):Future[Done]={
    cache.set(userInfo.email,userInfo)
  }
  def getUser(email:String):Future[Option[UserInfo]]={
    cache.get[UserInfo](email)
  }
}
