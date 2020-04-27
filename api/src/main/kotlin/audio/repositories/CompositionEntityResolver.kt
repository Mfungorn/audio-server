package audio.repositories

import audio.exception.ResourceNotFoundException
import audio.models.Composition
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
class CompositionEntityResolver @Autowired constructor(
        private val compositionRepository: CompositionRepository
) : SimpleObjectIdResolver() {

    override fun resolveId(id: IdKey): Any? {
        var resolved: Any? = super.resolveId(id)
        if (resolved == null) {
            resolved = tryToLoadFromSource(id).orElseThrow { ResourceNotFoundException("Composition", "id", id.key as Long) }
            bindItem(id, resolved)
        }

        return resolved
    }

    private fun tryToLoadFromSource(idKey: IdKey): Optional<Composition> {
        requireNonNull(idKey.scope, "global scope does not supported")

        val id = idKey.key as Long
        val poType = idKey.scope

        return compositionRepository.findById(id)
    }

    override fun newForDeserialization(context: Any?): ObjectIdResolver {
        return CompositionEntityResolver(compositionRepository)
    }

    override fun canUseFor(resolverType: ObjectIdResolver): Boolean {
        return resolverType.javaClass == CompositionEntityResolver::class.java
    }
}