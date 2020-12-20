package guide

import akka.actor.typed.{ActorSystem, Behavior, PostStop, PreRestart, Signal, SupervisorStrategy}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}

class SupervisingActor(context: ActorContext[String]) extends AbstractBehavior[String](context) {
  private val child = context.spawn(
    Behaviors.supervise(SupervisedActor()).onFailure(SupervisorStrategy.restart),
    "supervised-actor"
  )

  override def onMessage(msg: String): Behavior[String] = {
    msg match {
      case "stop" =>
        child ! "fail"
        this
      case "shutdown" =>
        Behaviors.stopped
    }
  }

  override def onSignal: PartialFunction[Signal, Behavior[String]] = {
    case PostStop =>
      println("supervisors stopped")
      this
  }
}

object SupervisingActor {
  def apply(): Behavior[String] = Behaviors.setup(new SupervisingActor(_))
}

class SupervisedActor(context: ActorContext[String]) extends AbstractBehavior[String](context) {
  println("supervised started")

  override def onMessage(msg: String): Behavior[String] = {
    msg match {
      case "fail" =>
        println("supervised fails now")
        throw new Exception("I failed!")
    }
  }

  override def onSignal: PartialFunction[Signal, Behavior[String]] = {
    case PreRestart =>
      println("supervised will be restarted")
      this
    case PostStop =>
      println("supervised stopped")
      this
  }
}

object SupervisedActor {
  def apply(): Behavior[String] = Behaviors.setup(new SupervisedActor(_))
}

object SupervisionExample extends App {
  val system = ActorSystem(SupervisingActor(), "supervisor")
  system ! "stop"
  system ! "shutdown"
}