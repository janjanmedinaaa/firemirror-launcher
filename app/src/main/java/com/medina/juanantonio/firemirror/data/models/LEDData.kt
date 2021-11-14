package com.medina.juanantonio.firemirror.data.models

import com.medina.juanantonio.firemirror.data.commander.BLEDOMCommander

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

    fun getCommandBytes(): ArrayList<ByteArray> {
        return arrayListOf<ByteArray>().apply {
            add(BLEDOMCommander.setPower(isOn))

            colorEffect.let {
                if (it != null) {
                    add(BLEDOMCommander.setModeEffect(it))
                } else {
                    add(
                        BLEDOMCommander.setColorRGB(
                            red = red,
                            green = green,
                            blue = blue,
                        )
                    )
                    add(BLEDOMCommander.setBrightness(brightness))
                }
            }

            add(BLEDOMCommander.setSpeed(speed))
        }
    }
}