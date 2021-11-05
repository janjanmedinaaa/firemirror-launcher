package com.medina.juanantonio.firemirror.data.commander

/**
 * Currently works on Baseus T2. Haven't tested on other models.
 */
object BaseusCommander {

    object NotificationCommand {
        const val OP_CODE_BUTTON_CLICK = (0xAA08).toByte()
    }

    fun turnOnAlarm(): ByteArray {
        return byteArrayOf((0xBA).toByte(), 0x03, 0x01)
    }
}