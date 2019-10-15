package org.quest.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import javax.persistence.*

@Entity
@Table(name = "composition")
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
    @JsonIgnoreProperties("compositions")
    @Column(name = "albums")
    @ManyToMany(cascade = [
        CascadeType.PERSIST,
        CascadeType.MERGE
    ])
    @JoinTable(
            name = "composition_album",
            joinColumns = [JoinColumn(name = "composition_id")],
            inverseJoinColumns = [JoinColumn(name = "album_id")])
    val albums: MutableSet<Album> = mutableSetOf()

//    @JsonIgnore
    @JsonIgnoreProperties("compositions")
    @Column(name = "authors")
    @ManyToMany(mappedBy = "compositions")
    val authors: MutableSet<Author> = mutableSetOf()

    //@JsonIgnore
    @JsonIgnoreProperties("compositions")
    @Column(name = "genres")
    @ManyToMany(mappedBy = "compositions")
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
}