package audio.util

import audio.models.Album
import audio.models.Composition
import audio.models.Customer
import audio.payload.AlbumPayload
import audio.payload.CompositionPayload
import audio.payload.CustomerProfilePayload

fun List<Album>.toAlbumsPayload() = this.mapTo(ArrayList()) {
    it.toAlbumPayload()
}

fun Album.toAlbumPayload() = AlbumPayload(
        id = this.id,
        title = this.title,
        cover = this.cover,
        year = this.year,
        rating = this.rating,
        price = this.compositions.sumBy { it.price },
        tracksCount = this.compositions.size,
        authorName = if (this.authors.isEmpty()) "" else this.authors.first().name,
        genre = if (this.compositions.flatMap { it.genres }.isEmpty())
            "" else this.compositions.flatMap { it.genres }.first().name
)

fun List<Composition>.toCompositionsPayload() = this.mapTo(ArrayList()) {
    it.toCompositionPayload()
}

fun Composition.toCompositionPayload() = CompositionPayload(
        id = this.id,
        title = this.title,
        duration = this.duration,
        rating = this.rating,
        text = this.text,
        price = this.price,
        cover = this.cover,
        authorName = if (this.authors.isEmpty()) "" else this.authors.first().name,
        genre = if (this.genres.isEmpty()) "" else this.genres.first().name
)

fun Customer.toCustomerProfilePayload() = CustomerProfilePayload(
        name, email, phone, balance, favoriteCompositions.toList()
)