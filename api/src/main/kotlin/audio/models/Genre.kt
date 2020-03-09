package audio.models

import audio.repositories.GenreEntityResolver
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import javax.persistence.*

@Entity
@Table(name = "genre")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator::class,
        property = "id",
        scope = Genre::class,
        resolver = GenreEntityResolver::class)
data class Genre(
    @Column(name = "name", unique = true)
    val name: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genre_id")
    val id: Long? = null

    //@JsonIgnore
//    @JsonIgnoreProperties("genres")
//    @Column(name = "compositions")
//    @ManyToMany(cascade = [
//        CascadeType.PERSIST,
//        CascadeType.MERGE
//    ])
//    @JoinTable(
//            name = "genre_composition",
//            joinColumns = [JoinColumn(name = "genre_id")],
//            inverseJoinColumns = [JoinColumn(name = "composition_id")])
//    val compositions: MutableSet<Composition> = mutableSetOf()
    @JsonIgnoreProperties("genres", "authors", "albums")
    @Column(name = "compositions")
    @ManyToMany(mappedBy = "genres")
    val compositions: MutableSet<Composition> = mutableSetOf()

    override fun toString(): String {
        return name
    }
}