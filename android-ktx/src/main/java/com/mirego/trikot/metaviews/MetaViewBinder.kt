package com.mirego.trikot.metaviews

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.core.view.ViewCompat
import androidx.databinding.BindingAdapter
import com.mirego.trikot.metaviews.mutable.MutableMetaView
import com.mirego.trikot.metaviews.properties.MetaAction
import com.mirego.trikot.streams.android.ktx.asLiveData
import com.mirego.trikot.streams.android.ktx.observe
import com.mirego.trikot.streams.reactive.just

private val NoMetaView = MutableMetaView()
    .apply { hidden = true.just() } as MetaView

@BindingAdapter("meta_view", "lifecycleOwnerWrapper")
fun View.bindMetaView(
    metaView: MetaView?,
    lifecycleOwnerWrapper: LifecycleOwnerWrapper
) {
    (metaView ?: NoMetaView).let {
        it.hidden.observe(lifecycleOwnerWrapper.lifecycleOwner) { isHidden ->
            visibility = if (isHidden) View.GONE else View.VISIBLE
        }

        it.alpha.observe(lifecycleOwnerWrapper.lifecycleOwner) { alpha ->
            setAlpha(alpha)
        }

        bindOnTap(it, lifecycleOwnerWrapper)

        it.backgroundColor.asLiveData()
            .observe(lifecycleOwnerWrapper.lifecycleOwner) { selector ->
                if (!selector.hasAnyValue) {
                    return@observe
                }

                background ?: run {
                    ViewCompat.setBackground(this, ColorDrawable(Color.WHITE))
                }

                ViewCompat.setBackgroundTintList(this, selector.toColorStateList())
            }
    }
}

fun View.bindOnTap(metaView: MetaView, lifecycleOwnerWrapper: LifecycleOwnerWrapper) {
    metaView.onTap.observe(lifecycleOwnerWrapper.lifecycleOwner) { action ->
        when (action) {
            MetaAction.None -> {
                setOnClickListener(null)
                isClickable = false
            }
            else -> setOnClickListener { view ->
                with(view) {
                    isClickable = false
                    postDelayed({ isClickable = true }, NEXT_CLICK_THRESHOLD)
                    action.execute(this)
                }
            }
        }
    }
}

const val NEXT_CLICK_THRESHOLD = 200L
