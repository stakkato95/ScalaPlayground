import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import guide.iot.Device
import guide.iot.Device.{ReadTemperature, RecordTemperature, RespondTemperature, TemperatureRecorded}
import org.scalatest.wordspec.AnyWordSpecLike

class DeviceSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {

  "Device actor" must {

    "reply with empty reading if no temperature is known" in {
      val probe = createTestProbe[RespondTemperature]()
      val deviceActor = spawn(Device("group", "device"))

      deviceActor ! ReadTemperature(requestId = DeviceSpec.TEMPERATURE_READ_ID, probe.ref)
      val response = probe.receiveMessage()
      response.requestId should ===(DeviceSpec.TEMPERATURE_READ_ID)
      response.value should ===(None)
    }

    "reply with latest temperature reading" in {
      val recordProbe = createTestProbe[TemperatureRecorded]()
      val respondProbe = createTestProbe[RespondTemperature]()
      val deviceActor = spawn(Device("group", "device"))

      deviceActor ! RecordTemperature(
        requestId = DeviceSpec.TEMPERATURE_RECORD_ID,
        value = DeviceSpec.TEMPERATURE_VALUE,
        replyTo = recordProbe.ref
      )
      recordProbe.expectMessage(TemperatureRecorded(DeviceSpec.TEMPERATURE_RECORD_ID))

      deviceActor ! ReadTemperature(DeviceSpec.TEMPERATURE_READ_ID, respondProbe.ref)
      //respondProbe.expectMessage(RespondTemperature(DeviceSpec.TEMPERATURE_READ_ID, Some(DeviceSpec.TEMPERATURE_VALUE)))

      val response = respondProbe.receiveMessage()
      response.requestId should ===(DeviceSpec.TEMPERATURE_READ_ID)
      response.value should ===(Some(DeviceSpec.TEMPERATURE_VALUE))
    }
  }
}

object DeviceSpec {
  val TEMPERATURE_READ_ID = 1

  val TEMPERATURE_RECORD_ID = 2
  val TEMPERATURE_VALUE = 100500
}