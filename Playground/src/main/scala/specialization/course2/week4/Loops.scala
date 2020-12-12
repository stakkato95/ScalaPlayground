package specialization.course2.week4

import scala.annotation.tailrec

object Loops {
  def main(args: Array[String]): Unit = {
    loops()
  }

  def loops() = {
    var i = 0
    WHILE(i < 5) {
      println(i)
      i = i + 1
    }

    DO {
      println(i)
      i = i + 1
    }(i < 10)
  }

  @tailrec
  def WHILE(predicate: => Boolean)(action: => Unit): Unit = if (predicate) {
    action
    WHILE(predicate)(action)
  }

  def DO(action: => Unit)(predicate: => Boolean): Unit = {
    action
    if (predicate) {
      DO(action)(predicate)
    }
  }
}
