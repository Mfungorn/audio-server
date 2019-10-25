package audio.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import javax.persistence.*

@Entity
@Table(name = "genre")
data class Genre(
    @Column(name = "name", unique = true)
    val name: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genre_id")
    val id: Long? = null

    //@JsonIgnore
    @JsonIgnoreProperties("genres")
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
}