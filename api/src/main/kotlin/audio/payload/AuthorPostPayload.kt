package audio.payload

data class AuthorPostPayload(
        val name: String,
        val bio: String,
        val logo: String = ""
)