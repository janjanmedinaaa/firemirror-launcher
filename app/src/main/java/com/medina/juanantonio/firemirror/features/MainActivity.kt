package com.medina.juanantonio.firemirror.features

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.viewModels
import com.medina.juanantonio.firemirror.R
import com.medina.juanantonio.firemirror.data.managers.SpotifyManager
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationResponse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    /**
     * 82 - Menu Button
     * 19 - Up
     * 20 - Down
     * 21 - Left
     * 22 - Right
     * 4 - Back Button
     * 89 - Playback
     * 85 - Play/Pause
     * 90 - Play Forward
     */
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.keyCode == KeyEvent.KEYCODE_DPAD_LEFT ||
            event?.keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ||
            event?.keyCode == KeyEvent.KEYCODE_DPAD_UP ||
            event?.keyCode == KeyEvent.KEYCODE_DPAD_DOWN ||
            event?.keyCode == KeyEvent.KEYCODE_MENU
        ) {
            if (event.action == 0)
                viewModel.dispatchKeyEvent.value = event

            return true
        }

        return super.dispatchKeyEvent(event)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.let {
            val response = AuthorizationResponse.fromUri(it)
            viewModel.authorizationResponse.value = response
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != SpotifyManager.REQUEST_CODE) return

        val response = AuthorizationClient.getResponse(resultCode, data)
        viewModel.authorizationResponse.value = response
    }
}