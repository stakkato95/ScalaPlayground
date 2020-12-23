package guide.iot

import akka.actor.typed.{ActorRef, Behavior, Signal}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors, TimerScheduler}
import guide.iot.DeviceGroupQuery.{CollectionTimeout, Command, DeviceTerminated, WrappedRespondTemperature}
import guide.iot.DeviceManager.{DeviceNotAvailable, DeviceTimedOut, RespondAllTemperatures, Temperature, TemperatureNotAvailable, TemperatureReading}

import scala.concurrent.duration.FiniteDuration

object DeviceGroupQuery {

  def apply(deviceIdToActor: Map[String, ActorRef[Device.Command]],
            requestId: Long,
            requester: ActorRef[DeviceManager.RespondAllTemperatures],
            timeout: FiniteDuration): Behavior[Command] = {
    Behaviors.setup { context =>
      Behaviors.withTimers { timers =>
        new DeviceGroupQuery(deviceIdToActor, requestId, requester, timeout, context, timers)
      }
    }
  }

  trait Command

  private case object CollectionTimeout extends Command

  //wrap Device msg into a message, that is extended from DeviceGroupQuery.Command
  final case class WrappedRespondTemperature(response: Device.RespondTemperature) extends Command

  private final case class DeviceTerminated(deviceId: String) extends Command

  val DEFAULT_TEMPERATURE_REQUEST_ID = 0
}

class DeviceGroupQuery(deviceIdToActor: Map[String, ActorRef[Device.Command]],
                       requestId: Long,
                       requester: ActorRef[DeviceManager.RespondAllTemperatures],
                       timeout: FiniteDuration,
                       context: ActorContext[Command],
                       timers: TimerScheduler[Command]) extends AbstractBehavior[Command](context) {

  private var replies = Map.empty[String, TemperatureReading]
  private var stillWaiting = deviceIdToActor.keySet

  timers.startSingleTimer(CollectionTimeout, CollectionTimeout, timeout)

  private val respondTemperatureAdapter = context.messageAdapter(WrappedRespondTemperature.apply)

  deviceIdToActor.foreach {
    case (deviceId, device) =>
      context.watchWith(device, DeviceTerminated(deviceId))
      device ! Device.ReadTemperature(DeviceGroupQuery.DEFAULT_TEMPERATURE_REQUEST_ID, respondTemperatureAdapter)
  }

  override def onMessage(msg: Command): Behavior[Command] = {
    msg match {
      case WrappedRespondTemperature(response) => onRespondTemperature(response)
      case DeviceTerminated(deviceId) => onDeviceTerminated(deviceId)
      case CollectionTimeout => onCollectionTimout()
    }
  }

  override def onSignal: PartialFunction[Signal, Behavior[Command]] = super.onSignal

  private def onRespondTemperature(temperature: Device.RespondTemperature): Behavior[Command] = {
    val reading = temperature.value match {
      case Some(value) => Temperature(value)
      case None => TemperatureNotAvailable
    }

    val deviceId = temperature.deviceId
    stillWaiting -= deviceId
    replies += deviceId -> reading

    respondWhenAllCollected()
  }

  private def onDeviceTerminated(deviceId: String): Behavior[Command] = {
    if (stillWaiting(deviceId)) {
      stillWaiting -= deviceId
      replies += deviceId -> DeviceNotAvailable
    }

    respondWhenAllCollected()
  }

  private def onCollectionTimout(): Behavior[Command] = {
    replies ++= stillWaiting.map(deviceId => deviceId -> DeviceTimedOut)
    stillWaiting = Set.empty
    respondWhenAllCollected()
  }

  private def respondWhenAllCollected(): Behavior[Command] = {
    if (stillWaiting.isEmpty) {
      requester ! RespondAllTemperatures(requestId, replies)
    }
    this
  }
}