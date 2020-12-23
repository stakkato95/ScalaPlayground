import java.util.concurrent.TimeUnit

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import guide.iot.Device.{RecordTemperature, RespondTemperature, TemperatureRecorded}
import guide.iot.DeviceGroupQuery.WrappedRespondTemperature
import guide.iot.{Device, DeviceGroup, DeviceGroupQuery}
import guide.iot.DeviceManager.{DeviceRegistered, DeviceTimedOut, RequestAllTemperatures, RequestTrackDevice, RespondAllTemperatures, Temperature, TemperatureNotAvailable}
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.duration.FiniteDuration


class DeviceGroupQuerySpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {

  "DeviceGroupQuerySpec actor" must {

    "return temperature value for working devices" in {
      val requester = createTestProbe[RespondAllTemperatures]()

      val device1 = createTestProbe[Device.Command]()
      val device2 = createTestProbe[Device.Command]()

      val deviceIdToActor = Map(
        DeviceGroupQuerySpec.DEVICE_ONE_ID -> device1.ref,
        DeviceGroupQuerySpec.DEVICE_TWO_ID -> device2.ref
      )

      val queryActor = spawn(DeviceGroupQuery(
        deviceIdToActor,
        DeviceGroupQuerySpec.REQUEST_ID,
        requester.ref,
        DeviceGroupQuerySpec.TIMEOUT
      ))

      device1.expectMessageType[Device.ReadTemperature]
      device2.expectMessageType[Device.ReadTemperature]

      val msg1 = RespondTemperature(
        DeviceGroupQuerySpec.REQUEST_ID,
        DeviceGroupQuerySpec.DEVICE_ONE_ID,
        Some(DeviceGroupQuerySpec.DEVICE_ONE_VALUE)
      )
      val msg2 = RespondTemperature(
        DeviceGroupQuerySpec.REQUEST_ID,
        DeviceGroupQuerySpec.DEVICE_TWO_ID,
        Some(DeviceGroupQuerySpec.DEVICE_TWO_VALUE)
      )

      queryActor ! WrappedRespondTemperature(msg1)
      queryActor ! WrappedRespondTemperature(msg2)

      val allTemperatures = RespondAllTemperatures(
        DeviceGroupQuerySpec.REQUEST_ID,
        Map(
          DeviceGroupQuerySpec.DEVICE_ONE_ID -> Temperature(DeviceGroupQuerySpec.DEVICE_ONE_VALUE),
          DeviceGroupQuerySpec.DEVICE_TWO_ID -> Temperature(DeviceGroupQuerySpec.DEVICE_TWO_VALUE)
        )
      )
      requester.expectMessage(allTemperatures)
    }

    "return TemperatureNotAvailable for devices with no readings" in {
      val requester = createTestProbe[RespondAllTemperatures]()
      val device1 = createTestProbe[Device.Command]()
      val device2 = createTestProbe[Device.Command]()

      val deviceIdToActor = Map(
        DeviceGroupQuerySpec.DEVICE_ONE_ID -> device1.ref,
        DeviceGroupQuerySpec.DEVICE_TWO_ID -> device2.ref
      )

      val queryActor = spawn(DeviceGroupQuery(
        deviceIdToActor,
        DeviceGroupQuerySpec.REQUEST_ID,
        requester.ref,
        DeviceGroupQuerySpec.TIMEOUT
      ))

      device1.expectMessageType[Device.ReadTemperature]
      device2.expectMessageType[Device.ReadTemperature]

      queryActor ! WrappedRespondTemperature(RespondTemperature(
        DeviceGroupQuerySpec.REQUEST_ID,
        DeviceGroupQuerySpec.DEVICE_ONE_ID,
        Some(DeviceGroupQuerySpec.DEVICE_ONE_VALUE)
      ))

      queryActor ! WrappedRespondTemperature(RespondTemperature(
        DeviceGroupQuerySpec.REQUEST_ID,
        DeviceGroupQuerySpec.DEVICE_TWO_ID,
        None
      ))

      val allTemperatures = RespondAllTemperatures(
        DeviceGroupQuerySpec.REQUEST_ID,
        Map(
          DeviceGroupQuerySpec.DEVICE_ONE_ID -> Temperature(DeviceGroupQuerySpec.DEVICE_ONE_VALUE),
          DeviceGroupQuerySpec.DEVICE_TWO_ID -> TemperatureNotAvailable
        )
      )
      requester.expectMessage(allTemperatures)
    }

    "return DeviceNotAvailable if device stops before answering" in {

    }

    "return DeviceTimedOut if device does not answer in time" in {
      val requester = createTestProbe[RespondAllTemperatures]()
      val device1 = createTestProbe[Device.Command]()
      val device2 = createTestProbe[Device.Command]()

      val deviceIdToActor = Map(
        DeviceGroupQuerySpec.DEVICE_ONE_ID -> device1.ref,
        DeviceGroupQuerySpec.DEVICE_TWO_ID -> device2.ref
      )

      val queryActor = spawn(DeviceGroupQuery(
        deviceIdToActor,
        DeviceGroupQuerySpec.REQUEST_ID,
        requester.ref,
        DeviceGroupQuerySpec.TIMEOUT
      ))

      device1.expectMessageType[Device.ReadTemperature]
      device2.expectMessageType[Device.ReadTemperature]

      queryActor ! WrappedRespondTemperature(RespondTemperature(
        DeviceGroupQuerySpec.REQUEST_ID,
        DeviceGroupQuerySpec.DEVICE_ONE_ID,
        Some(DeviceGroupQuerySpec.DEVICE_ONE_VALUE)
      ))

      val allTemperatures = RespondAllTemperatures(
        DeviceGroupQuerySpec.REQUEST_ID,
        Map(
          DeviceGroupQuerySpec.DEVICE_ONE_ID -> Temperature(DeviceGroupQuerySpec.DEVICE_ONE_VALUE),
          DeviceGroupQuerySpec.DEVICE_TWO_ID -> DeviceTimedOut
        )
      )

      requester.awaitAssert(requester.expectMessage(allTemperatures), DeviceGroupQuerySpec.TEST_TIMEOUT)
    }

    "be able to collect temperatures from all active devices" in {
      val registeredProbe = createTestProbe[DeviceRegistered]()
      val groupActor = spawn(DeviceGroup("group"))

      groupActor ! RequestTrackDevice("group", "device1", registeredProbe.ref)
      val deviceActor1 = registeredProbe.receiveMessage().device

      groupActor ! RequestTrackDevice("group", "device2", registeredProbe.ref)
      val deviceActor2 = registeredProbe.receiveMessage().device

      groupActor ! RequestTrackDevice("group", "device3", registeredProbe.ref)
      registeredProbe.receiveMessage()

      // Check that the device actors are working
      val recordProbe = createTestProbe[TemperatureRecorded]()
      deviceActor1 ! RecordTemperature(requestId = 0, 1.0, recordProbe.ref)
      recordProbe.expectMessage(TemperatureRecorded(requestId = 0))
      deviceActor2 ! RecordTemperature(requestId = 1, 2.0, recordProbe.ref)
      recordProbe.expectMessage(TemperatureRecorded(requestId = 1))
      // No temperature for device3

      val allTempProbe = createTestProbe[RespondAllTemperatures]()
      groupActor ! RequestAllTemperatures(requestId = 0, groupId = "group", allTempProbe.ref)
      allTempProbe.expectMessage(
        RespondAllTemperatures(
          requestId = 0,
          temperatures =
            Map("device1" -> Temperature(1.0), "device2" -> Temperature(2.0), "device3" -> TemperatureNotAvailable)))
    }
  }
}

object DeviceGroupQuerySpec {
  val REQUEST_ID = 1

  val DEVICE_ONE_ID = "device1"
  val DEVICE_TWO_ID = "device2"

  val DEVICE_ONE_VALUE = 1
  val DEVICE_TWO_VALUE = 2

  val TIMEOUT = FiniteDuration(3, TimeUnit.SECONDS)
  val TEST_TIMEOUT = FiniteDuration(4, TimeUnit.SECONDS)
}