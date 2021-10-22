package com.medina.juanantonio.firemirror.data.models.listdisplay

import com.medina.juanantonio.firemirror.R

data class ImageListDisplayItem(
    val imageUrl: String
): ListDisplayItem {

    override var viewType: Int = R.layout.item_list_display_image
}