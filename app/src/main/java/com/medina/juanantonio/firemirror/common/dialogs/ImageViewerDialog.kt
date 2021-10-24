package com.medina.juanantonio.firemirror.common.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import coil.load
import com.medina.juanantonio.firemirror.common.utils.autoCleared
import com.medina.juanantonio.firemirror.databinding.DialogImageViewerBinding

class ImageViewerDialog : DialogFragment() {

    companion object {
        const val IMAGE_URL_ARG = "imageUrlArg"
    }

    private var binding: DialogImageViewerBinding by autoCleared()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogImageViewerBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding.imageViewDisplay.load(arguments?.getString(IMAGE_URL_ARG))
    }
}