package com.medina.juanantonio.firemirror.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import coil.load
import com.medina.juanantonio.firemirror.R
import com.medina.juanantonio.firemirror.common.extensions.animateDimensions
import com.medina.juanantonio.firemirror.common.extensions.animateTextSize
import com.medina.juanantonio.firemirror.data.models.listdisplay.*
import com.medina.juanantonio.firemirror.databinding.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class ListDisplayView(
    context: Context,
    attrs: AttributeSet
) : ConstraintLayout(context, attrs) {

    companion object {
        private val imageDimensionsMap = mutableMapOf<Int, Pair<Int, Int>>()
    }

    private val binding = ViewListDisplayBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var title: String? = null
    private var itemList: ArrayList<ListDisplayItem> = arrayListOf()
    lateinit var adapter: ListDisplayAdapter
        private set

    fun initialize(
        title: String? = this.title,
        itemList: ArrayList<ListDisplayItem> = this.itemList,
        onClickAction: ((ListDisplayItem, ViewBinding) -> Unit)? = null,
        noBottomPadding: Boolean = false
    ): ListDisplayAdapter {
        this.title = title
        this.itemList = itemList

        id = Random.nextInt(100000, 999999)

        binding.textViewListTitle.text = title
        binding.textViewListTitle.textAlignment = this.textAlignment
        binding.textViewListTitle.isVisible = !title.isNullOrBlank()

        binding.viewListBottomShadow.isVisible = itemList.size > 6

        val adapter = ListDisplayAdapter(
            itemList,
            onClickAction = onClickAction,
            textAlignment = this.textAlignment,
            onFocusChanged = {
                binding.recyclerViewListDisplay.smoothScrollToPosition(
                    when {
                        it == itemList.size - 1 -> it + 1 // Last Item
                        it == 0 -> it // First Item
                        it < 6 -> 0 // First 5 Items
                        it > 0 -> it + 2
                        else -> it - 1
                    }
                )
            }
        )
        this.adapter = adapter

        binding.recyclerViewListDisplay.apply {
            if (noBottomPadding) updatePadding(bottom = 0)
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }

        return adapter
    }

    fun scrollToTop() {
        binding.recyclerViewListDisplay.smoothScrollToPosition(0)
    }

    inner class ListDisplayAdapter(
        private val itemList: ArrayList<ListDisplayItem>,
        private val onClickAction: ((ListDisplayItem, ViewBinding) -> Unit)? = { _, _ -> },
        private val textAlignment: Int = TEXT_ALIGNMENT_TEXT_START,
        private val onFocusChanged: ((Int) -> Unit) = {}
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        val viewList = mutableMapOf<Int, ViewBinding>()

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerView.ViewHolder {
            return when (viewType) {
                R.layout.item_list_display_default -> {
                    val binding = ItemListDisplayDefaultBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    DefaultListDisplayItemViewHolder(binding)
                }
                R.layout.item_list_display_image -> {
                    val binding = ItemListDisplayImageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    ImageListDisplayItemViewHolder(binding)
                }
                R.layout.item_list_display_icon_label -> {
                    val binding = ItemListDisplayIconLabelBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    IconLabelListDisplayItemViewHolder(binding)
                }
                else -> {
                    val binding = ItemListDisplaySpotifyBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    SpotifyListDisplayItemViewHolder(binding)
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            return itemList[position].viewType
        }

        override fun getItemCount() = itemList.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is DefaultListDisplayItemViewHolder -> {
                    holder.bind(position, itemList[position] as DefaultListDisplayItem)
                }
                is ImageListDisplayItemViewHolder -> {
                    holder.bind(position, itemList[position] as ImageListDisplayItem)
                }
                is IconLabelListDisplayItemViewHolder -> {
                    holder.bind(position, itemList[position] as IconLabelListDisplayItem)
                }
                is SpotifyListDisplayItemViewHolder -> {
                    holder.bind(position, itemList[position] as SpotifyListDisplayItem)
                }
            }
        }

        inner class DefaultListDisplayItemViewHolder(
            private val binding: ItemListDisplayDefaultBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(position: Int, item: DefaultListDisplayItem) {
                viewList[position] = binding

                binding.textViewLabel.text = item.label
                binding.textViewLabel.textAlignment = textAlignment
                binding.textViewLabel.setOnClickListener {
                    onClickAction?.invoke(item, binding)
                }

                binding.textViewLabel.onFocusChangeListener =
                    OnFocusChangeListener { view, hasFocus ->
                        (view as AppCompatTextView).animateTextSize(if (hasFocus) 20f else 16f)
                        onFocusChanged(position)
                    }
            }
        }

        inner class ImageListDisplayItemViewHolder(
            private val binding: ItemListDisplayImageBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(position: Int, item: ImageListDisplayItem) {
                viewList[position] = binding
                binding.imageView.apply {
                    id = Random.nextInt(100000, 999999)
                    item.imageUrl?.let {
                        load(it) {
                            listener(
                                onSuccess = { _, _ ->
                                    CoroutineScope(Dispatchers.Main).launch {
                                        delay(500)
                                        imageDimensionsMap[id] =
                                            Pair(measuredWidth, measuredHeight)
                                    }
                                }
                            )
                        }
                    }

                    item.drawable?.let {
                        setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                it,
                                null
                            )
                        )

                        postDelayed(500) {
                            imageDimensionsMap[id] = Pair(measuredWidth, measuredHeight)
                        }
                    }

                    val params = layoutParams as RelativeLayout.LayoutParams
                    when (this@ListDisplayAdapter.textAlignment) {
                        TEXT_ALIGNMENT_TEXT_START ->
                            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                        TEXT_ALIGNMENT_CENTER ->
                            params.addRule(RelativeLayout.CENTER_IN_PARENT)
                        TEXT_ALIGNMENT_TEXT_END ->
                            params.addRule(RelativeLayout.ALIGN_PARENT_END)
                    }
                    layoutParams = params

                    onFocusChangeListener =
                        OnFocusChangeListener { view, hasFocus ->
                            val imageDimensions =
                                imageDimensionsMap[id] ?: return@OnFocusChangeListener
                            (view as AppCompatImageView).animateDimensions(
                                originalViewDimensions = imageDimensions,
                                increasePercentage = if (hasFocus) .1f else 0f
                            )
                            onFocusChanged(position)
                        }

                    setOnClickListener {
                        onClickAction?.invoke(item, binding)
                    }
                }
            }
        }

        inner class IconLabelListDisplayItemViewHolder(
            private val binding: ItemListDisplayIconLabelBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(position: Int, item: IconLabelListDisplayItem) {
                viewList[position] = binding

                binding.textViewLabel.text = item.label
                binding.textViewLabel.textAlignment = textAlignment
                binding.textViewLabel.setOnClickListener {
                    onClickAction?.invoke(item, binding)
                }

                binding.textViewLabel.onFocusChangeListener =
                    OnFocusChangeListener { view, hasFocus ->
                        (view as AppCompatTextView).animateTextSize(if (hasFocus) 20f else 16f)
                        onFocusChanged(position)
                    }

                binding.imageViewIcon.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        item.drawable,
                        null
                    )
                )
            }
        }

        inner class SpotifyListDisplayItemViewHolder(
            private val binding: ItemListDisplaySpotifyBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(position: Int, item: SpotifyListDisplayItem) {
                viewList[position] = binding

                binding.viewSpotify.apply {
                    val params = layoutParams as RelativeLayout.LayoutParams
                    when (this@ListDisplayAdapter.textAlignment) {
                        TEXT_ALIGNMENT_TEXT_START ->
                            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                        TEXT_ALIGNMENT_CENTER ->
                            params.addRule(RelativeLayout.CENTER_IN_PARENT)
                        TEXT_ALIGNMENT_TEXT_END ->
                            params.addRule(RelativeLayout.ALIGN_PARENT_END)
                    }
                    layoutParams = params
                }

                binding.viewSpotify.spotifyLogo.apply {
                    id = Random.nextInt(100000, 999999)
                    postDelayed(500) {
                        imageDimensionsMap[id] = Pair(measuredWidth, measuredHeight)

                        onFocusChangeListener =
                            OnFocusChangeListener { view, hasFocus ->
                                val imageDimensions =
                                    imageDimensionsMap[id] ?: return@OnFocusChangeListener
                                (view as AppCompatImageView).animateDimensions(
                                    originalViewDimensions = imageDimensions,
                                    increasePercentage = if (hasFocus) .1f else 0f
                                )
                                onFocusChanged(position)
                            }
                    }

                    setOnClickListener {
                        onClickAction?.invoke(item, binding)
                    }
                }

                binding.viewSpotify.imageView.apply {
                    id = Random.nextInt(100000, 999999)

                    postDelayed(500) {
                        val dimensionPair = Pair(measuredWidth, measuredHeight)
                        imageDimensionsMap[id] = dimensionPair
                        binding.viewSpotify.textBackground.animateDimensions(
                            originalViewDimensions = dimensionPair,
                            increasePercentage = 0f
                        )

                        onFocusChangeListener =
                            OnFocusChangeListener { view, hasFocus ->
                                val imageDimensions =
                                    imageDimensionsMap[id] ?: return@OnFocusChangeListener
                                (view as AppCompatImageView).animateDimensions(
                                    originalViewDimensions = imageDimensions,
                                    increasePercentage = if (hasFocus) .1f else 0f
                                )
                                binding.viewSpotify.textBackground.animateDimensions(
                                    originalViewDimensions = imageDimensions,
                                    increasePercentage = if (hasFocus) .1f else 0f
                                )
                                onFocusChanged(position)
                            }
                    }

                    setOnClickListener {
                        onClickAction?.invoke(item, binding)
                    }
                }
            }
        }
    }
}