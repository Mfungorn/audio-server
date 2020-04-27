package audio.repositories

import audio.exception.ResourceNotFoundException
import audio.models.Album
import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey
import com.fasterxml.jackson.annotation.ObjectIdResolver
import com.fasterxml.jackson.annotation.SimpleObjectIdResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.util.*
import java.util.Objects.requireNonNull


@Component
@Scope("prototype")
class AlbumEntityResolver @Autowired constructor(
        private val albumRepository: AlbumRepository
) : SimpleObjectIdResolver() {

    override fun resolveId(id: IdKey): Any? {
        var resolved: Any? = super.resolveId(id)
        if (resolved == null) {
            resolved = tryToLoadFromSource(id).orElseThrow { ResourceNotFoundException("Album", "id", id.key as Long) }
            bindItem(id, resolved)
        }

        return resolved
    }

    private fun tryToLoadFromSource(idKey: IdKey): Optional<Album> {
        requireNonNull(idKey.scope, "global scope does not supported")

        val id = idKey.key as Long
        val poType = idKey.scope

        return albumRepository.findById(id)
    }

    override fun newForDeserialization(context: Any?): ObjectIdResolver {
        return AlbumEntityResolver(albumRepository)
    }

    override fun canUseFor(resolverType: ObjectIdResolver): Boolean {
        return resolverType.javaClass == AlbumEntityResolver::class.java
    }
}