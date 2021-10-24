package com.medina.juanantonio.firemirror.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import coil.load
import com.medina.juanantonio.firemirror.data.models.SpotifyCurrentTrack
import com.medina.juanantonio.firemirror.databinding.ViewSpotifyBinding

class SpotifyView(
    context: Context,
    attrs: AttributeSet
) : ConstraintLayout(context, attrs) {

    private val binding = ViewSpotifyBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    fun updateView(currentTrack: SpotifyCurrentTrack?) {
        if (currentTrack != null) {
            binding.imageViewAlbum.load(currentTrack.albumImageUrl)
            binding.textViewName.text = currentTrack.songName
            binding.textViewSinger.text = currentTrack.artist
            binding.textViewPlaylist.text = currentTrack.albumName

            binding.groupPlaying.isVisible = true
            binding.imageViewSpotifyStandby.isVisible = false
        } else {
            binding.groupPlaying.isVisible = false
            binding.imageViewSpotifyStandby.isVisible = true
        }
    }
}