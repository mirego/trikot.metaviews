package com.mirego.trikot.metaviews.text

import com.mirego.trikot.metaviews.properties.MetaAction
import org.reactivestreams.Publisher

data class RichText(val text: String, val ranges: List<RichTextRange>)

data class RichTextRange(val range: IntRange, val transform: RichTextTransform)

sealed class RichTextTransform

data class StyleTransform(val style: Style) : RichTextTransform() {
    enum class Style {
        NORMAL,
        BOLD,
        ITALIC,
        BOLD_ITALIC,
        UNDERLINE
    }
}

data class ActionTransform(val action: Publisher<MetaAction>) : RichTextTransform()
