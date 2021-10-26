package com.medina.juanantonio.firemirror.data.models.listdisplay

import com.medina.juanantonio.firemirror.R
import com.medina.juanantonio.firemirror.data.models.SpotifyCurrentTrack

data class SpotifyListDisplayItem(
    val currentTrack: SpotifyCurrentTrack?
): ListDisplayItem {

    override var viewType: Int = R.layout.item_list_display_spotify
}