package audio.models

import audio.repositories.CompositionEntityResolver
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import javax.persistence.*

@Entity
@Table(name = "composition")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator::class,
        property = "id",
        scope = Composition::class,
        resolver = CompositionEntityResolver::class)
data class Composition(
        @Column(name = "title")
        val title: String,

        @Column(name = "duration")
        val duration: Int,

        @Column(name = "text")
        val text: String,

        @Column(name = "price")
        val price: Int,

        @Column(name = "cover")
        val cover: String = ""
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "composition_id")
    val id: Long? = null

    @Column(name = "rating")
    var rating: Int = 0

    //@JsonIgnore
    @JsonIgnoreProperties("compositions", "authors", "genres")
    @Column(name = "albums")
    @ManyToMany(cascade = [
        CascadeType.MERGE,
        CascadeType.REFRESH
    ])
    @JoinTable(
            name = "composition_album",
            joinColumns = [JoinColumn(name = "composition_id")],
            inverseJoinColumns = [JoinColumn(name = "album_id")])
    val albums: MutableSet<Album> = mutableSetOf()

    //    @JsonIgnore
//    @JsonIgnoreProperties("compositions")
//    @Column(name = "authors")
//    @ManyToMany(mappedBy = "compositions")
//    val authors: MutableSet<Author> = mutableSetOf()
    @JsonIgnoreProperties("compositions", "albums", "genres")
    @Column(name = "authors")
    @ManyToMany(cascade = [
        CascadeType.MERGE,
        CascadeType.REFRESH
    ])
    @JoinTable(
            name = "composition_author",
            joinColumns = [JoinColumn(name = "composition_id")],
            inverseJoinColumns = [JoinColumn(name = "author_id")])
    val authors: MutableSet<Author> = mutableSetOf()

    //@JsonIgnore
    @JsonIgnoreProperties("compositions", "authors", "albums")
    @Column(name = "genres")
    @ManyToMany(cascade = [
        CascadeType.MERGE,
        CascadeType.REFRESH
    ])
    @JoinTable(
            name = "composition_genre",
            joinColumns = [JoinColumn(name = "composition_id")],
            inverseJoinColumns = [JoinColumn(name = "genre_id")])
    val genres: MutableSet<Genre> = mutableSetOf()

    @JsonIgnore
    @Column(name = "customers")
    @ManyToMany(mappedBy = "favoriteCompositions")
    val customers: MutableSet<Customer> = mutableSetOf()

    fun addAuthor(author: Author) {
        authors.add(author)
        author.compositions.add(this)
    }

    fun addAlbum(album: Album) {
        albums.add(album)
        album.compositions.add(this)
    }

    fun addGenre(genre: Genre) {
        genres.add(genre)
        genre.compositions.add(this)
    }

    fun addCustomer(customer: Customer) {
        customers.add(customer)
        customer.favoriteCompositions.add(this)
    }

    override fun toString(): String {
        return "Track $title"
    }
}