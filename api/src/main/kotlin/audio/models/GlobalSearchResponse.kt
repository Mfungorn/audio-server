package audio.models

data class GlobalSearchResponse(
        val authorResults: List<Author>,
        val albumResults: List<Album>,
        val compositionResults: List<Composition>,
        val genreResults: List<Genre>
)