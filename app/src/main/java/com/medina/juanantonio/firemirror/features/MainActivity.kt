package com.medina.juanantonio.firemirror.features

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.viewModels
import com.medina.juanantonio.firemirror.ble.BluetoothLEServiceManager
import com.medina.juanantonio.firemirror.data.managers.SpotifyManager
import com.medina.juanantonio.firemirror.databinding.ActivityMainBinding
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationResponse
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var bluetoothLEServiceManager: BluetoothLEServiceManager

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        TODO: Start Bluetooth LE Service
//        runWithPermissions(
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        ) {
//            bluetoothLEServiceManager.startBluetoothLEService()
//        }
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

    override fun finish() {
        bluetoothLEServiceManager.stopBluetoothLEService()
        super.finish()
    }
}