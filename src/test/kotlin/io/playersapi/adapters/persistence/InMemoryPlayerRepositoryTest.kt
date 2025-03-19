package io.playersapi.adapters.persistence

import io.playersapi.application.dto.PlayerFilter
import io.smallrye.mutiny.coroutines.awaitSuspending
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InMemoryPlayerRepositoryTest {

    private val repository = InMemoryPlayerRepository()

    @Test
    fun `should return all players when findAll is called`() {
        val players = runBlocking { repository.findByFilters(PlayerFilter()).awaitSuspending() }

        assertEquals(5, players.size)
        assertTrue(players.any { it.name == "Rafael Leao" })
        assertTrue(players.any { it.name == "Filippo Inzaghi" })
    }

    @Test
    fun `should return filtered players by position`() {
        val players = runBlocking { repository.findByFilters(PlayerFilter(position = "Winger")).awaitSuspending() }

        assertEquals(1, players.size)
        assertEquals("Rafael Leao", players.first().name)
    }

    @Test
    fun `should return filtered players by minBirthYear`() {
        val players = runBlocking { repository.findByFilters(PlayerFilter(minBirthYear = 1990)).awaitSuspending() }

        assertEquals(3, players.size)
        assertTrue(players.all { it.birthYear >= 1990 })
    }

    @Test
    fun `should return filtered players by maxBirthYear`() {
        val players = runBlocking { repository.findByFilters(PlayerFilter(maxBirthYear = 1980)).awaitSuspending() }

        assertEquals(2, players.size)
        assertTrue(players.all { it.birthYear <= 1980 })
    }

    @Test
    fun `should return filtered players by status`() {
        val players = runBlocking { repository.findByFilters(PlayerFilter(status = "ACTIVE")).awaitSuspending() }


        assertEquals(3, players.size)
        assertTrue(players.all { it.status.name == "ACTIVE" })
    }

    @Test
    fun `should return filtered players by club`() {
        val players = runBlocking { repository.findByFilters(PlayerFilter(club = "Milan")).awaitSuspending() }

        assertEquals(1, players.size)
        assertEquals("Rafael Leao", players.first().name)
    }

    @Test
    fun `should return filtered players by multiple criteria`() {
        val players = runBlocking { repository.findByFilters(PlayerFilter(position = "Winger", minBirthYear = 1998, status = "ACTIVE", club = "Milan")).awaitSuspending() }


        assertEquals(1, players.size)
        assertEquals("Rafael Leao", players.first().name)
    }

    @Test
    fun `should return empty list if no players match the filters`() {
        val players = runBlocking { repository.findByFilters(PlayerFilter(position = "Goalkeeper", minBirthYear = 1980, maxBirthYear = 1990, status = "RETIRED", club = "Juventus")).awaitSuspending() }

        assertTrue(players.isEmpty())
    }
}