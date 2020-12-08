package specialization.course1.week3

//import all
//import scala.collection.mutable._

//single class
//import scala.collection.mutable.Map

//certain classes
//import scala.collection.mutable.{Map List}

//ScalaObject is an alias for java.lang.Object

object ClassesAndPackages {
  def main(args: Array[String]): Unit = {
    //small "null is only for REF types"
    //big NULL is a TYPE!!! and small null is representation of it!
    val x: Null = null
    val y: String = null

    //"List" in Scala is a linked list

    //val - evaluated at declaration
    //def - evaluated each time it is referenced
  }

  //method returns nothing and throws an exception
  def error(msg: String): Nothing = throw new Error(msg)
}