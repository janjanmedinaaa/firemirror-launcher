package com.medina.juanantonio.firemirror.data.models.listdisplay

import com.medina.juanantonio.firemirror.R

data class DefaultListDisplayItem(
    val label: String,
    val value: String
): ListDisplayItem {

    override var viewType: Int = R.layout.item_list_display_default
}