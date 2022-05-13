package Web

import scalatags.Text.all._
import scalatags.Text.tags2

type ScalaTag = scalatags.Text.TypedTag[String]

/**
  * Assembles the method used to layout ScalaTags
  */
object Layouts:
  // You can use it to store your methods to generate ScalaTags.
  def index(): ScalaTag =
    html(
      getHeader,
      body(
        getNav,
        getMessageContentDiv,
      )
    )

  def login(errorLogin: Boolean = false, errorRegister: Boolean = false): ScalaTag =
    html(
      getHeader,
      body(
        getNav,
        getLoginContentDiv(errorLogin ,errorRegister),
      )
    )

  def getHeader: ScalaTag =
    head(
      link(rel := "stylesheet", href := "/static/resources/css/main.css"),
      script(src := "/static/resources/js/main.js")
    )

  def getNav: ScalaTag =
    tag("nav")(
      a(`class` := "nav-brand")(
        "Bot-tender"
      ),
      div(`class` := "nav-item")(
        a(href := "/login")(
          "Log-in"
        ),
      ),
    )

  // Messages
  def getMessageContentDiv: ScalaTag =
    div(`class` := "content")(
      getBoardMessage,
      getForm,
    )

  def getBoardMessage: ScalaTag =
    div(id := "boardMessage")(
      //getMessageDiv,
      p(style := "text-align:center;")("Please wait, the messages are loading !"),
    )

  def getMessageDiv: ScalaTag =
    div(`class` := "msg")(
      span(`class` := "author")(),
      span(`class` := "msg-content")(),
    )

  def getForm: ScalaTag =
    form(id := "msgForm", onsubmit := "submitMessageForm(); return false")(
      div(id := "errorDiv", `class` := "errorMsg"),
      label(`for` := "messageInput")(
        "Your message:"
      ),
      input(id := "messageInput", `type` := "text", placeholder := "Write your message"),
      input(`type` := "submit"),
    )

  // Login
  def getLoginContentDiv(errorLogin: Boolean, errorRegister: Boolean): ScalaTag =
    div(`class` := "content")(
      p(`class` := "title1")("Login"),
      getLoginForm(errorLogin),
      p(`class` := "title1")("Register"),
      getRegisterForm(errorRegister),
    )

  def getLoginForm(error: Boolean): ScalaTag =
    form(id := "loginForm", action := "/login", method := "POST")(
      div(`class` := "errorMsg")(if error then "The specified user does not exists" else ""),
      label(`for` := "loginInput")(
        "Username:"
      ),
      input(name := "username", id := "loginInput", `type` := "text", placeholder := "Write your username"),
      input(`type` := "submit"),
    )

  def getRegisterForm(error: Boolean): ScalaTag =
    form(id := "registerForm", action := "/register", method := "POST")(
      div(`class` := "errorMsg")(if error then "The specified user already exists" else ""),
      label(`for` := "registerInput")(
        "Username:"
      ),
      input(name := "username", id := "registerInput", `type` := "text", placeholder := "Write your username"),
      input(`type` := "submit"),
    )

end Layouts
