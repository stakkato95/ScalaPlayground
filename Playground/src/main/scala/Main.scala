import scala.collection.mutable.{Map => MutableMap}

object Main {
  implicit val stringMonoid: Monoid[String] = new Monoid[String] {
    override def add(x: String, y: String): String = x concat y

    override def unit: String = ""
  }

  implicit val intMonoid: Monoid[Int] = new Monoid[Int] {
    override def add(x: Int, y: Int): Int = x + y

    override def unit: Int = 0
  }

  def main(args: Array[String]) = {
    //    assertions()
    //    classes()
    //    optional()
    //    companionObjects()
    //    tuples()
    //    lambdas()
    //    closures()
    //    lists()
    //    maps()
    //    sets()
    //    patternMatching()
    //    caseClasses()
    //    ranges()
    //    partiallyAppliedFunAndCurrying()
    //    partialFunction()
    //    implicits()
  }

  def assertions() = {
    //1 asserts
    val left = 1
    val right = 1
    assert(left == right)
  }

  def classes() = {
    //    //2 classes
    //    val pt = new Point(1, 2)
    //    println(pt)
  }

  def optional() = {
    //3 option
    val helloWorld = mayBeValue(true)
    println(helloWorld.get)
    val default = mayBeValue(false).getOrElse("default")
    println(default)
    //pattern matching
    val unknownString: Option[String] = mayBeValue(false)
    val result = unknownString match {
      case Some(str) => str
      case None => "anotherDefault"
    }
    println(result)

    //map optional
    val number = Some(3)
    val noNumber: Option[Int] = None
    println(number.map(_ * 1.5).get)
    println(noNumber.map(_ * 1.5).get)

    //unfold an optional
    val valueIfEmpty = 1.0
    println(number.fold(valueIfEmpty)(_ * 1.5))
    println(noNumber.fold(valueIfEmpty)(_ * 1.5))
  }

  def mayBeValue(flag: Boolean): Option[String] = {
    if (flag) Some("hello world") else None
  }

  def companionObjects() = {
    println(Movie.getMovie(1999).get)
    println(Movie.getMovie(1998).getOrElse(new Movie("default", 2000)))
  }

  def tuples() = {
    val t1 = Tuple2("hello", 1)
    val t2 = ("hello", "world", 2)
    println(t1._1)
    println(t2._3)

    val (hello, world, two) = t2
    println(hello + " " + world + " " + two)
  }

  def lambdas() = {
    def lambda = { x: Int => x + 1 }

    println(lambda(4))

    def lambda2 = (x: Int) => x + 2

    println(lambda2(4))

    def lambda3 = new Function1[Int, Int] {
      override def apply(v1: Int): Int = v1 + 3
    }

    println(lambda3(4))

    def lambda4 = new (Int => Int) {
      override def apply(v1: Int): Int = v1 + 4
    }

    println(lambda4.apply(4))
    println("is function taking one arg and returning one arg " + lambda4.isInstanceOf[Function1[_, _]])
    println("is function taking one arg and returning one arg " + lambda4.isInstanceOf[(_) => _])
  }

  def closures() = {
    var number = 1

    def closure = (x: Int, y: Int) => x + y + number

    println(closure(1, 2))

    number = 5
    println(closure(1, 2))

    def addFive = (x: Int) => x + 10

    def funReturningAnotherFun(x: Int, f: Int => Int) = f(x)

    val resultOfAddingFiveAndTen = funReturningAnotherFun(5, addFive)
    println(resultOfAddingFiveAndTen)

    val list = List("HELLO", "WoRlD", "ExClAmAtIoN")

    def listProcessor(ls: List[String], sideEffect: String => String) = ls map sideEffect

    println(listProcessor(list, _.toLowerCase))
  }

  def lists() = {
    val a = List(1, 2, 3, 4, 5, 6)
    val b = List(1, 2, 3, 4, 5, 6)
    //content is compared
    println(a eq b)
    println(a == b)

    val stringList: List[String] = Nil
    val intList: List[Int] = Nil
    //lists of different types are equal
    println(stringList == intList)

    println(stringList.headOption.getOrElse("empty list"))

    try {
      println(stringList.head)
    } catch {
      case e: NoSuchElementException => {
        println("failed: " + e.getLocalizedMessage)
      }
    }

    println(a(1))

    //filtering elements
    val c = a
      .filter(_ % 2 == 0)
      .map(_.toString * 2)
      .reverse
      .map(_.toInt * 2)

    println(c)

    //concat arrays
    val rangeList = (1 to 5).toList
    println(rangeList ::: rangeList)
    //concat arrays
    println(0 :: rangeList)
  }

