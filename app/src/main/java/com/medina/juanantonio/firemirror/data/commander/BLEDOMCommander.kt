package com.medina.juanantonio.firemirror.data.commander

import androidx.annotation.IntRange

/**
 * Controller for sending commands to Chinese generic Bluetooth receiver
 * ELK-BLEDOM on most RGB LED strips.
 *
 * Credits:
 * https://github.com/arduino12/ble_rgb_led_strip_controller
 * https://github.com/TheSylex/ELK-BLEDOM-bluetooth-led-strip-controller
 */
object BLEDOMCommander {

    object CommandBytes {
        const val COMMAND_START_BYTE: Byte = 0x7E
        const val COMMAND_END_BYTE: Byte = (0xEF).toByte()
        const val COMMAND_FILL_BYTE: Byte = 0x00 // Byte not important so I'm using 0x00

        const val COMMAND_ID_BRIGHTNESS: Byte = 0x01
        const val COMMAND_ID_SPEED: Byte = 0x02
        const val COMMAND_ID_MODE: Byte = 0x03
        const val COMMAND_ID_POWER: Byte = 0x04
        const val COMMAND_ID_COLOR: Byte = 0x05

        const val COMMAND_SUB_ID_GRAYSCALE: Byte = 0x01
        const val COMMAND_SUB_ID_TEMPERATURE: Byte = 0x02
        const val COMMAND_SUB_ID_RGB: Byte = 0x03

        const val COMMAND_ARG_1_TEMPERATURE: Byte = 0x02
        const val COMMAND_ARG_1_MODE_EFFECT: Byte = 0x03
    }

    fun setBrightness(
        @IntRange(from = 0, to = 100) brightness: Int
    ): ByteArray {
        return setupCommandByteArray(
            commandId = CommandBytes.COMMAND_ID_BRIGHTNESS,
            commandSubId = brightness.toByte()
        )
    }

    fun setSpeed(
        @IntRange(from = 0, to = 100) speed: Int
    ): ByteArray {
        return setupCommandByteArray(
            commandId = CommandBytes.COMMAND_ID_SPEED,
            commandSubId = speed.toByte()
        )
    }

    fun setModeTemperature(
        @IntRange(from = 128, to = 138) temperature: Int
    ): ByteArray {
        return setupCommandByteArray(
            commandId = CommandBytes.COMMAND_ID_MODE,
            commandSubId = temperature.toByte(),
            commandArg1 = CommandBytes.COMMAND_ARG_1_TEMPERATURE
        )
    }

    fun setModeEffect(colorEffect: ColorEffect): ByteArray {
        return setupCommandByteArray(
            commandId = CommandBytes.COMMAND_ID_MODE,
            commandSubId = colorEffect.value,
            commandArg1 = CommandBytes.COMMAND_ARG_1_MODE_EFFECT
        )
    }

    fun setPower(turnOn: Boolean): ByteArray {
        val turnOnByte: Byte = if (turnOn) 0x01 else 0x00
        return setupCommandByteArray(
            commandId = CommandBytes.COMMAND_ID_POWER,
            commandSubId = turnOnByte
        )
    }

    fun setColorGrayscale(
        @IntRange(from = 0, to = 100) grayScale: Int
    ): ByteArray {
        return setupCommandByteArray(
            commandId = CommandBytes.COMMAND_ID_COLOR,
            commandSubId = CommandBytes.COMMAND_SUB_ID_GRAYSCALE,
            commandArg1 = grayScale.toByte()
        )
    }

    fun setColorTemperature(
        @IntRange(from = 0, to = 100) temperature: Int
    ): ByteArray {
        return setupCommandByteArray(
            commandId = CommandBytes.COMMAND_ID_COLOR,
            commandSubId = CommandBytes.COMMAND_SUB_ID_TEMPERATURE,
            commandArg1 = temperature.toByte()
        )
    }

    fun setColorRGB(
        @IntRange(from = 0, to = 255) red: Int,
        @IntRange(from = 0, to = 255) green: Int,
        @IntRange(from = 0, to = 255) blue: Int
    ): ByteArray {
        return setupCommandByteArray(
            commandId = CommandBytes.COMMAND_ID_COLOR,
            commandSubId = CommandBytes.COMMAND_SUB_ID_RGB,
            commandArg1 = red.toByte(),
            commandArg2 = green.toByte(),
            commandArg3 = blue.toByte()
        )
    }

    private fun setupCommandByteArray(
        commandId: Byte,
        commandSubId: Byte,
        commandArg1: Byte = CommandBytes.COMMAND_FILL_BYTE,
        commandArg2: Byte = CommandBytes.COMMAND_FILL_BYTE,
        commandArg3: Byte = CommandBytes.COMMAND_FILL_BYTE
    ): ByteArray {
        return byteArrayOf(
            CommandBytes.COMMAND_START_BYTE,
            CommandBytes.COMMAND_FILL_BYTE,
            commandId,
            commandSubId,
            commandArg1,
            commandArg2,
            commandArg3,
            CommandBytes.COMMAND_FILL_BYTE,
            CommandBytes.COMMAND_END_BYTE
        )
    }

    enum class ColorEffect(val value: Byte) {
        RED(value = (0x80).toByte()),
        GREEN(value = (0x81).toByte()),
        BLUE(value = (0x82).toByte()),
        YELLOW(value = (0x83).toByte()),
        CYAN(value = (0x84).toByte()),
        MAGENTA(value = (0x85).toByte()),
        WHITE(value = (0x86).toByte()),
        JUMP_RGB(value = (0x87).toByte()),
        JUMP_RGBYCMW(value = (0x88).toByte()),
        GRADIENT_RGB(value = (0x89).toByte()),
        GRADIENT_RGBYCMW(value = (0x8A).toByte()),
        GRADIENT_RED(value = (0x8B).toByte()),
        GRADIENT_GREEN(value = (0x8C).toByte()),
        GRADIENT_BLUE(value = (0x8D).toByte()),
        GRADIENT_YELLOW(value = (0x8E).toByte()),
        GRADIENT_CYAN(value = (0x8F).toByte()),
        GRADIENT_MAGENTA(value = (0x90).toByte()),
        GRADIENT_WHITE(value = (0x91).toByte()),
        GRADIENT_RED_GREEN(value = (0x92).toByte()),
        GRADIENT_RED_BLUE(value = (0x93).toByte()),
        GRADIENT_GREEN_BLUE(value = (0x94).toByte()),
        BLINK_RGBYCMW(value = (0x95).toByte()),
        BLINK_RED(value = (0x96).toByte()),
        BLINK_GREEN(value = (0x97).toByte()),
        BLINK_BLUE(value = (0x98).toByte()),
        BLINK_YELLOW(value = (0x99).toByte()),
        BLINK_CYAN(value = (0x9A).toByte()),
        BLINK_MAGENTA(value = (0x9B).toByte()),
        BLINK_WHITE(value = (0x9C).toByte())
    }
}