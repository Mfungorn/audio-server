package org.quest.controllers

import org.quest.exception.ResourceNotFoundException
import org.quest.models.Album
import org.quest.models.Author
import org.quest.models.Composition
import org.quest.models.Genre
import org.quest.repositories.AlbumRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/albums")
class AlbumController {

    private val log = LoggerFactory.getLogger("AlbumController")

    @Autowired
    private val albumRepository: AlbumRepository? = null

    @GetMapping("/{id}")
    fun getAlbum(@PathVariable id: Long): Album {
        log.info("get album with id: $id")
        return albumRepository!!.findById(id).orElseThrow { ResourceNotFoundException("Album", "id", id) }
    }

    @GetMapping("/popular")
    fun getPopularAlbums(): List<Album> {
        log.info("get popular albums")
        return albumRepository!!.findByOrderByRatingDesc()
    }

    @GetMapping("/search")
    fun findAuthor(@RequestParam q: String): List<Album> {
        log.info("find album by title stars with $q")
        return albumRepository!!.findAllByTitleStartsWith(q)
    }

    @GetMapping("/{id}/albums")
    fun getAlbumAuthors(@PathVariable id: Long): List<Author> {
        log.info("get authors of album with id: $id")
        val album = albumRepository!!.findById(id).orElseThrow { ResourceNotFoundException("Album", "id", id) }
        return album.authors.toList()
    }

    @GetMapping("/{id}/compositions")
    fun getAlbumCompositions(@PathVariable id: Long): List<Composition> {
        log.info("get compositions of album with id: $id")
        val album = albumRepository!!.findById(id).orElseThrow { ResourceNotFoundException("Album", "id", id) }
        return album.compositions.toList()
    }

    @GetMapping("/{id}/genres")
    fun getAlbumGenres(@PathVariable id: Long): Set<Genre> {
        log.info("get genres of album with id: $id")
        val album = albumRepository!!.findById(id).orElseThrow { ResourceNotFoundException("Album", "id", id) }
        return album.genres
    }
}