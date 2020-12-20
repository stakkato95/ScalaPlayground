package guide.iot

import akka.actor.typed.ActorSystem

object IotApp {
  def main(args: Array[String]): Unit = {
    ActorSystem[Nothing](IotSupervisor(), "iot-system")
  }
}
