package com.mirego.trikot.metaview.resource

import com.mirego.trikot.streams.concurrent.freeze

interface ImageResource {
    companion object {
        val None = freeze(NoImageResource() as ImageResource)
    }
}

class NoImageResource : ImageResource
