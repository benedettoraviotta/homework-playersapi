package io.playersapi.adapters.persistence

import io.playersapi.core.domain.PlayerStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InMemoryPlayerRepositoryTest {

    private val repository = InMemoryPlayerRepository()

    @Test
    fun `should return all players when findAll is called`() {
        val players = repository.findAll()

        assertEquals(5, players.size)
        assertTrue(players.any { it.name == "Rafael Leao" })
        assertTrue(players.any { it.name == "Filippo Inzaghi" })
    }

    @Test
    fun `should return filtered players by position`() {
        val players = repository.findByFilters(position = "Winger", null, null, null, null)

        assertEquals(1, players.size)
        assertEquals("Rafael Leao", players.first().name)
    }

    @Test
    fun `should return filtered players by minBirthYear`() {
        val players = repository.findByFilters(null, 1990, null, null, null)

        assertEquals(3, players.size)
        assertTrue(players.all { it.birthYear >= 1990 })
    }

    @Test
    fun `should return filtered players by maxBirthYear`() {
        val players = repository.findByFilters(null, null, 1980, null, null)

        assertEquals(2, players.size)
        assertTrue(players.all { it.birthYear <= 1980 })
    }

    @Test
    fun `should return filtered players by status`() {
        val players = repository.findByFilters(null, null, null, PlayerStatus.ACTIVE, null)

        assertEquals(3, players.size)
        assertTrue(players.all { it.status == PlayerStatus.ACTIVE })
    }

    @Test
    fun `should return filtered players by club`() {
        val players = repository.findByFilters(null, null, null, null, "Milan")

        assertEquals(1, players.size)
        assertEquals("Rafael Leao", players.first().name)
    }

    @Test
    fun `should return filtered players by multiple criteria`() {
        val players = repository.findByFilters("Winger", 1998, null, PlayerStatus.ACTIVE, "Milan")

        assertEquals(1, players.size)
        assertEquals("Rafael Leao", players.first().name)
    }

    @Test
    fun `should return empty list if no players match the filters`() {
        val players = repository.findByFilters("Goalkeeper", 1980, 1990, PlayerStatus.RETIRED, "Juventus")

        assertTrue(players.isEmpty())
    }
}
