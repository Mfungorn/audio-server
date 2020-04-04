package audio.models

import audio.repositories.AlbumEntityResolver
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import javax.persistence.*

@Entity
@Table(name = "album")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator::class,
        property = "id",
        scope = Album::class,
        resolver = AlbumEntityResolver::class)
data class Album(
        @Column(name = "title")
        val title: String,

        @Column(name = "cover")
        var cover: String = "",

        @Column(name = "year")
        val year: Int? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_id")
    val id: Long? = null

    @Column(name = "rating")
    val rating: Int = 0


    val price: Int
        get() = compositions.sumBy { it.price }

    //@JsonIgnore
    @JsonIgnoreProperties("albums", "compositions", "genres")
    @Column(name = "authors")
    @ManyToMany(cascade = [
        CascadeType.MERGE,
        CascadeType.REFRESH
    ])
    @JoinTable(
            name = "album_author",
            joinColumns = [JoinColumn(name = "album_id")],
            inverseJoinColumns = [JoinColumn(name = "author_id")])
    val authors: MutableSet<Author> = mutableSetOf()

    //@JsonIgnore
    @JsonIgnoreProperties("albums", "authors", "genres")
    @Column(name = "compositions")
    @ManyToMany(mappedBy = "albums")
    val compositions: MutableSet<Composition> = mutableSetOf()

    // transient
    val genres: Set<Genre>
        get() = compositions.flatMapTo(mutableSetOf()) { it.genres }

    fun addAuthor(author: Author) {
        authors.add(author)
        author.albums.add(this)
    }

    fun addComposition(composition: Composition) {
        compositions.add(composition)
        composition.albums.add(this)
    }

    override fun toString(): String {
        return "Album $title"
    }
}
