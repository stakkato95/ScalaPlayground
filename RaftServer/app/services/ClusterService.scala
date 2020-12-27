package services

import javax.inject.Inject
import models.Person

class ClusterService @Inject()() {

  def showPerson(person: Person): Unit = {
    println(s"success $person")
  }
}
