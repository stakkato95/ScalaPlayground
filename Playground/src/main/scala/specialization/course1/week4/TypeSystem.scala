package specialization.course1.week4

object TypeSystem {
  def main(args: Array[String]): Unit = {
    //Int, Double, ... - mapped to Java's int, double for interoperability
    //Functions are also objects, e.g. "Function1". Same concept like in Java with Interfaces
    //functions of a class are not objects, BUT if they are passed to another function,
    //then an object for this function is created automatically

    //S <: T - S is a subtype of T - COVARIANCE - T is a lover bound for this generic
    //S >: T - S is a supertype of T - S is an upper bound for this generic

    //Arrays in scala are NOT COVARIANT

    //isInstanceOf / asInstanceOf - bad way for testing / casting types

    showExpression()

    //Lists - recursive (linked list with lat element "Nil")
    //Arrays - flat
    val empty: List[Nothing] = List()
  }

  def show(expr: Expr): String = expr match {
    case Var(n) => n.toString
    case Sum(a, b) => "(" + show(a) + " + " + show(b) + ")"
    case Mult(a, b) => "(" + show(a) + " * " + show(b) + ")"
  }

  def showExpression() = {
    val expr = Sum(Var(1), Mult(Var(2), Var(4)))
    println(show(expr))
  }
}
