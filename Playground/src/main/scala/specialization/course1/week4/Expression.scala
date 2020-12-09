package specialization.course1.week4

trait Expr {
  def eval: Int
}

case class Var(n: Int) extends Expr {
  override def eval: Int = n
}

case class Sum(a: Expr, b: Expr) extends Expr {
  override def eval: Int = a.eval + b.eval
}

case class Mult(a: Expr, b: Expr) extends Expr {
  override def eval: Int = a.eval * b.eval
}