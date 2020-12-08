package specialization.course1.week2

class Rational(x: Int, y: Int) {
  def num: Int = x

  def denom: Int = y

  //instead of "def neg"
  def unary_- : Rational = new Rational(-num, denom)

  //instead of "def subtract(that: Rational)"
  def -(that: Rational): Rational = {
    new Rational(
      num * that.denom - that.num * denom,
      denom * that.denom
    )
  }
}
