package user

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.Form


case class UserInfo(name:String,lname:String,email:String)

class UserForm {

  val userInfoForm=Form(
    mapping(
    "fname"->text.verifying("",_.nonEmpty),
    "lname"->text.verifying("",_.nonEmpty),
    "email"->email
  )(UserInfo.apply)(UserInfo.unapply))
}

