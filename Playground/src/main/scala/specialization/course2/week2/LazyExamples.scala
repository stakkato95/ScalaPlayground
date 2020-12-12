package specialization.course2.week2

object LazyExamples {
  def main(args: Array[String]): Unit = {
    //    streams()
    lazyComputation()
  }

  def streams() = {
    //tail of a Stream is evaluated on demand
    //Stream is replaced with LazyList

    val initiallyLazy = LazyList(1, 2, 3)
    val castedLazy = List(1, 2, 3).to(LazyList)
  }

  def lazyComputation() = {
    //val - calculated at declaration, result reused
    //def - calculated each time it's called
    //lazy - calculated at first call, result reused

    val naturalNumbers = from(1).take(100).toList
    println(naturalNumbers)
  }

  def from(n: Int): LazyList[Int] = n #:: from(n + 1)
}
