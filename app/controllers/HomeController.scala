package controllers

import javax.inject._

import model.UserInfoRepo
import play.api.mvc.{Action, _}
import play.api.data.Forms._
import play.api.data.Form
import play.api.i18n.I18nSupport
import user.UserForm

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */

//case class User(name: String, id: Int)
case class UserData(name: String, age: Int)
@Singleton
class HomeController @Inject()(cc: ControllerComponents,userForm: UserForm,userInfoRepo: UserInfoRepo) extends AbstractController(cc) with I18nSupport {
  implicit val message = cc.messagesApi
  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  val userform: Form[UserData] = Form(
    mapping(
      "name" -> nonEmptyText,
      "age" -> number(min = 0)
    )(UserData.apply)(UserData.unapply)
  )

  //  def index = Action {
  //    val user1 = User("kritika", 1)
  //    val content = views.html.index(user1)
  //    Ok(content)
  //  }
  def index = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index(userform))
  }


  def validate() = Action { implicit request: Request[AnyContent] =>

    userform.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.error(formWithErrors))
      },
      userData => {
        val newUser = UserData(userData.name, userData.age)
        // val id =models.UserData.create(newUser)
        // Ok(views.html.display(newUser))
        Redirect(routes.HomeController.hello).withSession("name" -> newUser.name, "age" -> newUser.age.toString)
          .flashing("success" -> "user created")
        // })


      })
  }



  //  def createUser = Action { implicit request =>
//    userform.bindFromRequest().fold(
//      formWithErrors => BadRequest(views.html.index(formWithErrors)),
//      user => Ok(s"UserNAME ${user.name} created successfully"))
//  }GET     /                           controllers.HomeController.index



    def hello =  Action { implicit request: Request[AnyContent] =>
      Ok(views.html.hello())
    }

  case class UserInfo(name:String,lname:String,email:String)

  def index1:Action[AnyContent]=Action.async{implicit request=>
    Future.successful(Ok(views.html.form(userForm.userInfoForm)))
  }
  def storeData:Action[AnyContent]=Action.async{implicit request=>
    userForm.userInfoForm.bindFromRequest().fold(
      formWithError=>{
      Future.successful(BadRequest(views.html.form(formWithError)))
      },
      data=>{
     userInfoRepo.getUser(data.email).flatMap{optionalRecord=>
       optionalRecord.fold{
         //none
         val record=userInfoRepo.UserInfo(data.name,data.lname,data.email)
         userInfoRepo.store(record).map{_=>
           Ok("stored")
         }
       }{//some
         _ =>
           Future.successful(InternalServerError("user already exist"))
       }
     }
  })
  }

  def getData(email: String): Action [AnyContent] = Action.async { implicit request =>
    userInfoRepo.getUser(email).map { optionalRecord =>
      optionalRecord.fold {
        NotFound("Oops! user not found")
      } {
        record =>
          Ok(s"User's FullName is: ${record.name},${record.lname},${record.email}")
      }
    }
  }
}
