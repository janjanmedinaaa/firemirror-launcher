package com.medina.juanantonio.firemirror.common.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import coil.load
import com.medina.juanantonio.firemirror.R
import com.medina.juanantonio.firemirror.common.utils.autoCleared
import com.medina.juanantonio.firemirror.databinding.DialogImageViewerBinding
import com.medina.juanantonio.firemirror.features.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageViewerDialog : DialogFragment() {

    companion object {
        const val IMAGE_URL_ARG = "imageUrlArg"
    }

    private var binding: DialogImageViewerBinding by autoCleared()
    private val mainViewModel: MainViewModel by activityViewModels()

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

    override fun onResume() {
        super.onResume()
        mainViewModel.currentScreenLayout = R.layout.dialog_image_viewer
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding.imageViewDisplay.load(arguments?.getString(IMAGE_URL_ARG))
    }
}