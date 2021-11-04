package com.medina.juanantonio.firemirror.data.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
class BlueButtDevice(
    name: String,
    alias: String
) : BleDevice(name, alias) {

    @PrimaryKey
    override var macAddress: String = ""

    @Ignore
    override var isConnected: Boolean = false

    @Ignore
    override var isDeviceLoading: Boolean = false

    @Ignore
    override var isPaired: Boolean = false

    var clickCount: Int = 0

    // Default Request when Switch Mode is null
    var triggerRequestOff: TriggerRequest = TriggerRequest.default()

    var triggerRequestOn: TriggerRequest = TriggerRequest.default()

    // Switch Mode NULL means Switch Mode is Deactivated
    // Boolean values shows the current status of the Switch
    var switchMode: Boolean? = null

    /**
     * Previous implementation depends on switch Mode having a nullable value
     * If switchMode is null, just trigger the off request,
     * else boolean value determines the trigger request.
     *
     * switchMode.let {
     *      if (it != null) {
     *          if (it) triggerRequestOn.run()
     *          else triggerRequestOff.run()
     *      } else {
     *          triggerRequestOff.run()
     *      }
     * }
     */
    suspend fun runRequest() {
        if (switchMode == null) switchMode = false
        when (switchMode) {
            true -> {
                if (triggerRequestOn.url.isEmpty())
                    triggerRequestOff.run()
                else triggerRequestOn.run()
            }
            false -> {
                triggerRequestOff.run()
            }
        }
    }
}