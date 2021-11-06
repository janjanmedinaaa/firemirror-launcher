package com.medina.juanantonio.firemirror.common.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.medina.juanantonio.firemirror.R
import com.medina.juanantonio.firemirror.common.extensions.animateTextSize
import com.medina.juanantonio.firemirror.common.utils.autoCleared
import com.medina.juanantonio.firemirror.databinding.DialogLedOptionsBinding
import com.medina.juanantonio.firemirror.features.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LEDOptionsDialog : DialogFragment() {

    companion object {
        const val DEVICE_MAC_ADDRESS_ARG = "deviceMacAddressArg"
    }

    private var binding: DialogLedOptionsBinding by autoCleared()
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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mainViewModel.currentScreenLayout = R.layout.fragment_home
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.switchPower.setOnCheckedChangeListener { _, isChecked ->

        }

        binding.sliderRed.addOnChangeListener { _, value, _ ->
            binding.textViewRedLabel.text =
                getString(R.string.red_label_format, value.toInt())
        }

        binding.sliderRed.setOnFocusChangeListener { _, isFocused ->
            binding.textViewRedLabel.animateTextSize(if (isFocused) 20f else 16f)
        }

        binding.sliderGreen.addOnChangeListener { _, value, _ ->
            binding.textViewGreenLabel.text =
                getString(R.string.green_label_format, value.toInt())
        }

        binding.sliderGreen.setOnFocusChangeListener { _, isFocused ->
            binding.textViewGreenLabel.animateTextSize(if (isFocused) 20f else 16f)
        }

        binding.sliderBlue.addOnChangeListener { _, value, _ ->
            binding.textViewBlueLabel.text =
                getString(R.string.blue_label_format, value.toInt())
        }

        binding.sliderBlue.setOnFocusChangeListener { _, isFocused ->
            binding.textViewBlueLabel.animateTextSize(if (isFocused) 20f else 16f)
        }

        binding.sliderBrightness.addOnChangeListener { _, value, _ ->
            binding.textViewBrightnessLabel.text =
                getString(R.string.brightness_label_format, value.toInt())
        }

        binding.sliderBrightness.setOnFocusChangeListener { _, isFocused ->
            binding.textViewBrightnessLabel.animateTextSize(if (isFocused) 20f else 16f)
        }

        binding.sliderTemperature.addOnChangeListener { _, value, _ ->
            binding.textViewTemperatureLabel.text =
                getString(R.string.temperature_label_format, value.toInt())
        }

        binding.sliderTemperature.setOnFocusChangeListener { _, isFocused ->
            binding.textViewTemperatureLabel.animateTextSize(if (isFocused) 20f else 16f)
        }
    }
}