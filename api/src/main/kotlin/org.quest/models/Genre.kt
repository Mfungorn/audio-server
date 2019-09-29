package org.quest.models

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
@Table(name = "genre")
data class Genre(
    @Column(name = "name")
    val name: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genre_id")
    val id: Long? = null

    @JsonIgnore
    @Column(name = "compositions")
    @ManyToMany(cascade = [
        CascadeType.PERSIST,
        CascadeType.MERGE
    ])
    @JoinTable(
            name = "genre_composition",
            joinColumns = [JoinColumn(name = "genre_id")],
            inverseJoinColumns = [JoinColumn(name = "composition_id")])
    val compositions: MutableSet<Composition> = mutableSetOf()

    @JsonIgnore
    @Column(name = "authors")
    @ManyToMany(cascade = [
        CascadeType.PERSIST,
        CascadeType.MERGE
    ])
    @JoinTable(
            name = "genre_author",
            joinColumns = [JoinColumn(name = "genre_id")],
            inverseJoinColumns = [JoinColumn(name = "author_id")])
    val authors: MutableSet<Author> = mutableSetOf()

    @JsonIgnore
    @Column(name = "albums")
    @ManyToMany(cascade = [
        CascadeType.PERSIST,
        CascadeType.MERGE
    ])
    @JoinTable(
            name = "genre_album",
            joinColumns = [JoinColumn(name = "genre_id")],
            inverseJoinColumns = [JoinColumn(name = "album_id")])
    val albums: MutableSet<Album> = mutableSetOf()
}