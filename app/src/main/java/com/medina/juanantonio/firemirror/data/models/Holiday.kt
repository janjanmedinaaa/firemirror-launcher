package com.medina.juanantonio.firemirror.data.models

data class Holiday(
    val name: String,
    val constant: Boolean
) {
    var month: Int = -1
    var dayOfMonth: Int = -1
}