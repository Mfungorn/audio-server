package audio.repositories

import audio.exception.ResourceNotFoundException
import audio.models.Author
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
class AuthorEntityResolver @Autowired constructor(
        private val authorRepository: AuthorRepository
) : SimpleObjectIdResolver() {

    override fun resolveId(id: IdKey): Any? {
        var resolved: Any? = super.resolveId(id)
        if (resolved == null) {
            resolved = _tryToLoadFromSource(id).orElseThrow { ResourceNotFoundException("Author", "id", id.key as Long) }
            bindItem(id, resolved)
        }

        return resolved
    }

    private fun _tryToLoadFromSource(idKey: IdKey): Optional<Author> {
        requireNonNull(idKey.scope, "global scope does not supported")

        val id = idKey.key as Long
        val poType = idKey.scope

        return authorRepository.findById(id)
    }

    override fun newForDeserialization(context: Any?): ObjectIdResolver {
        return AuthorEntityResolver(authorRepository)
    }

    override fun canUseFor(resolverType: ObjectIdResolver): Boolean {
        return resolverType.javaClass == AuthorEntityResolver::class.java
    }
}