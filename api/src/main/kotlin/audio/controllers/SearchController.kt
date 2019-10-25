package audio.controllers

import audio.models.GlobalSearchResponse
import audio.repositories.AlbumRepository
import audio.repositories.AuthorRepository
import audio.repositories.CompositionRepository
import audio.repositories.GenreRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/search")
class SearchController {
    private val log = LoggerFactory.getLogger("SearchController")

    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @Autowired
    private lateinit var albumRepository: AlbumRepository

    @Autowired
    private lateinit var compositionRepository: CompositionRepository

    @Autowired
    private lateinit var genreRepository: GenreRepository

    @GetMapping
    fun search(@RequestParam q: String): ResponseEntity<GlobalSearchResponse> {
        log.info("attempt to global search by query: $q")
        val authors = authorRepository.findAllByNameStartsWith(q)
        log.info("find ${authors.size} authors")
        val albums = albumRepository.findAllByTitleStartsWith(q)
        log.info("find ${albums.size} authors")
        val compositions = compositionRepository.findAllByTitleStartsWith(q)
        log.info("find ${compositions.size} authors")
        val genres = genreRepository.findAllByNameStartsWith(q).toList()
        log.info("find ${genres.size} authors")

        val result = GlobalSearchResponse(
                authors,
                albums,
                compositions,
                genres
        )
        return ResponseEntity.ok(result)
    }
}