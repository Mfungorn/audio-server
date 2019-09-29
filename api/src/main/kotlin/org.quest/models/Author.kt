package org.quest.models

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
@Table(name = "author")
data class Author(
        @Column(name = "name")
        var name: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "author_id")
    val id: Long? = null

    @Column(name = "logo")
    var logo: String = ""

    @Column(name = "rating")
    var rating: Int = 0

    @JsonIgnore
    @Column(name = "albums")
    @ManyToMany(mappedBy = "authors")
    val albums: MutableSet<Album> = mutableSetOf()

    @JsonIgnore
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
    @Column(name = "genres")
    @ManyToMany(mappedBy = "authors")
    val genres: MutableSet<Genre> = mutableSetOf()
}