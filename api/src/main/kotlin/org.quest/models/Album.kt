package org.quest.models

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
@Table(name = "album")
data class Album(
        @Column(name = "title")
        val title: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_id")
    val id: Long? = null

    @Column(name = "cover")
    var cover: String = ""

    @Column(name = "rating")
    val rating: Int = 0

    @Column(name = "price")
    var price: Int = 0

    @JsonIgnore
    @Column(name = "authors")
    @ManyToMany(cascade = [
        CascadeType.PERSIST,
        CascadeType.MERGE
    ])
    @JoinTable(
            name = "album_author",
            joinColumns = [JoinColumn(name = "album_id")],
            inverseJoinColumns = [JoinColumn(name = "author_id")])
    val authors: MutableSet<Author> = mutableSetOf()

    @JsonIgnore
    @Column(name = "compositions")
    @ManyToMany(mappedBy = "albums")
    val compositions: MutableSet<Composition> = mutableSetOf()

    @JsonIgnore
    @Column(name = "genres")
    @ManyToMany(mappedBy = "albums")
    val genres: MutableSet<Genre> = mutableSetOf()

    fun addAuthor(author: Author) {
        authors.add(author)
        author.albums.add(this)
    }

    fun addComposition(composition: Composition) {
        compositions.add(composition)
        composition.albums.add(this)
    }
}
