package specialization.course2.week1

object ForLoop {
  def main(args: Array[String]): Unit = {
    forIsLikeSql()
    monads()
  }

  def forIsLikeSql() = {
    //for can be formed like an SQL query
    val books = List(
      Book("1", List("ac", "b")),
      Book("2", List("ac", "d")),
      Book("3", List("e", "f")),
      Book("4", List("g", "ah"))
    )

    //find books where author starts with "a"
    val filteredAuthors = for (b <- books; a <- b.authors if a.startsWith("a")) yield b.title
    println(filteredAuthors)

    //find authors who have written at least 2 books
    val withTwoBooks = for {
      //creates permutation of all books
      //(A, B) and (B, A) are therefore different pairs
      b1 <- books
      b2 <- books
      if b1 != b2
      a1 <- b1.authors
      a2 <- b2.authors
      if a1 == a2
    } yield a1
    println(withTwoBooks)

    val withTwoBooksNoDuplicates = for {
      b1 <- books
      b2 <- books
      //lexicographical order prevents duplicates
      if b1.title < b2.title
      a1 <- b1.authors
      a2 <- b2.authors
      if a1 == a2
    } yield a1
    println(withTwoBooksNoDuplicates)

    //solution by design: instead of "List" use "Set"

    //for loops are often more convenient then a chain of map, filter, etc

    //filter by author with higher order functions
    println(authorFilter(books, "a"))
  }

  def mapFun[T, U](xs: List[T], f: T => U): List[U] = {
    for (x <- xs) yield f(x)
  }

  def filter[T](xs: List[T], f: T => Boolean): List[T] = {
    for (x <- xs if f(x)) yield x
  }

  def authorFilter(books: List[Book], author: String): List[String] =
    books
      .filter(b =>
        b.authors.exists(_.startsWith(author))
      )
      .map(_.title)

  def monads() = {
    //monad has "unit()" and "bind()". in scala "bind()" is called "flatMap()"
    //List, Set, Option - are monads. "unit()" for each of this monads is different

    //monads exist to "inline nested for expressions"

    //Try - for passing errors between threads or
    //by returning a value instead of passing error in call stack
  }
}
