package specialization.course1.week6

object OtherCollections {
  def main(args: Array[String]): Unit = {
    //    vectors()
    //    otherOperations()
    maps()
  }

  def vectors() = {
    //Vectors up to 32 elements are arrays
    //1 if an is array already 32 elements long,
    //then an array of 32 pointers on 32 arrays is created
    //2 if this array is exhausted, then another level of indirection is introduced

    val nums = Vector(1, 2, 3)
    val newNums = 0 +: nums
    val updatedNums = newNums :+ 4
    println(updatedNums)
  }

  def otherOperations() = {
    //"Seq" is a base class for "List" and "Vector"
    //"Iterable" is a base class for "Seq", "Set" and "Map"
    //"Array" and "String" is also an "Iterable"
    val s = "Hello World"
    //any uppercase letter in th string
    println(s.exists(_.isUpper))

    println(s.forall(_.isUpper))
    println("product: \"" + s.product + "\"")
  }

  def scalarProduct(xs: Vector[Double], ys: Vector[Double]): Double =
    (xs zip ys).map(xy => xy._1 * xy._2).sum

  def scalarProduct2(xs: Vector[Double], ys: Vector[Double]): Double =
    (xs zip ys).map { case (x, y) => x * y }.sum

  def maps() = {
    //map is a function type of "KeyType => ValueType"

    //"Option" is a trait, "Some" and "None"
    //are implementations of this trait
  }
}
