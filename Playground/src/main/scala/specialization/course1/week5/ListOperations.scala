package specialization.course1.week5

object ListOperations {
  def main(args: Array[String]): Unit = {
    //    extendedOperations()
    //    pairs()
    listOperations()
  }

  def extendedOperations() = {
    val list = 0 until 10
    val updatedList = list.updated(9, 100500).toList
    println(updatedList)

    val element = last(updatedList)
    println(element)

    val allExceptLast = init(updatedList)
    println(allExceptLast)

    val reversed = reverse(updatedList)
    println(reversed)

    val removed = removeAt(1, List('a', 'b', 'c', 'd'))
    println(removed)
  }

  //implementation of "last" in scala stdlib
  //shows that "List" is a linked list
  def last[T](xs: List[T]): T = xs match {
    case List() => throw new Error("last of an empty list")
    case List(x) => x
    case y :: ys => last(ys)
  }

  //implementation of "init" in scala stdlib
  def init[T](xs: List[T]): List[T] = xs match {
    case List() => throw new Error("init of an empty list")
    case List(x) => List()
    case y :: ys => y :: init(ys)
  }

  //implementation of "reverse" in scala stdlib
  //bad implementation
  def reverse[T](xs: List[T]): List[T] = xs match {
    case List() => throw new Error("reverse of an empty list")
    case List(x) => xs
    case y :: ys => reverse(ys) ::: List(y)
  }

  def removeAt[T](n: Int, xs: List[T]): List[T] = {
    if (n > xs.length) {
      return xs
    }
    xs.take(n) ::: xs.drop(n + 1)
  }

  def pairs() = {
    val pair = (42, "world")
    val (fortyTwo, hello) = pair
    println(fortyTwo, hello)

    val result = (fortyTwo, hello) match {
      case (n, "hello") => -n
      case (n, "world") => n
    }
    println(result)
  }

  def listOperations() = {
    val list = List(2, -4, 5, 7, 1)

    //filter + filterNot in one go
    println(list.partition(_ > 0))

    //takeWhile + dropWhile in one go
    println(list.span(_ > 0))

    //create lists of consecutive duplicates
    val dups = List("a", "a", "a", "a", "b", "b", "c", "c", "c", "c")
    println(pack(dups))
    println(packEncoder(dups))
  }


  def pack[T](xs: List[T]): List[List[T]] = xs match {
    case Nil => Nil
    case x :: xs1 =>
      val (first, rest) = xs.span(_ == x)
      first :: pack(rest)
  }

  def packEncoder[T](xs: List[T]): List[(T, Int)] = xs match {
    case Nil => Nil
    case x :: _ =>
      val (first, rest) = xs.span(_ == x)
      (x, first.length) :: packEncoder(rest)
  }
}
