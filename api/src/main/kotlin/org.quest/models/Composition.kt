package org.quest.models

import com.fasterxml.jackson.annotation.JsonIgnore
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
    val price: Int
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "composition_id")
    val id: Long? = null

    @Column(name = "rating")
    val rating: Int = 0

    @JsonIgnore
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

    @JsonIgnore
    @Column(name = "authors")
    @ManyToMany(mappedBy = "compositions")
    val authors: MutableSet<Author> = mutableSetOf()

    @JsonIgnore
    @Column(name = "genres")
    @ManyToMany(mappedBy = "compositions")
    val genres: MutableSet<Genre> = mutableSetOf()
}