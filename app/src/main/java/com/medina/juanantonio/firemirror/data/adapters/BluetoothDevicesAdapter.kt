package com.medina.juanantonio.firemirror.data.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.medina.juanantonio.firemirror.R
import com.medina.juanantonio.firemirror.common.extensions.animateTextSize
import com.medina.juanantonio.firemirror.data.models.BleDevice
import com.medina.juanantonio.firemirror.data.models.BlueButtDevice
import com.medina.juanantonio.firemirror.databinding.ItemBluetoothDeviceBinding

class BluetoothDevicesAdapter(
    private val listener: BleDeviceListener
) : ListAdapter<BleDevice, BluetoothDevicesAdapter.BleDeviceViewHolder>(
    object : DiffUtil.ItemCallback<BleDevice>() {
        override fun areItemsTheSame(
            oldItem: BleDevice,
            newItem: BleDevice
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: BleDevice,
            newItem: BleDevice
        ): Boolean {
            return false
        }
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleDeviceViewHolder {
        val binding = ItemBluetoothDeviceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return BleDeviceViewHolder(binding)
    }

    override fun getItemCount() = currentList.size

    override fun onBindViewHolder(holder: BleDeviceViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class BleDeviceViewHolder(
        private val binding: ItemBluetoothDeviceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BleDevice) {
            binding.textViewDeviceName.text = item.getDeviceName()
            binding.textViewDeviceAddress.text = item.macAddress

            binding.textViewDeviceClickCount.isVisible = item is BlueButtDevice
            if (item is BlueButtDevice) {
                binding.textViewDeviceClickCount.text =
                    binding.root.context.getString(R.string.click_count_format, item.clickCount)
            }

            val drawable =
                if (item.isConnected) R.drawable.ic_bluetooth_connected
                else R.drawable.ic_bluetooth

            binding.imageViewBluetoothStatus.setImageDrawable(
                ResourcesCompat.getDrawable(
                    binding.root.context.resources,
                    drawable,
                    null
                )
            )

            binding.progressBarBluetoothLoading.isVisible = item.isDeviceLoading

            binding.root.setOnClickListener {
                if (item.isDeviceLoading) return@setOnClickListener
                listener.onDeviceClicked(item)
            }

            binding.root.setOnLongClickListener {
                if (item.isDeviceLoading) return@setOnLongClickListener true
                listener.onDeviceLongClicked(item)

                return@setOnLongClickListener false
            }

            binding.root.onFocusChangeListener =
                View.OnFocusChangeListener { _, hasFocus ->
                    binding.textViewDeviceName.animateTextSize(if (hasFocus) 22f else 16f)
                }
        }
    }

    interface BleDeviceListener {
        fun onDeviceClicked(item: BleDevice)
        fun onDeviceLongClicked(item: BleDevice)
    }
}
