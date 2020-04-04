package audio

import audio.models.*
import audio.repositories.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component


@Component
class StartupData : ApplicationRunner {

    @Autowired
    lateinit var managerRepository: ManagerRepository

    @Autowired
    lateinit var authorRepository: AuthorRepository

    @Autowired
    lateinit var albumRepository: AlbumRepository

    @Autowired
    lateinit var genreRepository: GenreRepository

    @Autowired
    lateinit var trackRepository: CompositionRepository

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
        managerRepository.save(manager)

        val albums = listOf(
                Album("Reroute to Remain", year = 2002),
                Album("Metallica", "https://www.1c-interes.ru/upload/resize_src/0d/0d9a3b1e052d80f11ff4a6e7d1befe03.jpg", year = 1991)
        )
        val authors = listOf(
                Author("In Flames", "In Flames is a Swedish metal band from Gothenburg, formed in 1990. The band’s earlier work is considered to be a major influence on the melodic death metal music genre...", "https://files.gamebanana.com/img/ico/sprays/flames.png").apply { addAlbum(albums[0]) },
                Author("Metallica", "Metallica is an American metal band formed in 1981 in Los Angeles, California, United States when drummer Lars Ulrich posted an advertisement in The Recycler", "https://www.1c-interes.ru/upload/resize_src/f7/f7ac22876190086d367638a564c2e862.jpg").apply { addAlbum(albums[1]) },
                Author("Muse", "Muse are an alternative rock band from Teignmouth, England, United Kingdom", "https://is1-ssl.mzstatic.com/image/thumb/Purple113/v4/5e/bd/6c/5ebd6c8e-c5c3-715c-c40b-e842c4e2eee5/source/256x256bb.jpg"),
                Author("The Beatles", "The Beatles were an iconic rock group from Liverpool, England. They are frequently cited as the most commercially successful and critically acclaimed band in modern history", "http://show-biz.by/blog/image/blog_image/3728/large/_v=64d081531337732"),
                Author("The Weeknd", "Abel Makkonen Tesfaye, known professionally as The Weeknd (pronounced the weekend), is a Canadian singer, songwriter, and record producer", "https://res.cloudinary.com/dysheof28/image/upload//t_square_large/images/artists/the-weeknd/tracks/the-hills.jpg"),
                Author("Lana Del Rey", "Elizabeth Woolridge Grant (born 21 June 1985), better known as Lana Del Rey, is a singer-songwriter and producer from Lake Placid, New York, United States", "https://www.1c-interes.ru/upload/resize_src/94/9457dffd61022096f0c69759b5cb8e85.jpg")
        )
        val genres = listOf(
                Genre("metal"),
                Genre("rock"),
                Genre("rnb"),
                Genre("indie"),
                Genre("pop")
        )
        val tracks = listOf(
                Composition("Cloud Connected", 220, "People like you\n" +
                        "You live in a dream world\n" +
                        "You despise the outside\n" +
                        "And you fear the next one\n" +
                        "It's in your dream…", 10).apply {
                    addGenre(genres[0])
                    addAuthor(authors[0])
                },
                Composition("Nothing Else Matters", 390, "So close, no matter how far\n" +
                        "Couldn't be much more from the heart\n" +
                        "Forever trusting who we are\n" +
                        "And nothing else matters\n" +
                        "Never opened myself this…", 10).apply {
                    addGenre(genres[1])
                    addAuthor(authors[1])
                },
                Composition("Supermassive Black Hole", 210, "", 10).apply {
                    addGenre(genres[1])
                    addAuthor(authors[2])
                },
                Composition("Yesterday", 125, "No lyrics", 10).apply {
                    addGenre(genres[1])
                    addAuthor(authors[3])
                },
                Composition("Starboy", 230, "I'm tryna put you in the worst mood, ah\n" +
                        "P1 cleaner than your church shoes, ah\n" +
                        "Milli point two just to hurt you, ah\n" +
                        "All red Lamb' just to tease you,…", 20).apply {
                    addGenre(genres[4])
                    addGenre(genres[2])
                    addAuthor(authors[4])
                },
                Composition("California", 305, "You don't ever have to be stronger than you really are\n" +
                        "When you're lying in my arms…", 15).apply {
                    addGenre(genres[4])
                    addGenre(genres[3])
                    addAuthor(authors[5])
                }
        )

        authorRepository.saveAll(authors)
        genreRepository.saveAll(genres)
        trackRepository.saveAll(tracks)

        albums[0].apply {
            addAuthor(authors[0])
            authorRepository.save(authors[0])
            albumRepository.save(this)
        }
        albums[1].apply {
            addAuthor(authors[1])
            authorRepository.save(authors[1])
            albumRepository.save(this)
        }
    }
}