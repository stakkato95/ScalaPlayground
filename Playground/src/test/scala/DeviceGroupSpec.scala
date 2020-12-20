import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import guide.iot.DeviceGroup
import guide.iot.DeviceManager.{DeviceRegistered, RequestTrackDevice}
import org.scalatest.wordspec.AnyWordSpecLike

class DeviceGroupSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {

  "DeviceGroup actor" must {

    "be able to register a device actor" in {
      val testProbe = createTestProbe[DeviceRegistered]()
      val deviceGroup = spawn(DeviceGroup(DeviceGroupSpec.GROUP_ID))

      //create first device
      deviceGroup ! RequestTrackDevice(
        groupId = DeviceGroupSpec.GROUP_ID,
        deviceId = DeviceGroupSpec.DEVICE_ONE,
        replyTo = testProbe.ref
      )
      val registered1 = testProbe.receiveMessage()
      val device1 = registered1.device

      //create second device
      deviceGroup ! RequestTrackDevice(
        groupId = DeviceGroupSpec.GROUP_ID,
        deviceId = DeviceGroupSpec.DEVICE_TWO,
        replyTo = testProbe.ref
      )
      val registered2 = testProbe.receiveMessage()
      val device2 = registered2.device

      device1 should !==(device2)
    }
  }
}

object DeviceGroupSpec {
  val GROUP_ID = "group-one"
  val DEVICE_ONE = "device-one"
  val DEVICE_TWO = "device-two"
}