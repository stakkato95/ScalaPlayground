package guide.iot

import akka.actor.typed.{ActorRef, Behavior, PostStop, Signal}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import guide.iot.Device.{Command, ReadTemperature, RecordTemperature, RespondTemperature, TemperatureRecorded}

object Device {

  def apply(groupId: String, deviceId: String): Behavior[Command] = Behaviors.setup(new Device(_, groupId, deviceId))

  sealed trait Command

  //requests to a device from IoT system
  final case class ReadTemperature(requestId: Long, replyTo: ActorRef[RespondTemperature]) extends Command

  //ACK response of a device to IoT system
  final case class RespondTemperature(requestId: Long, value: Option[Double])

  //requests to a device from a sensor
  //"Record" - command
  final case class RecordTemperature(requestId: Long, value: Double, replyTo: ActorRef[TemperatureRecorded]) extends Command

  //ACK response of a device to a sensor
  //"RecordED" - acknowledgement
  final case class TemperatureRecorded(requestId: Long)

}

class Device(context: ActorContext[Command], groupId: String, deviceId: String) extends AbstractBehavior[Command](context) {

  var lastTemperatureReading: Option[Double] = None

  context.log.info("Device actor {}-{} started", groupId, deviceId)

  override def onMessage(msg: Command): Behavior[Command] = {
    msg match {
      case RecordTemperature(requestId, value, replyTo) =>
        context.log.info("Recorded temperature reading {} with {}", value, requestId)
        lastTemperatureReading = Some(value)
        replyTo ! TemperatureRecorded(requestId)
        this
      case ReadTemperature(requestId, replyTo) =>
        replyTo ! RespondTemperature(requestId, lastTemperatureReading)
        this
    }
  }

  override def onSignal: PartialFunction[Signal, Behavior[Command]] = {
    case PostStop =>
      context.log.info("Device actor {}-{} stopped", groupId, deviceId)
      this
  }
}