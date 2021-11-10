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
}