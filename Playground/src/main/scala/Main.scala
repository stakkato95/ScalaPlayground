object Main {
  def main(args: Array[String]) = {
//    assertions()
//    classes()
//    optional()
    companionObjects()
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
}
