package audio.controllers

import audio.exception.ResourceNotFoundException
import audio.models.Author
import audio.models.Composition
import audio.models.Genre
import audio.payload.AlbumPayload
import audio.payload.AuthorPostPayload
import audio.repositories.AlbumRepository
import audio.repositories.AuthorRepository
import audio.repositories.CompositionRepository
import audio.repositories.CustomerRepository
import audio.security.TokenProvider
import audio.util.mapToAlbumPayloadList
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.BadCredentialsException
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
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var tokenProvider: TokenProvider

    @GetMapping("/{id}")
    fun getAuthor(@PathVariable id: Long): Author {
        log.info("attempt to get author with id: $id")
        return authorRepository.findById(id).orElseThrow { ResourceNotFoundException("Author", "id", id) }
    }

    @GetMapping
    fun getAuthor(@RequestParam name: String): Author {
        log.info("attempt to get author with name: $name")
        return authorRepository.findByName(name).orElseThrow { ResourceNotFoundException("Author", "name", name) }
    }

    @GetMapping("/all")
    fun getAuthors(): List<Author> {
        log.info("attempt to get all authors")
        return authorRepository.findAll()
    }

    @GetMapping("/popular")
    fun getPopularAuthors(): List<Author> {
        log.info("attempt to get popular authors")
        return authorRepository.findAllByOrderByRatingDesc()
    }

    @GetMapping("/search")
    fun findAuthor(@RequestParam q: String): List<Author> {
        log.info("attempt to find author by name starts with $q")
        return authorRepository.findAllByNameStartsWith(q)
    }

    @GetMapping("/{id}/albums")
    fun getAuthorAlbums(@PathVariable id: Long): List<AlbumPayload> {
        log.info("attempt to get albums of author with id: $id")
        val author = authorRepository.findById(id).orElseThrow { ResourceNotFoundException("Author", "id", id) }
        return author.albums.toList().mapToAlbumPayloadList()
    }

    @GetMapping("/{id}/compositions")
    fun getAuthorCompositions(@PathVariable id: Long): List<Composition> {
        log.info("attempt to get compositions of author with id: $id")
        val author = authorRepository.findById(id).orElseThrow { ResourceNotFoundException("Author", "id", id) }
        return author.compositions.toList()
    }

    @GetMapping("/{id}/genres")
    fun getAuthorGenres(@PathVariable id: Long): Set<Genre> {
        log.info("attempt to get genres of author with id: $id")
        val author = authorRepository.findById(id).orElseThrow { ResourceNotFoundException("Author", "id", id) }
        return author.compositions.flatMapTo(mutableSetOf()) { it.genres }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping(consumes = ["application/json"], produces = ["application/json"])
    fun createAuthor(@RequestBody payload: AuthorPostPayload): ResponseEntity<Author> {
        log.info("attempt to create author with name: ${payload.name}")
        val author = Author(
                payload.name,
                payload.bio,
                payload.logo
        )
        val result = authorRepository.save(author)
        log.info("created author id: ${result.id}")
        return ResponseEntity.ok(result)
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    fun putAuthor(@PathVariable id: Long, @RequestBody changes: Author): ResponseEntity<String> {
        log.info("attempt to put author $id")
        authorRepository.save(changes)
        return ResponseEntity.ok("Author updated successfully")
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/{id}/albums")
    fun updateAlbums(@PathVariable id: Long, @RequestBody albumTitle: String): ResponseEntity<String> {
        log.info("attempt to add to author $id album with title: $albumTitle")
        val author = authorRepository.findById(id).orElseThrow { ResourceNotFoundException("Author", "id", id) }
        val album = albumRepository.findByTitle(albumTitle).orElseThrow {
            ResourceNotFoundException("Album", "title", albumTitle)
        }
        author.addAlbum(album)
        albumRepository.save(album)
        log.info("$albumTitle added successfully, attempt to update genres")
        authorRepository.save(author)
        return ResponseEntity.ok("Album added to author")
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/{id}/compositions")
    fun updateCompositions(@PathVariable id: Long, @RequestBody compositionTitle: String): ResponseEntity<String> {
        log.info("attempt to  add to author $id composition with title: $compositionTitle")
        val author = authorRepository.findById(id).orElseThrow { ResourceNotFoundException("Author", "id", id) }
        val composition = compositionRepository.findByTitle(compositionTitle).orElseThrow {
            ResourceNotFoundException("Composition", "title", compositionTitle)
        }
        author.addComposition(composition)
        compositionRepository.save(composition)
        log.info("$compositionTitle added successfully, attempt to update genres")
        authorRepository.save(author)
        return ResponseEntity.ok("Composition added to author")
    }

    @PatchMapping("/{id}/favorite")
    fun addToFavorite(
            @RequestHeader(value = "Authorization") authorization: String,
            @PathVariable id: Long
    ): ResponseEntity<String> {
        log.info("attempt to find customer and add favorite author with id: $id")
        val token = if (authorization.startsWith("Bearer ")) {
            authorization.substring(7, authorization.length)
        } else {
            throw BadCredentialsException("Invalid token")
        }
        val customerId = tokenProvider.getUserIdFromToken(token) ?: throw BadCredentialsException("Invalid token")
        val customer = customerRepository.findById(customerId)
                .orElseThrow { ResourceNotFoundException("User", "id", customerId) }
        log.info("find customer - ${customer.name}")
        val author = authorRepository.findById(id).orElseThrow { ResourceNotFoundException("Author", "id", id) }
        log.info("find author - ${author.name}")
        author.addCustomer(customer)
        author.rating++
        customerRepository.save(customer)
        authorRepository.save(author)
        return ResponseEntity.ok("Author added to favorite")
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    fun deleteAuthor(@PathVariable id: Long): ResponseEntity<Long> {
        val author = authorRepository.findById(id).orElseThrow { ResourceNotFoundException("Author", "id", id) }
        log.info("attempt to delete author ${author.name}")
        authorRepository.delete(author)
        return ResponseEntity.ok(id)
    }
}