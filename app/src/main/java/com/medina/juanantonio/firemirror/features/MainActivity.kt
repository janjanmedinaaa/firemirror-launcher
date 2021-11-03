package com.medina.juanantonio.firemirror.features

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.viewModels
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.medina.juanantonio.firemirror.R
import com.medina.juanantonio.firemirror.ble.BluetoothLEServiceManager
import com.medina.juanantonio.firemirror.data.managers.SpotifyManager
import com.medina.juanantonio.firemirror.data.receivers.RestartReceiver
import com.medina.juanantonio.firemirror.databinding.ActivityMainBinding
import com.medina.juanantonio.firemirror.features.server.FireMirrorServer
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationResponse
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var bluetoothLEServiceManager: BluetoothLEServiceManager

    @Inject
    lateinit var fireMirrorServer: FireMirrorServer

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        RestartReceiver.startAlarm(applicationContext)
        fireMirrorServer.start()
    }

    override fun onResume() {
        super.onResume()

        runWithPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) {
            bluetoothLEServiceManager.startBluetoothLEService()
        }
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
        if (viewModel.currentScreenLayout != R.layout.fragment_home)
            return super.dispatchKeyEvent(event)

        if (event?.keyCode == KeyEvent.KEYCODE_DPAD_LEFT ||
            event?.keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ||
            event?.keyCode == KeyEvent.KEYCODE_DPAD_UP ||
            event?.keyCode == KeyEvent.KEYCODE_DPAD_DOWN ||
            event?.keyCode == KeyEvent.KEYCODE_MEDIA_REWIND ||
            event?.keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD
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

    override fun finish() {
        bluetoothLEServiceManager.stopBluetoothLEService()
        fireMirrorServer.stop()
        super.finish()
    }
}