package Chat

enum Token:
  case // Terms
  // Part 2 Step 1
  BONJOUR,
  SVP,
  JE,
  // Etat
  ASSOIFFE,
  AFFAME,
  // Actions
  ETRE,
  VOULOIR,
  COMMANDER,
  CONNAITRE,
  COUTER,
  // Logic Operators
  ET,
  OU,
  // Products
  PRODUIT,
  MARQUE,
  // Util
  PSEUDO,
  NUM,
  SOLDE,
  PRIX,
  QUEL,
  COMBIEN,
  STOPWORD,
  EOL,
  UNKNOWN,
  BAD
end Token
