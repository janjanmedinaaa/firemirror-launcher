package com.medina.juanantonio.firemirror.common.extensions

import com.medina.juanantonio.firemirror.data.commander.BLEDOMCommander
import com.medina.juanantonio.firemirror.data.models.LabelValue

fun BLEDOMCommander.ColorEffect.toLabelValue(): LabelValue =
    LabelValue(toString().snakeToCapitalize(), this)