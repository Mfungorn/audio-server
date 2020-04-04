package audio.controllers

import audio.exception.ResourceNotFoundException
import audio.models.Album
import audio.models.Author
import audio.models.Composition
import audio.models.Genre
import audio.payload.AlbumPayload
import audio.payload.AlbumPostPayload
import audio.repositories.AlbumRepository
import audio.repositories.AuthorRepository
import audio.repositories.CompositionRepository
import audio.util.mapToAlbumPayload
import audio.util.mapToAlbumPayloadList
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

    @GetMapping("/{id}")
    fun getAlbum(@PathVariable id: Long): ResponseEntity<AlbumPayload> {
        log.info("attempt to get album with id: $id")
        val album = albumRepository.findById(id).orElseThrow { ResourceNotFoundException("Album", "id", id) }
        val payload = album.mapToAlbumPayload()
        return ResponseEntity.ok(payload)
    }

    @GetMapping
    fun getAlbum(@RequestParam title: String): ResponseEntity<AlbumPayload> {
        log.info("attempt to get album with title: $title")
        val album = albumRepository.findByTitle(title).orElseThrow { ResourceNotFoundException("Album", "title", title) }
        val payload = album.mapToAlbumPayload()
        return ResponseEntity.ok(payload)
    }

    @Deprecated("-", ReplaceWith("getPopularAlbums()"))
    @GetMapping("/all")
    fun getAlbums(): List<Album> {
        log.info("attempt to get all albums")
        return albumRepository.findAll()
    }

    @GetMapping("/popular")
    fun getPopularAlbums(): ArrayList<AlbumPayload> {
        log.info("attempt to get popular albums")
        val sortedAlbums = albumRepository.findByOrderByRatingDesc()
        return sortedAlbums.mapToAlbumPayloadList()
    }

    @GetMapping("/search")
    fun findAlbum(@RequestParam q: String): ArrayList<AlbumPayload> {
        log.info("attempt to find album by title stars with $q")
        val results = albumRepository.findAllByTitleStartsWith(q)
        return results.mapToAlbumPayloadList()
    }

    @GetMapping("/{id}/authors")
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
        return album.compositions.flatMapTo(mutableSetOf()) { composition -> composition.genres }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping(consumes = ["application/json"], produces = ["application/json"])
    fun createAlbum(@RequestBody payload: AlbumPostPayload): ResponseEntity<AlbumPayload> {
        log.info("attempt to create album with title: ${payload.title}")
        val album = Album(
                payload.title,
                payload.cover
        )
        val result = albumRepository.save(album)
        log.info("created album id: ${result.id}")
        return ResponseEntity.ok(result.mapToAlbumPayload())
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    fun putComposition(@PathVariable id: Long, @RequestBody changes: Album): ResponseEntity<String> {
        log.info("attempt to put album $id")
        albumRepository.save(changes)
        return ResponseEntity.ok("Album updated successfully")
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/{id}/authors")
    fun updateAuthors(@PathVariable id: Long, @RequestBody authorName: String): ResponseEntity<String> {
        log.info("attempt to add to album $id author with name: $authorName")
        val album = albumRepository.findById(id).orElseThrow { ResourceNotFoundException("Album", "id", id) }
        val author = authorRepository.findByName(authorName).orElseThrow {
            ResourceNotFoundException("Author", "name", authorName) }
        album.addAuthor(author)
        authorRepository.save(author)
        log.info("$authorName added successfully")
        val result = albumRepository.save(album)
        return ResponseEntity.ok("Author added to album")
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/{id}/compositions")
    fun updateCompositions(@PathVariable id: Long, @RequestBody compositionTitle: String): ResponseEntity<String> {
        log.info("attempt to add to album $id composition with title: $compositionTitle")
        val album = albumRepository.findById(id).orElseThrow { ResourceNotFoundException("Album", "id", id) }
        val composition = compositionRepository.findByTitle(compositionTitle).orElseThrow {
            ResourceNotFoundException("Composition", "title", compositionTitle) }
        album.addComposition(composition)
        compositionRepository.save(composition)
        log.info("$compositionTitle added successfully, attempt to update genres")
        albumRepository.save(album)
        return ResponseEntity.ok("Composition added to album")
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    fun deleteAlbum(@PathVariable id: Long): ResponseEntity<Long> {
        val album = albumRepository.findById(id).orElseThrow { ResourceNotFoundException("Album", "id", id) }
        log.info("attempt to delete album ${album.title}")
        albumRepository.delete(album)
        return ResponseEntity.ok(id)
    }
}