package com.medina.juanantonio.firemirror.common.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.medina.juanantonio.firemirror.R
import com.medina.juanantonio.firemirror.common.extensions.animateTextSize
import com.medina.juanantonio.firemirror.common.utils.autoCleared
import com.medina.juanantonio.firemirror.data.commander.BLEDOMCommander
import com.medina.juanantonio.firemirror.data.models.LEDData
import com.medina.juanantonio.firemirror.databinding.DialogLedOptionsBinding
import com.medina.juanantonio.firemirror.features.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LEDOptionsDialog : DialogFragment() {

    companion object {
        const val DEVICE_MAC_ADDRESS_ARG = "deviceMacAddressArg"
    }

    private var binding: DialogLedOptionsBinding by autoCleared()
    private val viewModel: LEDOptionsViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogLedOptionsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.currentScreenLayout = R.layout.dialog_led_options
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString(DEVICE_MAC_ADDRESS_ARG)?.let {
            viewModel.macAddress = it
            viewModel.getBLEDOMDevice(macAddress = it)
        }

        binding.switchPower.setOnCheckedChangeListener { _, isChecked ->
            updatePowerStatus(isChecked, saveConfig = true)
        }

        binding.sliderRed.let {
            binding.textViewRedLabel.apply {
                it.addOnChangeListener { _, value, fromUser ->
                    text = getString(R.string.red_label_format, value.toInt())
                    if (fromUser) updateRGB(saveConfig = true)
                }
                it.setOnFocusChangeListener { _, isFocused ->
                    animateTextSize(if (isFocused) 20f else 16f)
                }
            }
        }

        binding.sliderGreen.let {
            binding.textViewGreenLabel.apply {
                it.addOnChangeListener { _, value, fromUser ->
                    text = getString(R.string.green_label_format, value.toInt())
                    if (fromUser) updateRGB(saveConfig = true)
                }
                it.setOnFocusChangeListener { _, isFocused ->
                    animateTextSize(if (isFocused) 20f else 16f)
                }
            }
        }

        binding.sliderBlue.let {
            binding.textViewBlueLabel.apply {
                it.addOnChangeListener { _, value, fromUser ->
                    text = getString(R.string.blue_label_format, value.toInt())
                    if (fromUser) updateRGB(saveConfig = true)
                }
                it.setOnFocusChangeListener { _, isFocused ->
                    animateTextSize(if (isFocused) 20f else 16f)
                }
            }
        }

        binding.sliderBrightness.let {
            binding.textViewBrightnessLabel.apply {
                it.addOnChangeListener { _, value, fromUser ->
                    text = getString(R.string.brightness_label_format, value.toInt())
                    if (fromUser) updateBrightness(value.toInt(), saveConfig = true)
                }
                it.setOnFocusChangeListener { _, isFocused ->
                    animateTextSize(if (isFocused) 20f else 16f)
                }
            }
        }

        listenToVM()
    }

    private fun listenToVM() {
        viewModel.ledData.observe(viewLifecycleOwner) {
            binding.sliderRed.value = it.red.toFloat()
            binding.sliderGreen.value = it.green.toFloat()
            binding.sliderBlue.value = it.blue.toFloat()
            binding.sliderBrightness.value = it.brightness.toFloat()
            binding.switchPower.isChecked = it.isOn

            viewModel.viewModelScope.launch {
                updatePowerStatus(it.isOn)
                delay(20)
                updateRGB(red = it.red, green = it.green, blue = it.blue)
                delay(20)
                updateBrightness(it.brightness)
            }
        }
    }

    private fun updatePowerStatus(isPowerOn: Boolean, saveConfig: Boolean = false) {
        if (saveConfig) saveConfig(isOn = isPowerOn)
        viewModel.writeToDevice(
            BLEDOMCommander.setPower(isPowerOn)
        )
    }

    private fun updateRGB(
        red: Int = binding.sliderRed.value.toInt(),
        green: Int = binding.sliderGreen.value.toInt(),
        blue: Int = binding.sliderBlue.value.toInt(),
        saveConfig: Boolean = false
    ) {
        if (saveConfig) saveConfig(red = red, green = green, blue = blue)
        viewModel.writeToDevice(
            BLEDOMCommander.setColorRGB(
                red = red,
                green = green,
                blue = blue,
            )
        )
    }

    private fun updateBrightness(brightness: Int, saveConfig: Boolean = false) {
        if (saveConfig) saveConfig(brightness = brightness)
        viewModel.writeToDevice(
            BLEDOMCommander.setBrightness(brightness)
        )
    }

    private fun saveConfig(
        red: Int = binding.sliderRed.value.toInt(),
        green: Int = binding.sliderGreen.value.toInt(),
        blue: Int = binding.sliderBlue.value.toInt(),
        brightness: Int = binding.sliderBrightness.value.toInt(),
        isOn: Boolean = binding.switchPower.isChecked
    ) {
        viewModel.setupSaveConfig(
            LEDData().apply {
                this.red = red
                this.green = green
                this.blue = blue
                this.brightness = brightness
                this.isOn = isOn
            }
        )
    }
}