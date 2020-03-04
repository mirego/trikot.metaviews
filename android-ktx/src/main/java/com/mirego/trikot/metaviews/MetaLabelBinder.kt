package com.mirego.trikot.metaviews

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.core.view.ViewCompat
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
            textView.text = richText.asSpannableString(lifecycleOwnerWrapper)
            if (richText.ranges.any { it.transform is ClickableSpan }) {
                textView.applyLinkMovementMethod()
            }
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

        metaLabel.backgroundColor.asLiveData()
            .observe(lifecycleOwnerWrapper.lifecycleOwner) { selector ->
                if (!selector.hasAnyValue) {
                    return@observe
                }

                textView.background ?: run {
                    ViewCompat.setBackground(textView, ColorDrawable(Color.WHITE))
                }

                ViewCompat.setBackgroundTintList(textView, selector.toColorStateList())
            }

        textView.bindOnTap(metaLabel, lifecycleOwnerWrapper)
    }
}

enum class HiddenVisibility(val value: Int) {
    GONE(View.GONE),
    INVISIBLE(View.INVISIBLE);
}

private fun TextView.applyLinkMovementMethod() {
    if (movementMethod == null && movementMethod !is LinkMovementMethod) {
        movementMethod = LinkMovementMethod.getInstance()
    }
}
