package com.medina.juanantonio.firemirror.common.extensions

import com.medina.juanantonio.firemirror.common.views.ListDisplayView
import com.medina.juanantonio.firemirror.common.views.SpotifyView
import com.medina.juanantonio.firemirror.databinding.ItemListDisplaySpotifyBinding

val ListDisplayView.spotifyView: SpotifyView?
    get() = (adapter.viewList[0] as? ItemListDisplaySpotifyBinding)?.viewSpotify