package com.medina.juanantonio.firemirror.common.dialogs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.medina.juanantonio.firemirror.R
import com.medina.juanantonio.firemirror.ble.BluetoothLEService
import com.medina.juanantonio.firemirror.ble.IBluetoothLEManager
import com.medina.juanantonio.firemirror.common.utils.autoCleared
import com.medina.juanantonio.firemirror.data.adapters.BluetoothDevicesAdapter
import com.medina.juanantonio.firemirror.data.models.BlueButtDevice
import com.medina.juanantonio.firemirror.databinding.DialogBluetoothDevicesBinding
import com.medina.juanantonio.firemirror.features.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BluetoothDevicesListDialog :
    DialogFragment(),
    BluetoothDevicesAdapter.BlueButtDeviceListener {

    private var binding: DialogBluetoothDevicesBinding by autoCleared()
    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var bluetoothDevicesAdapter: BluetoothDevicesAdapter

    private val localBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(requireContext())
    }

    private val bluetoothLeServiceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            @Suppress("UNCHECKED_CAST")
            val scannedDevices = intent?.getSerializableExtra(
                BluetoothLEService.BLUE_BUTT_DEVICES
            ) as? HashMap<String, BlueButtDevice>

            scannedDevices?.let {
                bluetoothDevicesAdapter.submitList(
                    it.map { (_, device) ->
                        Log.d("DEVELOP", "ITEM $device")
                        device
                    }
                )
            }
        }
    }

    @Inject
    lateinit var bluetoothLeManager: IBluetoothLEManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBluetoothDevicesBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.currentScreenLayout = R.layout.dialog_bluetooth_devices
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mainViewModel.currentScreenLayout = R.layout.fragment_home
        localBroadcastManager.unregisterReceiver(bluetoothLeServiceReceiver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bluetoothDevicesAdapter = BluetoothDevicesAdapter(this)
        binding.recyclerViewBluetoothDevices.apply {
            adapter = bluetoothDevicesAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }

        localBroadcastManager.registerReceiver(
            bluetoothLeServiceReceiver,
            IntentFilter(BluetoothLEService.SCANNED_DEVICES)
        )
        bluetoothLeManager.refreshDeviceList()
    }

    override fun onDeviceClicked(item: BlueButtDevice) {
        CoroutineScope(Dispatchers.IO).launch {
            if (!item.isConnected)
                bluetoothLeManager.connectDevice(item.macAddress)
        }
    }

    override fun onDeviceLongClicked(item: BlueButtDevice) {
        CoroutineScope(Dispatchers.IO).launch {
            if (item.isConnected)
                bluetoothLeManager.disconnectDevice(item.macAddress)
        }
    }
}