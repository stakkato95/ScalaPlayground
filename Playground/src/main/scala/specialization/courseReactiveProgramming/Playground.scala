package specialization.courseReactiveProgramming

object Playground {
  def main(args: Array[String]): Unit = {
    val map = Map.empty[String, Int].withDefaultValue(-1) //makes "map()" call safe

    //both calls don't throw an exception
    println(map.get(""))
    println(map(""))
  }
}
