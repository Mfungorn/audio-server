package org.quest.controllers

import org.quest.exception.ResourceNotFoundException
import org.quest.models.Album
import org.quest.models.Author
import org.quest.models.Composition
import org.quest.models.Genre
import org.quest.repositories.AlbumRepository
import org.quest.repositories.AuthorRepository
import org.quest.repositories.CompositionRepository
import org.quest.repositories.GenreRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/authors")
class AuthorController {

    private val log = LoggerFactory.getLogger("AuthorController")

    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @Autowired
    private lateinit var albumRepository: AlbumRepository

    @Autowired
    private lateinit var compositionRepository: CompositionRepository

    @Autowired
    private lateinit var genreRepository: GenreRepository

    @GetMapping("/{id}")
    fun getAuthor(@PathVariable id: Long): Author {
        log.info("get author with id: $id")
        return authorRepository.findById(id).orElseThrow { ResourceNotFoundException("Author", "id", id) }
    }

    @GetMapping("/popular")
    fun getPopularAuthors(): List<Author> {
        log.info("get popular authors")
        return authorRepository.findAllByOrderByRatingDesc()
    }

    @GetMapping("/search")
    fun findAuthor(@RequestParam q: String): List<Author> {
        log.info("find author by name stars with $q")
        return authorRepository.findAllByNameStartsWith(q)
    }

    @GetMapping("/{id}/albums")
    fun getAuthorAlbums(@PathVariable id: Long): List<Album> {
        log.info("get albums of author with id: $id")
        val author = authorRepository.findById(id).orElseThrow { ResourceNotFoundException("Author", "id", id) }
        return author.albums.toList()
    }

    @GetMapping("/{id}/compositions")
    fun getAuthorCompositions(@PathVariable id: Long): List<Composition> {
        log.info("get compositions of author with id: $id")
        val author = authorRepository.findById(id).orElseThrow { ResourceNotFoundException("Author", "id", id) }
        return author.compositions.toList()
    }

    @GetMapping("/{id}/genres")
    fun getAuthorGenres(@PathVariable id: Long): Set<Genre> {
        log.info("get genres of author with id: $id")
        val author = authorRepository.findById(id).orElseThrow { ResourceNotFoundException("Author", "id", id) }
        return author.genres
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = ["application/json"], produces = ["application/json"])
    fun createAuthorWithName(@RequestBody authorName: String): ResponseEntity<Author> {
        log.info("create author with name: $authorName")
        val author = Author(authorName)
        val result = authorRepository.save(author)
        log.info("created author id: ${result.id}")
        return ResponseEntity.ok(result)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/albums")
    fun updateAlbums(@PathVariable id: Long, @RequestBody albumTitle: String): ResponseEntity<Author> {
        log.info("add to author $id album with title: $albumTitle")
        val author = authorRepository.findById(id).orElseThrow { ResourceNotFoundException("Author", "id", id) }
        val album = albumRepository.findByTitle(albumTitle).orElseThrow {
            ResourceNotFoundException("Album", "title", albumTitle) }
        author.addAlbum(album)
        albumRepository.save(album)
        val result = updateAuthorGenresFromAlbum(author, album)
        return ResponseEntity.ok(authorRepository.save(result))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/compositions")
    fun updateCompositions(@PathVariable id: Long, @RequestBody compositionTitle: String): ResponseEntity<Author> {
        log.info("add to author $id composition with title: $compositionTitle")
        val author = authorRepository.findById(id).orElseThrow { ResourceNotFoundException("Author", "id", id) }
        val composition = compositionRepository.findByTitle(compositionTitle).orElseThrow {
            ResourceNotFoundException("Composition", "title", compositionTitle) }
        author.addComposition(composition)
        compositionRepository.save(composition)
        val result = updateAuthorGenresFromComposition(author, composition)
        return ResponseEntity.ok(authorRepository.save(result))
    }

    private fun updateAuthorGenresFromAlbum(author: Author, album: Album): Author {
        author.genres += album.genres
        val difference = album.genres - author.genres
        difference.forEach { it.authors.add(author) }
        genreRepository.saveAll(difference)
        return author
    }

    private fun updateAuthorGenresFromComposition(author: Author, composition: Composition): Author {
        author.genres += composition.genres
        val difference = composition.genres - author.genres
        difference.forEach { it.authors.add(author) }
        genreRepository.saveAll(difference)
        return author
    }
}