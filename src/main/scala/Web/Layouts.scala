package Web

import scalatags.Text.all.*
import scalatags.Text.tags2

/**
 * Assembles the method used to layout ScalaTags
 */
object Layouts:
    def homePage(): scalatags.Text.TypedTag[String] = {
      html(
        head(
          link(rel:="stylesheet", href:="/static/resources/css/main.css"),
          script(src:="/static/resources/js/main.js")
        ),
        body(
          tag("nav")(
            a(cls := "nav-brand", "Bot-tender"),
            div(cls := "nav-item",
              a("Log in")
            )
          ),
          div(cls := "content",
            div(id := "boardMessage",
              div(cls := "msg",
                tag("center")("Please wait, the messages are loading !"),
                span(cls := "author", "TestAuthor"),
                span(cls := "msg-content", "Hello world !")
              )
            ),
            form(id := "msgForm", onsubmit:="submitMessageForm(); return false",
              div(id := "errorDiv", cls:="errorMsg", "Test error"),
              label(`for`:="messageInput", "Your message"),
              input(id := "messageInput", `type`:="text", placeholder:="Write your message"),
              input(`type`:="submit")
            )
          )
        )
      )
    }
end Layouts
