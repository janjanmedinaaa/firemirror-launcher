package com.medina.juanantonio.firemirror.common.dialogs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medina.juanantonio.firemirror.ble.IBluetoothLEManager
import com.medina.juanantonio.firemirror.common.utils.Event
import com.medina.juanantonio.firemirror.data.commander.BLEDOMCommander
import com.medina.juanantonio.firemirror.data.managers.IBLEDOMDevicesManager
import com.medina.juanantonio.firemirror.data.models.LEDData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class LEDOptionsViewModel @Inject constructor(
    private var bluetoothLeManager: IBluetoothLEManager,
    private var bleDomDevicesManager: IBLEDOMDevicesManager
) : ViewModel() {

    val ledData = MutableLiveData<Event<LEDData>>()

    var macAddress = ""
    var currentColorEffect: BLEDOMCommander.ColorEffect? = null

    private var jobSaveConfig: Job? = null

    fun writeToDevice(command: ByteArray) {
        bluetoothLeManager.writeToDevice(macAddress, command)
    }

    fun getBLEDOMDevice(macAddress: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val ledData = bleDomDevicesManager.getDevice(macAddress)?.ledData ?: return@launch
            withContext(Dispatchers.Main) {
                this@LEDOptionsViewModel.ledData.value = Event(ledData)
            }
        }
    }

    fun setupSaveConfig(ledData: LEDData) {
        jobSaveConfig?.cancel()
        jobSaveConfig = viewModelScope.launch {
            delay(500)
            bleDomDevicesManager.updateDeviceLEDData(macAddress, ledData)
        }
    }
}