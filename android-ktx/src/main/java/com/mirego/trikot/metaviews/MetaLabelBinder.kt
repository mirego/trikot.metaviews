package com.mirego.trikot.metaviews

import android.R
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.mirego.trikot.metaviews.mutable.MutableMetaLabel
import com.mirego.trikot.streams.android.ktx.asLiveData
import com.mirego.trikot.streams.android.ktx.observe
import com.mirego.trikot.streams.reactive.just

object MetaLabelBinder {

    private val NoMetaLabel = MutableMetaLabel().apply { hidden = true.just() } as MetaLabel

    @JvmStatic
    @BindingAdapter("meta_view", "hiddenVisibility", "lifecycleOwnerWrapper")
    fun bind(
        textView: TextView,
        metaLabel: MetaLabel?,
        hiddenVisibility: HiddenVisibility,
        lifecycleOwnerWrapper: LifecycleOwnerWrapper
    ) {
        val label = metaLabel ?: NoMetaLabel

        label.richText?.observe(lifecycleOwnerWrapper.lifecycleOwner) { richText ->
            textView.text = richText.asSpannableString()
        }

        label.takeUnless { it.richText != null }?.text
            ?.observe(lifecycleOwnerWrapper.lifecycleOwner) {
                textView.text = it
            }

        label.textColor.asLiveData()
            .observe(lifecycleOwnerWrapper.lifecycleOwner) { selector ->
                selector.default?.let {
                    textView.setTextColor(it.toIntColor())
                }
            }

        label.backgroundColor.asLiveData()
            .observe(lifecycleOwnerWrapper.lifecycleOwner) { selector ->
                if (!selector.hasAnyValue) {
                    return@observe
                }

                val defaultColor =
                    Color.parseColor(selector.default?.hexARGB("#") ?: "#00000000")
                val hoveredColor =
                    selector.highlighted?.let { Color.parseColor(it.hexARGB("#")) }
                        ?: defaultColor
                val selectedColor =
                    selector.selected?.let { Color.parseColor(it.hexARGB("#")) }
                        ?: defaultColor
                val disabledColor =
                    selector.disabled?.let { Color.parseColor(it.hexARGB("#")) }
                        ?: defaultColor
                textView.backgroundTintList = ColorStateList(
                    arrayOf(
                        intArrayOf(R.attr.state_enabled),
                        intArrayOf(R.attr.state_hovered),
                        intArrayOf(R.attr.state_selected),
                        intArrayOf(-R.attr.state_enabled)
                    ),
                    intArrayOf(defaultColor, hoveredColor, selectedColor, disabledColor)
                )
            }

        bindExtraViewProperties(textView, label, hiddenVisibility, lifecycleOwnerWrapper)
    }

    @JvmStatic
    @BindingAdapter("meta_view", "lifecycleOwnerWrapper")
    fun bind(
        textView: TextView,
        metaLabel: MetaLabel?,
        lifecycleOwnerWrapper: LifecycleOwnerWrapper
    ) {
        bind(textView, metaLabel, HiddenVisibility.GONE, lifecycleOwnerWrapper)
    }

    @JvmStatic
    fun bindWithoutTextPublishers(
        textView: TextView,
        metaLabel: MetaLabel?,
        lifecycleOwnerWrapper: LifecycleOwnerWrapper
    ) {
        bindExtraViewProperties(
            textView, metaLabel ?: NoMetaLabel, HiddenVisibility.GONE, lifecycleOwnerWrapper
        )
    }

    @JvmStatic
    private fun bindExtraViewProperties(
        textView: TextView,
        metaLabel: MetaLabel,
        hiddenVisibility: HiddenVisibility,
        lifecycleOwnerWrapper: LifecycleOwnerWrapper
    ) {
        metaLabel.hidden.observe(lifecycleOwnerWrapper.lifecycleOwner) { hidden ->
            with(textView) { visibility = if (hidden) hiddenVisibility.value else View.VISIBLE }
        }

        metaLabel.alpha.observe(lifecycleOwnerWrapper.lifecycleOwner) { alpha ->
            textView.alpha = alpha
        }

        textView.bindOnTap(metaLabel, lifecycleOwnerWrapper)
    }
}

enum class HiddenVisibility(val value: Int) {
    GONE(View.GONE),
    INVISIBLE(View.INVISIBLE);
}

