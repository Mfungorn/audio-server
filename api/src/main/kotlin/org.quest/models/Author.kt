package org.quest.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import javax.persistence.*

@Entity
@Table(name = "author")
data class Author(
        @Column(name = "name")
        var name: String,

        @Column(name = "bio")
        var bio: String = "No bio"
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "author_id")
    val id: Long? = null

    @Column(name = "logo")
    var logo: String = ""

    @Column(name = "rating")
    var rating: Int = 0

    //@JsonIgnore
    @JsonIgnoreProperties("authors")
    @Column(name = "albums")
    @ManyToMany(mappedBy = "authors")
    val albums: MutableSet<Album> = mutableSetOf()

    //@JsonIgnore
    @JsonIgnoreProperties("authors")
    @Column(name = "compositions")
    @ManyToMany(cascade = [
        CascadeType.PERSIST,
        CascadeType.MERGE
    ])
    @JoinTable(
            name = "author_composition",
            joinColumns = [JoinColumn(name = "author_id")],
            inverseJoinColumns = [JoinColumn(name = "composition_id")])
    val compositions: MutableSet<Composition> = mutableSetOf()

    @JsonIgnore
    @Column(name = "customers")
    @ManyToMany(mappedBy = "favoriteAuthors")
    val customers: MutableSet<Customer> = mutableSetOf()

    fun addAlbum(album: Album) {
        albums.add(album)
        album.authors.add(this)
    }

    fun addComposition(composition: Composition) {
        compositions.add(composition)
        composition.authors.add(this)
    }

    fun addCustomer(customer: Customer) {
        customers.add(customer)
        customer.favoriteAuthors.add(this)
    }
}