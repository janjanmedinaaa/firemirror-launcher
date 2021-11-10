package com.medina.juanantonio.firemirror.common.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.medina.juanantonio.firemirror.R
import com.medina.juanantonio.firemirror.common.utils.autoCleared
import com.medina.juanantonio.firemirror.data.adapters.LabelValueListAdapter
import com.medina.juanantonio.firemirror.data.models.LabelValue
import com.medina.juanantonio.firemirror.features.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.medina.juanantonio.firemirror.databinding.DialogListLabelValueBinding

@AndroidEntryPoint
class LabelValueListDialog :
    DialogFragment() {

    companion object {
        var labelValueList = arrayListOf<LabelValue>()
        var labelValueListener: LabelValueListAdapter.LabelValueListener? = null
    }

    private var binding: DialogListLabelValueBinding by autoCleared()
    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var labelValueListAdapter: LabelValueListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogListLabelValueBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.currentScreenLayout = R.layout.dialog_list_label_value
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        labelValueListAdapter = LabelValueListAdapter(labelValueList, labelValueListener)
        binding.recyclerView.apply {
            adapter = labelValueListAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
    }
}