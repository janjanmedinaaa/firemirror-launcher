package com.medina.juanantonio.firemirror.data.models

data class SpotifyCurrentTrack(
    val item: Item?
) {

    data class Item(
        val name: String,
        val album: Album,
    ) {

        data class Album(
            val artists: List<Artist>,
            val images: List<Image>,
            val name: String
        ) {

            data class Artist(
                val name: String
            )

            data class Image(
                val height: Int,
                val url: String,
                val width: Int
            )
        }
    }

    val songName: String
        get() = item?.name ?: ""

    val albumImageUrl: String
        get() = item?.album?.images?.first()?.url ?: ""

    val artist: String
        get() = item?.album?.artists?.first()?.name ?: ""

    val albumName: String
        get() = item?.album?.name ?: ""
}