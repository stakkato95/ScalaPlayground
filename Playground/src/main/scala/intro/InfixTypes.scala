package intro

case class Employee(name: String) {
  def supports(anotherEmployee: Employee) = new Supports(this, anotherEmployee)
}

class Supports[A, B](val supportProvider: A, val supportReceiver: B)