  def maps() = {
    //this is a TUPLE!!!
    val someTuples = (1 -> "hello", 2 -> "world", 3 -> "!!!", 4 -> "hello100500")
    println(someTuples.getClass.getTypeName)
    //but this is a MAP!!!
    val someMap = Map(1 -> "hello", 2 -> "world", 3 -> "!!!")
    println(someMap.getClass.getTypeName)

    println(someMap.size)
    println(someMap.contains(0))

    val anotherMap = someMap + (0 -> "start")
    println(anotherMap.contains(0))
    println(anotherMap.head)

    //different ways to get
    println(anotherMap(0))
    println("simply not found: " + anotherMap.get(10))
    println("not found and default: " + anotherMap.getOrElse(10, "hi!"))
    try {
      println(anotherMap(10))
    } catch {
      case e: NoSuchElementException => {
        println("not found and error message: " + e.getMessage)
      }
    }

    //remove elements
    val reducedMap = anotherMap - 0
    println(reducedMap)
    val veryReducedMap = reducedMap -- List(2, 3)
    println(veryReducedMap)
  }

  def sets() = {
    val setOne = Set("hello", "world", "!!!")
    val setTwo = Set("100500hello", "why", "world")

    val setUnion = setOne union setTwo
    println(setUnion == (setOne | setTwo))
    println(setUnion)

    val setDiff = setOne diff setTwo
    println(setDiff)
  }

  def patternMatching() = {
    val x = "world"
    val result = x match {
      case "hello" => "one"
      case "world" => "two"
      case "!!!" => "three"
      case _ => "four"
    }
    println(result)

    val y = List("strange", "man")
    val resultList = y match {
      case List("normal", "list") => List("hello", 1)
      case List("strange", _) => List("world", 2)
      case List("regular", "list") => List("!!!", 3)
    }
    println(resultList)

    val z = List("strange", "man")
    val resultList1 = y match {
      case List("normal", "list") => List("hello", 1)
      case List("strange", placeholder) => List("world", 2, placeholder)
      case List("regular", "list") => List("!!!", 3)
    }
    println(resultList1)

    //pattern matching in list
    val secondElement = List(1, 2) match {
      case head :: tail => tail.headOption.getOrElse(-1)
      case _ => 0
    }
    println(secondElement)

    {
      val secondElement = List(1, 2, 3) match {
        case head :: second :: tail => second
        case _ => 0
      }
      println(secondElement)
    }
  }

  def caseClasses() = {
    //case class is similar to data class in Kotlin
    //out of the box:
    //1 hashcode
    //2 equals
    //3 toString

    val x = Var("x")
    val x1 = Var("x")

    println(x == x1)
    println(x)

    def isIdentityFun(term: Term): Boolean = term match {
      case Fun(x, Var(y)) if x == y => true
      case _ => false
    }

    val fun = Fun("x", Var("x"))
    println(fun)
    println(isIdentityFun(fun))
    println(isIdentityFun(Var("x")))

    val robot = Robot("T100", "Terminator")
    //mutable "var" property
    robot.name = "Arnold"
  }

  def ranges() = {
    val start = 0
    val end = 10
    val rng = start to end
    val rngUntil = start until end
    println(rng)
    println(rng.size)
    println(rngUntil.size)

    val rngClassic = Range(0, 10, 2).inclusive
    println(rngClassic)
  }

  def partiallyAppliedFunAndCurrying() = {
    //partially applied function
    def sum = (a: Int, b: Int, c: Int) => a + b + c

    val onePlusFivePlus = sum(1, 5, _: Int)
    println(onePlusFivePlus(10))

    //currying - inverse of partially applied function
    def multiply(a: Int, b: Int) = a * b

    def multiplyAnother = (a: Int, b: Int) => a * b

    val multiplyCurried = (multiply _).curried
    println(multiplyCurried(3)(4))
  }

  def partialFunction() = {
    //NOT partially applied function!!!
    //    val doubleEvens: PartialFunction[Int, Int] = new PartialFunction[Int, Int] {
    //      override def isDefinedAt(x: Int): Boolean = x % 2 == 0
    //
    //      override def apply(v1: Int): Int = v1 * 2
    //    }
    //
    //    val tripleOdds: PartialFunction[Int, Int] = new PartialFunction[Int, Int] {
    //      override def isDefinedAt(x: Int): Boolean = x % 2 == 1
    //
    //      override def apply(v1: Int): Int = v1 * 3
    //    }
    //
    //    val whatToDo = doubleEvens orElse tripleOdds
    //    println("3 is a tripled odd: " + whatToDo(3))
    //    println("4 is a a doubled even: " + whatToDo(4))

    //case statement as a quick way to create a partial function
    val doubleEvens: PartialFunction[Int, Int] = {
      case x if (x % 2) == 0 => x * 2
    }
    val tripleOdds: PartialFunction[Int, Int] = {
      case x if (x % 2) == 1 => x * 3
    }
    val whatToDo = doubleEvens orElse tripleOdds
    println("3 is a tripled odd: " + whatToDo(3))
    println("4 is a a doubled even: " + whatToDo(4))

    val addFive = (x: Int) => x + 5
    val newToDo = doubleEvens orElse tripleOdds andThen addFive

    println(newToDo(3))
  }

  def implicits() = {
    def sum[A](xs: List[A])(implicit m: Monoid[A]): A = {
      if (xs.isEmpty) m.unit
      else m.add(xs.head, sum(xs.tail))
    }

    println(sum(List(1, 2, 3)))
    println(sum(List("one", "two", "three")))
  }
}
