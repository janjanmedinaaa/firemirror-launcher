package com.medina.juanantonio.firemirror.data.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medina.juanantonio.firemirror.common.extensions.animateTextSize
import com.medina.juanantonio.firemirror.data.models.LabelValue
import com.medina.juanantonio.firemirror.databinding.ItemListLabelValueBinding

class LabelValueListAdapter(
    private val list: ArrayList<LabelValue>,
    private val listener: LabelValueListener?
) : RecyclerView.Adapter<LabelValueListAdapter.LabelValueViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelValueViewHolder {
        val binding = ItemListLabelValueBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return LabelValueViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: LabelValueViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class LabelValueViewHolder(
        private val binding: ItemListLabelValueBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LabelValue) {
            binding.textViewLabel.text = item.label

            binding.root.onFocusChangeListener =
                View.OnFocusChangeListener { _, hasFocus ->
                    binding.textViewLabel.animateTextSize(if (hasFocus) 22f else 16f)
                }

            binding.root.setOnClickListener {
                listener?.onItemClicked(item.value)
            }
        }
    }

    interface LabelValueListener {
        fun onItemClicked(item: Any)
    }
}
