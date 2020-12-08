package specialization.course1.week1

import scala.annotation.tailrec

object FirstMain {
  def main(args: Array[String]): Unit = {
    //    callByNameAndByValue()
    //    betterSqrt()
    tailRecursion()
  }

  def callByNameAndByValue() = {
    //usually call-by-value

    //call-by-name is forced with "=>" - safe against loops
    def constOne(x: Int, y: => Int) = 1

    //call-by-value - unsafe against loops
    def constTwo(x: Int, y: Int) = 1

    def loop: Int = loop

    val result = constOne(1 + 2, loop)
    //val result1 = constTwo(1 + 2, loop) //infinite loop
    println(result)

    //def is calculated at runtime
    //val is calculated at compile time
    def square = (x: Int) => x * x

    val n = 2
    val sq = square(2) //sq == 4 at compile time
  }

  def betterSqrt() = {
    def sqrt(x: Double) = {
      //hide implementation of a function in the function block

      def abs(x: Double) = if (x < 0) -x else x

      def sqrtIter(guess: Double): Double =
        if (isGoodEnough(guess)) guess
        else sqrtIter(improve(guess))

      def isGoodEnough(guess: Double) =
        abs(guess * guess - x) / x < 0.001

      def improve(guess: Double) = (guess + x / guess) / 2

      sqrtIter(1.0)
    }


    println(sqrt(1e60))
  }

  def tailRecursion() = {
    //Tail recursion - if a function calls itself as a lust action,
    //then the stackframe of this function can be reused.
    //It means this recursion describes a simple iterative process
    //(i.e. functional form of a loop). One stack frame is enough

    //example of tail recursion
    //function can be marked as "tailrec"
    @tailrec
    def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)

    //example of NON tail recursion. deep recursion call.
    //function CAN NOT be marked as "tailrec"
    //@tailrec
    def factorial(n: Int): Int = if (n == 0) 1 else n * factorial(n - 1)

    def factorialTeilrec(n: Int): Int = {
      @tailrec
      def factorial(n: Int, total: Int): Int = {
        if (n == 0) total else factorial(n - 1, total * n)
      }

      val initialTotal = 1
      factorial(n, initialTotal)
    }

    println(factorialTeilrec(5))
  }
}
