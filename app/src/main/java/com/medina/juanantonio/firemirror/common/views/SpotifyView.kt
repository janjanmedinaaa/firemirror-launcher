package com.medina.juanantonio.firemirror.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import coil.load
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

    init {
        binding.imageViewAlbum.load(
            "https://cdn-s3.allmusic.com/release-covers/400/0003/135/0003135306.jpg"
        )
    }
}