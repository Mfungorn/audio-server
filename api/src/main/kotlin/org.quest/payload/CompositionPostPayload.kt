package org.quest.payload

data class CompositionPostPayload(
        val title: String,
        val duration: Int,
        val text: String,
        val price: Int,
        val cover: String = ""
)