package Web

import Chat.{AnalyzerService, TokenizerService}
import Data.{MessageService, AccountService, SessionService, Session}
import scala.collection.mutable
import castor.Context.Simple.global

/**
  * Assembles the routes dealing with the message board:
  * - One route to display the home page
  * - One route to send the new messages as JSON
  * - One route to subscribe with websocket to new messages
  *
  * @param log
  */
class MessagesRoutes(tokenizerSvc: TokenizerService,
                     analyzerSvc: AnalyzerService,
                     msgSvc: MessageService,
                     accountSvc: AccountService,
                     sessionSvc: SessionService)(implicit val log: cask.Logger) extends cask.Routes:
    import Decorators.getSession

    val websockets: mutable.ListBuffer[cask.WsChannelActor] = mutable.ListBuffer()

    @getSession(sessionSvc) // This decorator fills the `(session: Session)` part of the `index` method.
    @cask.get("/")
    def index()(session: Session): ScalaTag =
      val ltsMsg = msgSvc.getLatestMessages(20)
      val boardContent = ltsMsg.map(m => Layouts.getMessageDiv(m._1, m._2))
      Layouts.homePage(session.getCurrentUser.isDefined, boardContent)


    // TODO - Part 3 Step 4b: Process the new messages sent as JSON object to `/send`. The JSON looks
    //      like this: `{ "msg" : "The content of the message" }`.
    //
    //      A JSON object is returned. If an error occurred, it looks like this:
    //      `{ "success" : false, "err" : "An error message that will be displayed" }`.
    //      Otherwise (no error), it looks like this:
    //      `{ "success" : true, "err" : "" }`
    //
    //      The following are treated as error:
    //      - No user is logged in
    //      - The message is empty
    //
    //      If no error occurred, every other user is notified with the last 20 messages
    @getSession(sessionSvc) // This decorator fills the `(session: Session)` part of the `index` method.
    @cask.postJson("/send")
    def processMsg(msg: ujson.Value)(session: Session): ujson.Obj =
      def jsonResponse(success: Boolean, err: String = ""): ujson.Obj =
        ujson.Obj("success" -> success, "err" -> err)

      if session.getCurrentUser.isEmpty then
        jsonResponse(false, "No user is logged in")
      else if msg.str == "" then
        jsonResponse(false, "The message is empty")
      else
        msgSvc.add(sender = session.getCurrentUser.get, msg = Layouts.getMessageSpan(msg.str))
        val ltsMsg = msgSvc.getLatestMessages(20)
        val boardContent = ltsMsg.map(m => Layouts.getMessageDiv(m._1, m._2))
        val result = boardContent.foldLeft("")((content, m) => content + m)
        websockets.foreach(ws => {
          println(s"sending $result to: ${ws.toString}") // TODO to remove
          ws.send(cask.Ws.Text(result))
        })
        jsonResponse(true)

    // TODO - Part 3 Step 4c: Process and store the new websocket connection made to `/subscribe`
    @cask.websocket("/subscribe")
    def subscribe(): cask.WebsocketResult =
      cask.WsHandler { channel =>
        websockets.addOne(channel)
        val index = websockets.size-1
        println(s"added new channel: ${channel.toString}") // TODO to remove
        cask.WsActor {
          case cask.Ws.Close(_,_) => websockets.remove(index)
        }
      }

    // TODO - Part 3 Step 4d: Delete the message history when a GET is made to `/clearHistory`

    @cask.get("/clearHistory")
    def clearHistory(): Unit =
      msgSvc.deleteHistory()
      websockets.foreach(ws => ws.send(cask.Ws.Text("")))

    // TODO - Part 3 Step 5: Modify the code of step 4b to process the messages sent to the bot (message
    //      starts with `@bot `). This message and its reply from the bot will be added to the message
    //      store together.
    //
    //      The exceptions raised by the `Parser` will be treated as an error (same as in step 4b)



    initialize()
end MessagesRoutes
