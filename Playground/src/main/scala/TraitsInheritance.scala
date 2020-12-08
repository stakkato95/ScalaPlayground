trait Animal {
  def isHungry(): Boolean

  def isNotHungry() = !isHungry()
}

trait Trainable {
  def makeTrick(): String
}

class TrainedCat(hungry: Boolean, trick: String) extends Animal with Trainable {
  override def isHungry(): Boolean = hungry

  override def makeTrick(): String = trick
}