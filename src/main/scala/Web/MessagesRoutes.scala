package Web

import Chat.{AnalyzerService, Parser, TokenizerService, UnexpectedTokenException}
import Data.{AccountService, MessageService, Session, SessionService}

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
      val messages = msgSvc.getLatestMessages(20)
      Layouts.homePage(session.getCurrentUser.isDefined, if messages.nonEmpty then messages else null)


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
        ujson.Obj(
          "success" -> success,
          "err" -> err
        )

      if session.getCurrentUser.isEmpty then
        jsonResponse(false, "No user is logged in")
      else if msg.str == "" then
        jsonResponse(false, "The message is empty")
      else if msg.str.startsWith("@bot ") then
        val message = msg.str.stripPrefix("@bot ")

        // Process message of user
        val id = msgSvc.add(sender = session.getCurrentUser.get, msg = Layouts.getMessageSpan(message))
        send20LastMessageToAll()

        // Process response from chatbot
        try
          val tokenized = tokenizerSvc.tokenize(message.toLowerCase)

          val parser = new Parser(tokenized)
          val expr = parser.parsePhrases()

          msgSvc.add(
            sender = "Bot-tender",
            msg = Layouts.getMessageSpan(analyzerSvc.reply(session)(expr)),
            mention = Option("@bot"),
            replyToId = Option(id)
          )
          send20LastMessageToAll()
          jsonResponse(true)
        catch
          case e: UnexpectedTokenException => jsonResponse(false, s"Invalid input. ${e.getMessage}")

      else
        msgSvc.add(sender = session.getCurrentUser.get, msg = Layouts.getMessageSpan(msg.str))
        send20LastMessageToAll()
        jsonResponse(true)

    def send20LastMessageToAll(): Unit =
      for (ws <- websockets) {
        ws.send(cask.Ws.Text(Layouts.generateMessageBoardContent(msgSvc.getLatestMessages(20)).foldLeft("")(_+_)))
      }

    // TODO - Part 3 Step 4c: Process and store the new websocket connection made to `/subscribe`

    @cask.websocket("/subscribe")
    def subscribe(): cask.WebsocketResult =
      cask.WsHandler { (channel: cask.endpoints.WsChannelActor) =>
        websockets.addOne(channel)
        cask.WsActor {
          case cask.Ws.Close(_,_) => websockets -= channel
        }
      }

    // TODO - Part 3 Step 4d: Delete the message history when a GET is made to `/clearHistory`

    @cask.get("/clearHistory")
    def clearHistory(): Unit =
      msgSvc.deleteHistory()
      for (ws <- websockets) {
        ws.send(cask.Ws.Text(""))
      }

    // TODO - Part 3 Step 5: Modify the code of step 4b to process the messages sent to the bot (message
    //      starts with `@bot `). This message and its reply from the bot will be added to the message
    //      store together.
    //
    //      The exceptions raised by the `Parser` will be treated as an error (same as in step 4b)


    initialize()
end MessagesRoutes
