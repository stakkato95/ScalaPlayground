package specialization.course1.week2

import scala.annotation.tailrec

object SecondMain {
  def main(args: Array[String]): Unit = {
    //    higherOrderFunction()
    currying()
  }

  def higherOrderFunction() = {
    def sum(f: Int => Int, a: Int, b: Int): Int = {
      @tailrec
      def loop(a: Int, acc: Int): Int = {
        if (a > b) acc else loop(a + 1, f(a) + acc)
      }

      loop(a, 0)
    }

    println(sum(x => x * x, 3, 5))
  }

  def currying() = {
    def sum(f: Int => Int): (Int, Int) => Int = {
      def sumFunction(a: Int, b: Int): Int = {
        if (a > b) 0 else f(a) + sumFunction(a + 1, b)
      }

      sumFunction
    }

    //another way to write sum function
    def sumShorter(f: Int => Int)(a: Int, b: Int): Int = {
      if (a > b) 0 else f(a) + sumShorter(f)(a + 1, b)
    }

    val summarizer = sum(x => x + 1)
    println(summarizer(0, 3))
    println(sumShorter(x => x + 1)(0, 3))

    def product(f: Int => Int)(a: Int, b: Int): Int = {
      if (a > b) 1 else f(a) * product(f)(a + 1, b)
    }

    println(product(x => x * x)(3, 4))

    def factorial(n: Int) = product(x => x)(1, n)
    println(factorial(5))

    def reducer(f: Int => Int, combiner: (Int, Int) => Int, init: Int)(a: Int, b: Int): Int = {
      if (a > b) init else combiner(f(a), product(f)(a + 1, b))
    }

    def combinedFactorial(n: Int) = reducer(x => x, (a, b) => a * b, 1)(1, n)
    println(combinedFactorial(5))
  }
}
