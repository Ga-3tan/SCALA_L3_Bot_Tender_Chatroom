package Web

import scalatags.Text.all._
import scalatags.Text.tags2

type ScalaTag = scalatags.Text.TypedTag[String]

/**
  * Assembles the method used to layout ScalaTags
  */
object Layouts:
  // You can use it to store your methods to generate ScalaTags.
  def page(): ScalaTag =
    html(
      getHeader,
      body(
        getNav,
        getContentDiv,
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

  def getContentDiv: ScalaTag =
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

end Layouts
