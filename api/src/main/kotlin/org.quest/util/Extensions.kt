package org.quest.util

import org.quest.models.Album
import org.quest.models.Composition
import org.quest.payload.AlbumPayload
import org.quest.payload.CompositionPayload

fun List<Album>.mapToAlbumPayloadList() = this.mapTo(ArrayList()) {
    it.mapToAlbumPayload()
}

fun Album.mapToAlbumPayload() = AlbumPayload(
        id = this.id,
        title = this.title,
        cover = this.cover,
        rating = this.rating,
        price = this.price,
        tracksCount = this.compositions.size,
        authorName = this.authors.first().name,
        genre = this.genres.first().name
)

fun List<Composition>.mapToCompositionPayloadList() = this.mapTo(ArrayList()) {
    it.mapToCompositionPayload()
}

fun Composition.mapToCompositionPayload() = CompositionPayload(
        id = this.id,
        title = this.title,
        duration = this.duration,
        rating = this.rating,
        text = this.text,
        price = this.price,
        cover = this.cover,
        authorName = this.authors.first().name,
        genre = this.genres.first().name
)