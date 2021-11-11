package com.medina.juanantonio.firemirror.data.models

import com.medina.juanantonio.firemirror.data.commander.BLEDOMCommander
import com.medina.juanantonio.firemirror.data.commander.BLEDOMCommander.Utils.COMMAND_DELAY
import kotlinx.coroutines.delay

class LEDData {
    var red: Int = 255
    var green: Int = 255
    var blue: Int = 255

    var grayScale: Int = 0
    var temperature: Int = 1
    var speed: Int = 0
    var brightness: Int = 100
    var isOn: Boolean = false

    var colorEffect: BLEDOMCommander.ColorEffect? = null

    suspend fun getCommandBytes(onBytes: (ByteArray) -> Unit) {
        onBytes(BLEDOMCommander.setPower(isOn))
        delay(COMMAND_DELAY)

        colorEffect.let {
            if (it != null) {
                onBytes(BLEDOMCommander.setModeEffect(it))
            } else {
                onBytes(
                    BLEDOMCommander.setColorRGB(
                        red = red,
                        green = green,
                        blue = blue,
                    )
                )
                delay(COMMAND_DELAY)
                onBytes(BLEDOMCommander.setBrightness(brightness))
            }
        }

        delay(COMMAND_DELAY)
        onBytes(BLEDOMCommander.setSpeed(speed))
    }
}