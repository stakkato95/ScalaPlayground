import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import guide.iot.Device.Passivate
import guide.iot.DeviceGroup
import guide.iot.DeviceManager.{DeviceRegistered, ReplyDeviceList, RequestDeviceList, RequestTrackDevice}
import org.scalatest.wordspec.AnyWordSpecLike

class DeviceGroupSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {

  "DeviceGroup actor" must {

    "be able to register a device actor" in {
      val testProbe = createTestProbe[DeviceRegistered]()
      val deviceGroup = spawn(DeviceGroup(DeviceGroupSpec.GROUP_ONE))

      //create first device
      deviceGroup ! RequestTrackDevice(
        groupId = DeviceGroupSpec.GROUP_ONE,
        deviceId = DeviceGroupSpec.DEVICE_ONE,
        replyTo = testProbe.ref
      )
      val registered1 = testProbe.receiveMessage()
      val device1 = registered1.device

      //create second device
      deviceGroup ! RequestTrackDevice(
        groupId = DeviceGroupSpec.GROUP_ONE,
        deviceId = DeviceGroupSpec.DEVICE_TWO,
        replyTo = testProbe.ref
      )
      val registered2 = testProbe.receiveMessage()
      val device2 = registered2.device

      device1 should !==(device2)
    }

    "ignore requests for wrong groupId" in {
      val probe = createTestProbe[DeviceRegistered]()
      val deviceGroup = spawn(DeviceGroup(DeviceGroupSpec.GROUP_ONE))

      deviceGroup ! RequestTrackDevice(
        groupId = DeviceGroupSpec.GROUP_TWO,
        deviceId = DeviceGroupSpec.DEVICE_TWO,
        replyTo = probe.ref
      )
      probe.expectNoMessage()
    }

    "return same actor for same deviceId" in {
      val probe = createTestProbe[DeviceRegistered]()
      val deviceGroup = spawn(DeviceGroup(DeviceGroupSpec.GROUP_ONE))

      deviceGroup ! RequestTrackDevice(
        groupId = DeviceGroupSpec.GROUP_ONE,
        deviceId = DeviceGroupSpec.DEVICE_ONE,
        replyTo = probe.ref
      )
      val msg1 = probe.receiveMessage()

      deviceGroup ! RequestTrackDevice(
        groupId = DeviceGroupSpec.GROUP_ONE,
        deviceId = DeviceGroupSpec.DEVICE_ONE,
        replyTo = probe.ref
      )
      val msg2 = probe.receiveMessage()

      msg1.device should ===(msg2.device)
    }

    "be able to list active devices" in {
      val registeredProbe = createTestProbe[DeviceRegistered]()
      val listProbe = createTestProbe[ReplyDeviceList]()
      val deviceGroup = spawn(DeviceGroup(DeviceGroupSpec.GROUP_ONE))

      deviceGroup ! RequestTrackDevice(
        groupId = DeviceGroupSpec.GROUP_ONE,
        deviceId = DeviceGroupSpec.DEVICE_ONE,
        replyTo = registeredProbe.ref
      )
      deviceGroup ! RequestTrackDevice(
        groupId = DeviceGroupSpec.GROUP_ONE,
        deviceId = DeviceGroupSpec.DEVICE_TWO,
        replyTo = registeredProbe.ref
      )

      deviceGroup ! RequestDeviceList(
        requestId = DeviceGroupSpec.REQUEST_ID,
        groupId = DeviceGroupSpec.GROUP_ONE,
        replyTo = listProbe.ref
      )
      listProbe.expectMessage(ReplyDeviceList(DeviceGroupSpec.REQUEST_ID, DeviceGroupSpec.DEVICE_IDS))
    }

    "be able to list active devices after one shuts down" in {
      val registeredProbe = createTestProbe[DeviceRegistered]()
      val listProbe = createTestProbe[ReplyDeviceList]()
      val deviceGroup = spawn(DeviceGroup(DeviceGroupSpec.GROUP_ONE))

      deviceGroup ! RequestTrackDevice(
        groupId = DeviceGroupSpec.GROUP_ONE,
        deviceId = DeviceGroupSpec.DEVICE_ONE,
        replyTo = registeredProbe.ref
      )
      //messages are received in FIFO order
      val msg = registeredProbe.receiveMessage()
      val deviceToTerminate = msg.device

      deviceGroup ! RequestTrackDevice(
        groupId = DeviceGroupSpec.GROUP_ONE,
        deviceId = DeviceGroupSpec.DEVICE_TWO,
        replyTo = registeredProbe.ref
      )

      deviceToTerminate ! Passivate
      //can also be called on "listProbe"
      registeredProbe.expectTerminated(deviceToTerminate, registeredProbe.remainingOrDefault)

      //Evaluate the given assert every 100 ms until it does not throw an exception and return the result.
      listProbe.awaitAssert {
        deviceGroup ! RequestDeviceList(
          requestId = DeviceGroupSpec.REQUEST_ID,
          groupId = DeviceGroupSpec.GROUP_ONE,
          replyTo = listProbe.ref
        )
        listProbe.expectMessage(ReplyDeviceList(DeviceGroupSpec.REQUEST_ID, Set(DeviceGroupSpec.DEVICE_TWO)))
      }
    }
  }
}

object DeviceGroupSpec {
  val GROUP_ONE = "group-one"
  val GROUP_TWO = "group-two"

  val DEVICE_ONE = "device-one"
  val DEVICE_TWO = "device-two"
  val DEVICE_IDS = Set(DeviceGroupSpec.DEVICE_ONE, DeviceGroupSpec.DEVICE_TWO)

  val REQUEST_ID = 0
}