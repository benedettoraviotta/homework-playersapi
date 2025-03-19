package io.playersapi.application.services

import io.playersapi.adapters.web.errors.InvalidPlayerStatusException
import io.playersapi.application.dto.PlayerFilter
import io.playersapi.core.domain.PlayerStatus
import io.playersapi.core.ports.PlayerRepository
import io.playersapi.testutils.TestData
import io.playersapi.testutils.TestData.players
import io.quarkus.test.InjectMock
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.Uni
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Stream

@QuarkusTest
class PlayerServiceTest {

    @InjectMock
    private lateinit var repository: PlayerRepository

    private lateinit var playerService: PlayerService

    @BeforeEach
    fun setUp() {
        playerService = PlayerService(repository)
    }

    @ParameterizedTest
    @MethodSource("filterArguments")
    fun `should filter players based on filters`(
        filter: PlayerFilter,
        expectedPlayerNames: List<String>
    ) = runBlocking {
        `when`(repository.findByFilters(filter)).thenReturn(
            Uni.createFrom().item(
                TestData.players.filter { player ->
                    val minBirthYear = filter.minBirthYear
                    val maxBirthYear = filter.maxBirthYear
                    (filter.position == null || player.position == filter.position) &&
                            (filter.minBirthYear == null || player.birthYear >= minBirthYear!!) &&
                            (filter.maxBirthYear == null || player.birthYear <= maxBirthYear!!) &&
                            (filter.status == null || player.status.name == filter.status) &&
                            (filter.club == null || player.club == filter.club)
                }
            )
        )

        val resultUni = playerService.filterPlayers(filter)
        val result = resultUni.await().indefinitely()

        assertEquals(expectedPlayerNames.size, result.size)
        assertEquals(expectedPlayerNames, result.map { it.name })

        verify(repository).findByFilters(filter)
        verifyNoMoreInteractions(repository)
    }

    companion object {
        @JvmStatic
        fun filterArguments(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(PlayerFilter(null, null, null, null, null), listOf("Rafael Leao", "Gianluigi Buffon", "Alessandro Bastoni", "Sandi Lovric", "Filippo Inzaghi")),
                Arguments.of(PlayerFilter("Winger", null, null, null, null), listOf("Rafael Leao")),
                Arguments.of(PlayerFilter(null, 1998, null, null, null), listOf("Rafael Leao", "Alessandro Bastoni", "Sandi Lovric")),
                Arguments.of(PlayerFilter(null, null, 1980, null, null), listOf("Gianluigi Buffon", "Filippo Inzaghi")),
                Arguments.of(PlayerFilter(null, null, null, PlayerStatus.ACTIVE.name, null), listOf("Rafael Leao", "Alessandro Bastoni", "Sandi Lovric")),
                Arguments.of(PlayerFilter(null, null, null, PlayerStatus.RETIRED.name, null), listOf("Gianluigi Buffon", "Filippo Inzaghi")),
                Arguments.of(PlayerFilter(null, null, null, null, "Inter"), listOf("Alessandro Bastoni")),
                Arguments.of(PlayerFilter("Defender", 1995, 2000, PlayerStatus.ACTIVE.name, "Inter"), listOf("Alessandro Bastoni")),
                Arguments.of(PlayerFilter("Forward", 2000, 2010, PlayerStatus.ACTIVE.name, "Juventus"), emptyList<String>())
            )
        }
    }

    @Test
    fun `filterPlayers should throw InvalidPlayerStatusException when status is invalid`() {
        val filter = PlayerFilter(status = "invalid-status")

        val exception = assertThrows<InvalidPlayerStatusException> {
            runBlocking { playerService.filterPlayers(filter).await().indefinitely() }
        }

        assertEquals("Invalid player status: invalid-status", exception.message)
        assertEquals("INVALID_PLAYER_STATUS", exception.code)
    }

    /*
    @Test
    fun `filterPlayers should retry when repository throws an exception`() {
        val filter = PlayerFilter(position = "Winger")
        val subscriptionCounter = AtomicInteger(0)

        `when`(repository.findByFilters(filter))
            .thenReturn(Uni.createFrom().failure(RuntimeException("Simulated error 1")))
            .thenReturn(Uni.createFrom().failure(RuntimeException("Simulated error 2")))
            .thenReturn(Uni.createFrom().item(players.filter { it.position == filter.position }))


        val result = playerService.filterPlayers(filter).await().indefinitely()

        assertEquals(3, subscriptionCounter.get())
        assertEquals(result.size, 1)
        verify(repository, times(3)).findByFilters(filter)
    }

    @Test
    fun `filterPlayers should throw exception when repository always throws an exception`() {
        val filter = PlayerFilter(position = "Winger")

        `when`(repository.findByFilters(filter)).thenReturn(Uni.createFrom().failure(RuntimeException("Simulated persistent error")))

        assertThrows<RuntimeException> {
            runBlocking {
                playerService.filterPlayers(filter).await().indefinitely()
            }
        }
    }*/
}