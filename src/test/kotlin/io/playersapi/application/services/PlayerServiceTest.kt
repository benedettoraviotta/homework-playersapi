package io.playersapi.application.services


import io.playersapi.core.domain.Player
import io.playersapi.core.domain.PlayerStatus
import io.playersapi.core.ports.PlayerRepository
import io.quarkus.test.InjectMock
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`

@QuarkusTest
class PlayerServiceTest {

    @InjectMock
    private lateinit var repository: PlayerRepository

    private lateinit var playerService: PlayerService

    @BeforeEach
    fun setUp() {
        playerService = PlayerService(repository)
    }

    private val players = listOf(
        Player(1, "Rafael Leao", "Winger", 1999, PlayerStatus.ACTIVE, "Milan"),
        Player(2, "Gianluigi Buffon", "Goalkeeper", 1978, PlayerStatus.RETIRED, "RETIRED"),
        Player(3, "Alessandro Bastoni", "Defender", 1999, PlayerStatus.ACTIVE, "Inter"),
        Player(4, "Sandi Lovric", "Midfielder", 1998, PlayerStatus.ACTIVE, "Udinese"),
    )

    @Test
    fun `should return all players when no filters are applied`() {
        `when` ( repository.findAll()).thenReturn(players)
        `when` ( repository.findByFilters(null, null, null, null, null)).thenReturn(players)

        val result = playerService.filterPlayers(null, null, null, null, null)

        assertEquals(4, result.size)
    }

    @Test
    fun `should filter players by position`() {
        `when` ( repository.findByFilters("Winger", null, null, null, null)).thenReturn(players.filter { it.position == "Winger" })

        val result = playerService.filterPlayers("Winger", null, null, null, null)

        assertEquals(1, result.size)
        assertEquals("Rafael Leao", result.first().name)
    }

    @Test
    fun `should filter players by minBirthYear`() {
        `when` (repository.findByFilters(null, 1998, null, null, null) ).thenReturn(players.filter { it.birthYear >= 1998 })

        val result = playerService.filterPlayers(null, 1998, null, null, null)

        assertEquals(3, result.size)
        assertEquals(listOf("Rafael Leao", "Alessandro Bastoni", "Sandi Lovric"), result.map { it.name })
    }

    @Test
    fun `should filter players by maxBirthYear`() {
        `when` ( repository.findByFilters(null, null, 1980, null, null)).thenReturn(players.filter { it.birthYear <= 1980 })

        val result = playerService.filterPlayers(null, null, 1980, null, null)

        assertEquals(1, result.size)
        assertEquals("Gianluigi Buffon", result.first().name)
    }

    @Test
    fun `should filter players by status ACTIVE`() {
        `when` ( repository.findByFilters(null, null, null, PlayerStatus.ACTIVE, null))
            .thenReturn(players.filter { it.status == PlayerStatus.ACTIVE })

        val result = playerService.filterPlayers(null, null, null, PlayerStatus.ACTIVE, null)

        assertEquals(3, result.size)
        assertEquals(listOf("Rafael Leao", "Alessandro Bastoni", "Sandi Lovric"), result.map { it.name })
    }

    @Test
    fun `should filter players by status RETIRED`() {
        `when` ( repository.findByFilters(null, null, null, PlayerStatus.RETIRED, null))
            .thenReturn(players.filter { it.status == PlayerStatus.RETIRED })

        val result = playerService.filterPlayers(null, null, null, PlayerStatus.RETIRED, null)

        assertEquals(1, result.size)
        assertEquals("Gianluigi Buffon", result.first().name)
    }

    @Test
    fun `should filter players by club`() {
        `when` ( repository.findByFilters(null, null, null, null, "Inter"))
            .thenReturn(players.filter { it.club == "Inter" })

        val result = playerService.filterPlayers(null, null, null, null, "Inter")

        assertEquals(1, result.size)
        assertEquals("Alessandro Bastoni", result.first().name)
    }

    @Test
    fun `should apply multiple filters together`() {
        `when` ( repository.findByFilters("Defender", 1995, 2000, PlayerStatus.ACTIVE, "Inter"))
            .thenReturn(players.filter {
                it.position == "Defender" && it.birthYear in 1995..2000 && it.status == PlayerStatus.ACTIVE && it.club == "Inter"
            })

        val result = playerService.filterPlayers("Defender", 1995, 2000, PlayerStatus.ACTIVE, "Inter")

        assertEquals(1, result.size)
        assertEquals("Alessandro Bastoni", result.first().name)
    }

    @Test
    fun `should return empty list when no player matches filters`() {
        `when` ( repository.findByFilters("Forward", 2000, 2010, PlayerStatus.ACTIVE, "Juventus"))
            .thenReturn(emptyList())

        val result = playerService.filterPlayers("Forward", 2000, 2010, PlayerStatus.ACTIVE, "Juventus")

        assertEquals(0, result.size)
    }
}
