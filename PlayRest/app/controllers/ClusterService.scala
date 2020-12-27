package controllers

import javax.inject.Inject

class ClusterService @Inject()() {

  def showPerson(person: Person): Unit = {
    println(s"success $person")
  }
}
