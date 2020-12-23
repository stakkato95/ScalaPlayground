package guide.iot

import java.util.concurrent.TimeUnit

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior, PostStop, Signal}
import guide.iot.DeviceGroup.DeviceTerminated
import guide.iot.DeviceManager.{DeviceRegistered, ReplyDeviceList, RequestAllTemperatures, RequestDeviceList, RequestTrackDevice}

import scala.concurrent.duration.FiniteDuration

object DeviceGroup {

  def apply(groupId: String): Behavior[DeviceGroup.Command] = Behaviors.setup(new DeviceGroup(_, groupId))

  trait Command

  private final case class DeviceTerminated(device: ActorRef[Device.Command], groupId: String, deviceId: String) extends Command

}

class DeviceGroup(context: ActorContext[DeviceGroup.Command], groupId: String) extends AbstractBehavior[DeviceGroup.Command](context) {

  private var deviceIdToActor = Map.empty[String, ActorRef[Device.Command]]

  context.log.info("DeviceGroup {} started", groupId)

  override def onMessage(msg: DeviceGroup.Command): Behavior[DeviceGroup.Command] = {
    msg match {
      //`groupId` - pattern matching for "groupId" passed to constructor
      case trackMsg@RequestTrackDevice(`groupId`, deviceId, replyTo) =>
        deviceIdToActor.get(deviceId) match {
          case Some(deviceActor) =>
            replyTo ! DeviceRegistered(deviceActor)
          case None =>
            val deviceActor = context.spawn(Device(groupId, deviceId), s"device-$deviceId")
            context.watchWith(deviceActor, DeviceTerminated(deviceActor, groupId, deviceId))

            deviceIdToActor += deviceId -> deviceActor
            replyTo ! DeviceRegistered(deviceActor)
        }
        this
      case RequestTrackDevice(gId, _, _) =>
        context.log.info("Ignoring track device request for {}. This actor is responsible for {}.", gId, groupId)
        this
      case RequestDeviceList(requestId, gId, replyTo) =>
        if (gId == groupId) {
          replyTo ! ReplyDeviceList(requestId, deviceIdToActor.keySet)
          this
        } else {
          Behaviors.unhandled
        }
      case DeviceTerminated(_, _, deviceId) =>
        context.log.info("Device actor for {} has been terminated.", deviceId)
        deviceIdToActor -= deviceId
        this
      case RequestAllTemperatures(requestId, gId, replyTo) =>
        if (groupId != gId) {
          Behaviors.unhandled
        } else {
          context.spawnAnonymous(DeviceGroupQuery(deviceIdToActor, requestId, replyTo, FiniteDuration(3, TimeUnit.SECONDS)))
          this
        }
    }
  }

  override def onSignal: PartialFunction[Signal, Behavior[DeviceGroup.Command]] = {
    case PostStop =>
      context.log.info("DeviceGroup {} stopped", groupId)
      this
  }
}