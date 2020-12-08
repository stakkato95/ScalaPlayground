package intro

abstract class Monoid[A] {
  def add(x: A, y: A): A
  def unit: A
}
