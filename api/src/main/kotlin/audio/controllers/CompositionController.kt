package audio.controllers

import audio.exception.ResourceNotFoundException
import audio.models.Author
import audio.models.Composition
import audio.models.Genre
import audio.payload.AlbumPayload
import audio.payload.CompositionPayload
import audio.payload.CompositionPostPayload
import audio.repositories.*
import audio.security.TokenProvider
import audio.util.toAlbumsPayload
import audio.util.toCompositionPayload
import audio.util.toCompositionsPayload
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/compositions")
class CompositionController {

    private val log = LoggerFactory.getLogger("CompositionController")

    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @Autowired
    private lateinit var albumRepository: AlbumRepository

    @Autowired
    private lateinit var compositionRepository: CompositionRepository

    @Autowired
    private lateinit var genreRepository: GenreRepository

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var tokenProvider: TokenProvider

    @GetMapping("/{id}")
    fun getComposition(@PathVariable id: Long): CompositionPayload {
        log.info("get composition with id: $id")
        val composition = compositionRepository
                .findById(id)
                .orElseThrow { ResourceNotFoundException("Composition", "id", id) }
        return composition.toCompositionPayload()
    }

    @Deprecated("-", ReplaceWith("getPopularCompositions()"))
    @GetMapping("/all")
    fun getCompositions(): List<Composition> {
        log.info("attempt to get compositions")
        return compositionRepository.findAll()
    }

    @GetMapping("/popular")
    fun getPopularCompositions(): List<CompositionPayload> {
        log.info("attempt to get popular compositions")
        val result = compositionRepository.findByOrderByRatingDesc()
        return result.toCompositionsPayload()
    }

    @GetMapping("/search")
    fun findComposition(@RequestParam q: String): List<CompositionPayload> {
        log.info("attempt to find composition by title stars with $q")
        val result = compositionRepository.findAllByTitleStartsWith(q)
        return result.toCompositionsPayload()
    }

    @GetMapping("/{id}/authors")
    fun getCompositionAuthors(@PathVariable id: Long): List<Author> {
        log.info("attempt to get authors of composition with id: $id")
        val composition = compositionRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Composition", "id", id)
        }
        return composition.authors.toList()
    }

    @GetMapping("/{id}/albums")
    fun getCompositionAlbums(@PathVariable id: Long): List<AlbumPayload> {
        log.info("attempt to get albums with composition with id: $id")
        val composition = compositionRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Composition", "id", id)
        }
        return composition.albums.toList().toAlbumsPayload()
    }

    @GetMapping("/{id}/genres")
    fun getCompositionGenres(@PathVariable id: Long): Set<Genre> {
        log.info("attempt to get genres of album with id: $id")
        val composition = compositionRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Composition", "id", id)
        }
        return composition.genres
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping(consumes = ["application/json"], produces = ["application/json"])
    fun createComposition(@RequestBody postPayload: CompositionPostPayload): ResponseEntity<CompositionPayload> {
        log.info("attempt to create composition with title: ${postPayload.title}")
        val composition = Composition(
                postPayload.title,
                postPayload.duration,
                postPayload.text,
                postPayload.price,
                postPayload.cover
        )
        val result = compositionRepository.save(composition).toCompositionPayload()
        log.info("created composition id: ${result.id}")
        return ResponseEntity.ok(result)
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    fun putComposition(@PathVariable id: Long, @RequestBody changes: Composition): ResponseEntity<String> {
        log.info("attempt to put composition $id")
        compositionRepository.save(changes)
        return ResponseEntity.ok("Composition updated successfully")
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/{id}/authors")
    fun updateAuthors(@PathVariable id: Long, @RequestBody authorName: String): ResponseEntity<String> {
        log.info("attempt to add to composition $id author with name: $authorName")
        val composition = compositionRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Composition", "id", id)
        }
        val author = authorRepository.findByName(authorName).orElseThrow {
            ResourceNotFoundException("Author", "name", authorName)
        }
        composition.addAuthor(author)
        authorRepository.save(author)
        compositionRepository.save(composition)
        return ResponseEntity.ok("Author added to composition")
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/{id}/albums")
    fun updateAlbums(@PathVariable id: Long, @RequestBody albumTitle: String): ResponseEntity<String> {
        log.info("attempt to add to composition $id album with title: $albumTitle")
        val composition = compositionRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Composition", "id", id)
        }
        val album = albumRepository.findByTitle(albumTitle).orElseThrow {
            ResourceNotFoundException("Album", "title", albumTitle)
        }
        composition.addAlbum(album)
        albumRepository.save(album)
        compositionRepository.save(composition)
        return ResponseEntity.ok("Album added to composition")
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/{id}/genres")
    fun addGenre(@PathVariable id: Long, @RequestBody genreName: String): ResponseEntity<String> {
        log.info("attempt to add genre: $genreName to composition $id")
        val composition = compositionRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Composition", "id", id)
        }
        val genre = genreRepository.findByName(genreName).orElseThrow {
            ResourceNotFoundException("Genre", "name", genreName)
        }
        composition.addGenre(genre)
        genreRepository.save(genre)
        compositionRepository.save(composition)
        return ResponseEntity.ok("Genre added to composition")
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/{id}/favorite")
    fun addToFavorite(
            @RequestHeader(value = "Authorization") authorization: String,
            @PathVariable id: Long
    ): ResponseEntity<String> {
        log.info("attempt to find customer and add favorite composition with id: $id")
        val token = if (authorization.startsWith("Bearer ")) {
            authorization.substring(7, authorization.length)
        } else {
            throw BadCredentialsException("Invalid token")
        }
        val customerId = tokenProvider.getUserIdFromToken(token) ?: throw BadCredentialsException("Invalid token")
        val customer = customerRepository.findById(customerId)
                .orElseThrow { ResourceNotFoundException("User", "id", customerId) }
        log.info("find customer - ${customer.name}")
        val composition = compositionRepository.findById(id)
                .orElseThrow { ResourceNotFoundException("Composition", "id", id) }
        log.info("find composition - ${composition.title}")
        composition.addCustomer(customer)
        composition.rating++
        customerRepository.save(customer)
        compositionRepository.save(composition)
        return ResponseEntity.ok("Composition added to favorite")
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    fun deleteComposition(@PathVariable id: Long): ResponseEntity<Long> {
        val composition = compositionRepository.findById(id).orElseThrow {
            ResourceNotFoundException("composition", "id", id)
        }
        log.info("attempt to delete composition ${composition.title}")
        compositionRepository.delete(composition)
        return ResponseEntity.ok(id)
    }
}