package com.medina.juanantonio.firemirror.data.managers

import androidx.viewbinding.ViewBinding
import com.medina.juanantonio.firemirror.common.views.ListDisplayView
import com.medina.juanantonio.firemirror.databinding.ItemListDisplayDefaultBinding
import com.medina.juanantonio.firemirror.databinding.ItemListDisplayIconLabelBinding
import com.medina.juanantonio.firemirror.databinding.ItemListDisplayImageBinding

class FocusManager : IFocusManager {
    private val viewFocusList: MutableMap<Int, ListDisplayView> = mutableMapOf()
    private var currentFocusIndex = -1
    private var currentViewFocusedIndex = 0

    override fun setupViewFocusList(list: ArrayList<ListDisplayView>) {
        viewFocusList.clear()
        list.forEachIndexed { index, listDisplayView ->
            viewFocusList[index] = listDisplayView
        }
    }

    override fun focusItem(binding: ViewBinding) {
        when (binding) {
            is ItemListDisplayDefaultBinding -> {
                binding.textViewLabel.isFocusable = true
                binding.textViewLabel.requestFocus()
            }
            is ItemListDisplayIconLabelBinding -> {
                binding.textViewLabel.isFocusable = true
                binding.textViewLabel.requestFocus()
            }
            is ItemListDisplayImageBinding -> {
                binding.imageView.isFocusable = true
                binding.imageView.requestFocus()
            }
        }
    }

    override fun focusPreviousItem() {
        var nextPossibleFocusIndex = currentFocusIndex - 1
        if (nextPossibleFocusIndex < 0) {
            val previousCurrentViewFocusedIndex = currentViewFocusedIndex
            if (currentViewFocusedIndex == 0)
                currentViewFocusedIndex = viewFocusList.keys.size - 1
            else currentViewFocusedIndex -= 1

            nextPossibleFocusIndex =
                viewFocusList[currentViewFocusedIndex]?.adapter?.viewList?.keys?.size?.minus(1) ?: -1

            viewFocusList[previousCurrentViewFocusedIndex]?.scrollToTop()
        }
        currentFocusIndex = nextPossibleFocusIndex

        val binding =
            viewFocusList[currentViewFocusedIndex]?.adapter?.viewList?.get(currentFocusIndex)
                ?: return

        focusItem(binding)
    }

    override fun focusNextItem() {
        var nextPossibleFocusIndex = currentFocusIndex + 1
        val currentListMaxIndex =
            viewFocusList[currentViewFocusedIndex]?.adapter?.viewList?.keys?.size?.minus(1)
        if (nextPossibleFocusIndex > currentListMaxIndex ?: -1) {
            nextPossibleFocusIndex = 0

            val previousCurrentViewFocusedIndex = currentViewFocusedIndex
            if (currentViewFocusedIndex == viewFocusList.keys.size - 1)
                currentViewFocusedIndex = 0
            else currentViewFocusedIndex += 1

            viewFocusList[previousCurrentViewFocusedIndex]?.scrollToTop()
        }
        currentFocusIndex = nextPossibleFocusIndex

        val binding =
            viewFocusList[currentViewFocusedIndex]?.adapter?.viewList?.get(currentFocusIndex)
                ?: return

        focusItem(binding)
    }

    override fun focusPreviousList() {
        val previousCurrentViewFocusedIndex = currentViewFocusedIndex
        if (currentViewFocusedIndex == 0)
            currentViewFocusedIndex = viewFocusList.keys.size - 1
        else currentViewFocusedIndex -= 1

        viewFocusList[previousCurrentViewFocusedIndex]?.scrollToTop()
        currentFocusIndex = 0

        val binding =
            viewFocusList[currentViewFocusedIndex]?.adapter?.viewList?.get(currentFocusIndex)
                ?: return

        focusItem(binding)
    }

    override fun focusNextList() {
        val previousCurrentViewFocusedIndex = currentViewFocusedIndex
        if (currentViewFocusedIndex == viewFocusList.keys.size - 1)
            currentViewFocusedIndex = 0
        else currentViewFocusedIndex += 1

        viewFocusList[previousCurrentViewFocusedIndex]?.scrollToTop()
        currentFocusIndex = 0

        val binding =
            viewFocusList[currentViewFocusedIndex]?.adapter?.viewList?.get(currentFocusIndex)
                ?: return

        focusItem(binding)
    }
}

interface IFocusManager {
    fun setupViewFocusList(list: ArrayList<ListDisplayView>)
    fun focusItem(binding: ViewBinding)
    fun focusPreviousItem()
    fun focusNextItem()
    fun focusPreviousList()
    fun focusNextList()
}