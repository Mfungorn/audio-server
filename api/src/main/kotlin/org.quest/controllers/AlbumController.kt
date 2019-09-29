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
@RequestMapping("/albums")
class AlbumController {

    private val log = LoggerFactory.getLogger("AlbumController")

    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @Autowired
    private lateinit var albumRepository: AlbumRepository

    @Autowired
    private lateinit var compositionRepository: CompositionRepository

    @Autowired
    private lateinit var genreRepository: GenreRepository

    @GetMapping("/{id}")
    fun getAlbum(@PathVariable id: Long): Album {
        log.info("attempt to get album with id: $id")
        return albumRepository.findById(id).orElseThrow { ResourceNotFoundException("Album", "id", id) }
    }

    @GetMapping("/popular")
    fun getPopularAlbums(): List<Album> {
        log.info("attempt to get popular albums")
        return albumRepository.findByOrderByRatingDesc()
    }

    @GetMapping("/search")
    fun findAuthor(@RequestParam q: String): List<Album> {
        log.info("attempt to find album by title stars with $q")
        return albumRepository.findAllByTitleStartsWith(q)
    }

    @GetMapping("/{id}/albums")
    fun getAlbumAuthors(@PathVariable id: Long): List<Author> {
        log.info("attempt to get authors of album with id: $id")
        val album = albumRepository.findById(id).orElseThrow { ResourceNotFoundException("Album", "id", id) }
        return album.authors.toList()
    }

    @GetMapping("/{id}/compositions")
    fun getAlbumCompositions(@PathVariable id: Long): List<Composition> {
        log.info("attempt to get compositions of album with id: $id")
        val album = albumRepository.findById(id).orElseThrow { ResourceNotFoundException("Album", "id", id) }
        return album.compositions.toList()
    }

    @GetMapping("/{id}/genres")
    fun getAlbumGenres(@PathVariable id: Long): Set<Genre> {
        log.info("attempt to get genres of album with id: $id")
        val album = albumRepository.findById(id).orElseThrow { ResourceNotFoundException("Album", "id", id) }
        return album.genres
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = ["application/json"], produces = ["application/json"])
    fun createAlbumWithTitle(@RequestBody title: String): ResponseEntity<Album> {
        log.info("attempt to create album with title: $title")
        val album = Album(title)
        val result = albumRepository.save(album)
        log.info("created album id: ${result.id}")
        return ResponseEntity.ok(result)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/authors")
    fun updateAuthors(@PathVariable id: Long, @RequestBody authorName: String): ResponseEntity<Album> {
        log.info("attempt to add to album $id author with name: $authorName")
        val album = albumRepository.findById(id).orElseThrow { ResourceNotFoundException("Album", "id", id) }
        val author = authorRepository.findByName(authorName).orElseThrow {
            ResourceNotFoundException("Author", "name", authorName) }
        album.addAuthor(author)
        authorRepository.save(author)
        log.info("$authorName added successfully")
        val result = albumRepository.save(album)
        return ResponseEntity.ok(result)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/compositions")
    fun updateCompositions(@PathVariable id: Long, @RequestBody compositionTitle: String): ResponseEntity<Album> {
        log.info("attempt to add to album $id composition with title: $compositionTitle")
        val album = albumRepository.findById(id).orElseThrow { ResourceNotFoundException("Album", "id", id) }
        val composition = compositionRepository.findByTitle(compositionTitle).orElseThrow {
            ResourceNotFoundException("Composition", "title", compositionTitle) }
        album.addComposition(composition)
        compositionRepository.save(composition)
        log.info("$compositionTitle added successfully, attempt to update genres")
        val result = updateAlbumGenresFromComposition(album, composition)
        return ResponseEntity.ok(albumRepository.save(result))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    fun deleteAlbum(@PathVariable id: Long): ResponseEntity<Long> {
        val album = albumRepository.findById(id).orElseThrow { ResourceNotFoundException("Album", "id", id) }
        log.info("attempt to delete album ${album.title}")
        albumRepository.delete(album)
        return ResponseEntity.ok(id)
    }

    private fun updateAlbumGenresFromComposition(album: Album, composition: Composition): Album {
        album.genres += composition.genres
        val difference = composition.genres - album.genres
        log.info("${difference.size} genres are different")
        difference.forEach { it.albums.add(album) }
        genreRepository.saveAll(difference)
        return album
    }
}