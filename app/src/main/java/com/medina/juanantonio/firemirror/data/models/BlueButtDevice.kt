package com.medina.juanantonio.firemirror.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class BlueButtDevice(
    val name: String,
    var alias: String,
    val macAddress: String
) : Parcelable {

    companion object {

        fun default(): BlueButtDevice {
            return BlueButtDevice("", "", "")
        }
    }

    @PrimaryKey(autoGenerate = true)
    var id: Int = -1

    var clickCount: Int = 0

    // Default Request when Switch Mode is null
    var triggerRequestOff: TriggerRequest = TriggerRequest.default()

    var triggerRequestOn: TriggerRequest = TriggerRequest.default()

    // Switch Mode NULL means Switch Mode is Deactivated
    // Boolean values shows the current status of the Switch
    var switchMode: Boolean? = null

    @Ignore
    var isConnected: Boolean = false

    @Ignore
    var isPaired: Boolean = false

    fun getDeviceName() =
        if (alias.isNotEmpty()) alias else name

    suspend fun runRequest() {
        switchMode.let {
            if (it != null) {
                if (it) triggerRequestOn.run()
                else triggerRequestOff.run()
            } else {
                triggerRequestOff.run()
            }
        }
    }
}