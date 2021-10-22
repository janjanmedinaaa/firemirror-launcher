package com.medina.juanantonio.firemirror.data.models

import androidx.annotation.DrawableRes
import com.medina.juanantonio.firemirror.R

data class IconLabelListDisplayItem(
    val label: String,
    @DrawableRes val drawable: Int
): ListDisplayItem {

    override var viewType: Int = R.layout.item_list_display_icon_label
}