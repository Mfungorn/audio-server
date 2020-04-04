package audio.payload

data class AlbumPayload(
        val id: Long?,
        val title: String,
        val cover: String,
        val year: Int?,
        val rating: Int,
        val price: Int = 0,
        val authorName: String,
        val tracksCount: Int,
        val genre: String
)