package Web

import scalatags.Text.all.*
import scalatags.Text.tags2

type ScalaTag = scalatags.Text.TypedTag[String]

/**
  * Assembles the method used to layout ScalaTags
  */
object Layouts:
  /*********************
  *  ----- Pages ----- *
  *********************/
  def bodyPage(navItems: Seq[ScalaTag], contents: Seq[ScalaTag]): ScalaTag =
    html(
      getHeader,
      body(
        getNav(navItems),
        div(`class` := "content")(contents)
      )
    )

  def homePage(isLogged: Boolean = false): ScalaTag =
    bodyPage(
      getConnectionNavItems(isLogged),
      Seq(
        getBoardMessage,
        getForm(
          "Your message:",
          "/",
          "Write your message",
        )
      )
    )

  def loginPage(errorMsg: String = null): ScalaTag =
    bodyPage(
      Seq(navItemHomePage),
      Seq(
        h1("Login"),
        getForm(
          "Username:",
          "/login",
          "Enter your pseudo",
          errorMsg
        )
      )
    )

  def registerPage(errorMsg: String = null): ScalaTag =
    bodyPage(
      Seq(navItemHomePage),
      Seq(
        h1("Register"),
        getForm(
          "Username:",
          "/register",
          "Enter your pseudo",
          errorMsg
        )
      )
    )

  def loginStatusChangedPage(isLogin: Boolean, msg: String): ScalaTag =
    bodyPage(
      getConnectionNavItems(isLogin).+:(navItemHomePage),
      Seq(
        p(style := "text-align:center;")(msg)
      ),
    )

  /************************
  *  ----- elements ----- *
  ************************/
  // head
  def getHeader: ScalaTag =
    head(
      link(rel := "stylesheet", href := "/static/resources/css/main.css"),
      script(src := "/static/resources/js/main.js")
    )

  // nav
  def getNav(navItems: Seq[ScalaTag]): ScalaTag =
    tag("nav")(
      a(`class` := "nav-brand")("Bot-tender"),
      navItems
    )

  // Create navigation item. A link in the nav.
  def navItem(ref: String, text: String): ScalaTag =
    div(`class` := "nav-item")(a(href := ref)(text))

  // nav item to go to the homepage.
  def navItemHomePage: ScalaTag =
    navItem("/", "Go to the message board")

  // Get the navigation items based on the login status
  def getConnectionNavItems(isLogged: Boolean): Seq[ScalaTag] =
    if isLogged then
      Seq(navItem("/logout", "logout"))
    else
      Seq(
        navItem("/login", "Log-in"),
        navItem("/register", "Sign-up")
      )

  // chatroom display
  def getBoardMessage: ScalaTag =
    div(id := "boardMessage")(
      //getMessageDiv,
      p(style := "text-align:center;")("Please wait, the messages are loading !"),
    )

  // A line in the chatroom.
  def getMessageDiv: ScalaTag =
    div(`class` := "msg")(
      span(`class` := "author")(),
      span(`class` := "msg-content")(),
    )

  // Form to submit message or login.
  def getForm(text: String,
              submitPath: String,
              placeholderText: String,
              errorMsg: String = null): ScalaTag =
    val divError: Seq[ScalaTag] =
      if errorMsg != null then
        Seq(div(id := "errorDiv", `class` := "errorMsg",errorMsg))
      else Seq()

    val content: Seq[ScalaTag] =
      Seq(
        label(`for` := "messageInput")(text),
        input(name := "text", id := "messageInput", `type` := "text", placeholder := placeholderText),
        input(`type` := "submit"),
      )

    form(id := "msgForm", action := submitPath, method := "post")(
       divError ++ content
    )

end Layouts
