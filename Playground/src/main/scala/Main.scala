object Main {
  def main(args: Array[String]) = {
    //    assertions()
    //    classes()
    //    optional()
    //    companionObjects()
    //    tuples()
    //    lambdas()
    closures()
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
}
