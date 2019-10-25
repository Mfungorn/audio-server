package audio

import audio.models.AuthProvider
import audio.models.Manager
import audio.repositories.ManagerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class StartupData : ApplicationRunner {

    @Autowired
    lateinit var repository: ManagerRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    override fun run(args: ApplicationArguments) {
        val manager = Manager.generate(
                0L,
                "ADMIN",
                "manager@audio.org",
                passwordEncoder.encode("0000")
        ).apply {
            provider = AuthProvider.local
        }
        repository.save(manager)
    }
}