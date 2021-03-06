package guide.iot

import akka.actor.typed.{ActorRef, Behavior, PostStop, Signal}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext}
import guide.iot.DeviceManager.{Command, DeviceGroupTerminated, ReplyDeviceList, RequestDeviceList, RequestTrackDevice}

object DeviceManager {

  sealed trait Command

  final case class RequestTrackDevice(groupId: String, deviceId: String, replyTo: ActorRef[DeviceRegistered])
    extends Command with DeviceGroup.Command

  final case class DeviceRegistered(device: ActorRef[Device.Command])

  final case class RequestDeviceList(requestId: Long, groupId: String, replyTo: ActorRef[ReplyDeviceList])
    extends Command with DeviceGroup.Command

  final case class ReplyDeviceList(requestId: Long, ids: Set[String])

  private final case class DeviceGroupTerminated(groupId: String) extends DeviceManager.Command

  //requesting temperature from all devices
  //this msg should be understood by DeviceGroupQuery and DeviceGroup actors, so it also extends them
  final case class RequestAllTemperatures(requestId: Long, groupId: String, replyTo: ActorRef[RespondAllTemperatures])
    extends DeviceGroupQuery.Command with DeviceGroup.Command with Command

  final case class RespondAllTemperatures(requestId: Long, temperatures: Map[String, TemperatureReading])

  //different results of temperature reading
  sealed trait TemperatureReading

  final case class Temperature(value: Double) extends TemperatureReading

  final object TemperatureNotAvailable extends TemperatureReading

  final object DeviceNotAvailable extends TemperatureReading

  final object DeviceTimedOut extends TemperatureReading

}

class DeviceManager(context: ActorContext[Command]) extends AbstractBehavior[Command](context) {

  var groupIdToActor = Map.empty[String, ActorRef[DeviceGroup.Command]]

  context.log.info("DeviceManager started")

  override def onMessage(msg: Command): Behavior[Command] = {
    msg match {
      case trackMsg@RequestTrackDevice(groupId, _, replyTo) =>
        groupIdToActor.get(groupId) match {
          case Some(ref) =>
            ref ! trackMsg
            this
          case None =>
            val group = context.spawn(DeviceGroup(groupId), s"group-$groupId")
            context.watchWith(group, DeviceGroupTerminated(groupId))
            groupIdToActor += groupId -> group
            group ! trackMsg
            this
        }
      case req@RequestDeviceList(requestId, groupId, replyTo) =>
        groupIdToActor.get(groupId) match {
          case Some(ref) =>
            ref ! req
          case None =>
            replyTo ! ReplyDeviceList(requestId, Set.empty)
        }
        this
      case DeviceGroupTerminated(groupId) =>
        groupIdToActor -= groupId
        context.log.info("Device group id {} has terminated", groupId)
        this
    }
  }

  override def onSignal: PartialFunction[Signal, Behavior[Command]] = {
    case PostStop =>
      context.log.info("DeviceManager stopped")
      this
  }
}