package com.medina.juanantonio.firemirror.data.models.listdisplay

import androidx.annotation.DrawableRes
import com.medina.juanantonio.firemirror.R

data class ImageListDisplayItem(
    val imageUrl: String? = null,
    @DrawableRes val drawable: Int? = null
) : ListDisplayItem {

    override var viewType: Int = R.layout.item_list_display_image
}