package audio.models

data class Favorites(
        val favoriteAuthors: Set<Author>,
        val favoriteCompositions: Set<Composition>
)