package audio.payload

import audio.models.Composition

data class CustomerProfilePayload(
        val name: String,
        val email: String,
        val phone: String?,
        val balance: Int,
        val favoriteCompositions: List<Composition> = listOf()
)