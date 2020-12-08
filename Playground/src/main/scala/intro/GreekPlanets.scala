package intro

object GreekPlanets extends Enumeration {
  val Mars, Moon, Earth = Value
}

object Month extends Enumeration {

  class MonthValue(id: Int, val name: String, val ordinal: Int) extends Val(id: Int, name: String)

  val January = new MonthValue(-3, "JAN", 1)
  val February = new MonthValue(-2, "FEB", 2)
  val March = new MonthValue(-1, "MAR", 3)
}