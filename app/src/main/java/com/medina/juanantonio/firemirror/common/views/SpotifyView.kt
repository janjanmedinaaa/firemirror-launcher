package com.medina.juanantonio.firemirror.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
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

    val spotifyLogo: AppCompatImageView
        get() = binding.imageViewSpotifyStandby

    val imageView: AppCompatImageView
        get() = binding.imageViewAlbum

    val textBackground: View
        get() = binding.viewTextBackground

    val isOnStandBy: Boolean
        get() = binding.imageViewSpotifyStandby.isVisible

    fun updateView(currentTrack: SpotifyCurrentTrack?) {
        currentTrack?.run {
            binding.imageViewAlbum.load(albumImageUrl)
            binding.textViewName.text = songName
            binding.textViewSinger.text = artist
            binding.textViewPlaylist.text = albumName
        }

        binding.groupPlaying.isInvisible = currentTrack == null
        binding.imageViewSpotifyStandby.isInvisible = currentTrack != null
    }
}