package Chat

class UnexpectedTokenException(msg: String) extends Exception(msg) {}

// step 4
class Parser(tokenized: Tokenized):

  import ExprTree._
  import Chat.Token._

  // Start the process by reading the first token.
  var curTuple: (String, Token) = tokenized.nextToken()

  def curValue: String = curTuple._1

  def curToken: Token = curTuple._2

  /** Reads the next token and assigns it into the global variable curTuple */
  /** When meeting a STOPWORD Token, ignore it and skip to the next token   */
  def readToken(): Unit =
    val token = tokenized.nextToken()
    if token._2 == Token.STOPWORD then readToken()
    else curTuple = token

  /** "Eats" the expected token and returns it value, or terminates with an error. */
  private def eat(token: Token): String =
    if token == curToken then
      val tmp = curValue
      readToken()
      tmp
    else expected(token)

  /** Complains that what was found was not expected. The method accepts arbitrarily many arguments of type Token */
  private def expected(token: Token, more: Token*): Nothing =
    val expectedTokens = more.prepended(token).mkString(" or ")
    throw new UnexpectedTokenException(s"Expected: $expectedTokens, found: $curToken")

  /** the root method of the parser: parses an entry phrase */
  // Part 2 Step 4
  def parsePhrases() : ExprTree = {
    if curToken == BONJOUR then readToken()

    if curToken == JE then
      readToken()
      if curToken == ETRE then
        readToken()
        parseETRE()
      else if curToken == VOULOIR then
        readToken()
        parseVOULOIR()
      else expected(ETRE, VOULOIR)
    else if curToken == QUEL then
      readToken()
      eat(ETRE)
      eat(PRIX)
      RequestPrice(handleProductRequest())
    else if curToken == COMBIEN then
      readToken()
      eat(COUTER)
      RequestPrice(handleProductRequest())
    else expected(JE, QUEL, COMBIEN)
  }

  def parseETRE(): ExprTree = {
    if curToken == ASSOIFFE then
      readToken()
      Thirsty()
    else if curToken == AFFAME then
      readToken()
      Hungry()
    else if curToken == PSEUDO then
      Identify(curValue)
    else expected(ASSOIFFE, AFFAME, PSEUDO)
  }

  def parseVOULOIR(): ExprTree = {
    if curToken == CONNAITRE then
      readToken()
      if curToken == SOLDE then
        readToken()
        RequestBalance()
      else if curToken == PRIX then
        readToken()
        RequestPrice(handleProductRequest())
      else if curToken == QUEL then
        readToken()
        eat(ETRE)
        eat(PRIX)
        RequestPrice(handleProductRequest())
      else if curToken == COMBIEN then
        readToken()
        eat(COUTER)
        RequestPrice(handleProductRequest())
      else expected(SOLDE, QUEL, COMBIEN)
    else if curToken == COMMANDER then
      readToken()
      RequestOrder(handleProductRequest())
    else if curToken == SOLDE then
      readToken()
      RequestBalance()
    else if curToken == NUM then // Pas de readToken après !! C'est intentionnel !
      RequestOrder(handleProductRequest())
    else expected(CONNAITRE, COMMANDER)
  }

  def parseProduct(): ExprTree = {
    val quantity = eat(NUM).toInt
    val product: String = if curToken == PRODUIT then eat(PRODUIT) else null
    val brand: String = if curToken == MARQUE then eat(MARQUE) else null

    if brand == null && product == null then expected(PRODUIT, MARQUE)

    Order(quantity, product, brand)
  }

  def handleProductRequest(tLeft: ExprTree = null): ExprTree = {
    val tRight: ExprTree =
      if tLeft == null then parseProduct()
      else
        if curToken == ET then
          readToken()
          And(tLeft, parseProduct())
        else if curToken == OU then
          readToken()
          Or(tLeft, parseProduct())
        else expected(ET, OU)

    if curToken == ET || curToken == OU then // Pas de readToken après !! C'est intentionnel !
      handleProductRequest(tRight)
    else tRight
  }
