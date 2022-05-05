package Chat

/**
  * This sealed trait represents a node of the tree.
  */
sealed trait ExprTree

/**
  * Declarations of the nodes' types.
  */
object ExprTree:
  // TODO - Part 2 Step 3
  // Example cases
  case class Thirsty() extends ExprTree
  case class Hungry() extends ExprTree
