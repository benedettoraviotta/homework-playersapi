package io.playersapi.core.ports

import io.playersapi.core.domain.Player
import io.playersapi.core.domain.PlayerStatus

interface PlayerRepository {
    fun findAll(): List<Player>
    fun findByFilters(
        position: String?,
        minBirthYear: Int?,
        maxBirthYear: Int?,
        status: PlayerStatus?,
        club: String?
    ): List<Player>
}