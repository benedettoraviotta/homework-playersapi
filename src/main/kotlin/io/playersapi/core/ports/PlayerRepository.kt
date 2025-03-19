package io.playersapi.core.ports

import io.playersapi.application.dto.PlayerFilter
import io.playersapi.core.domain.Player
import io.smallrye.mutiny.Uni

interface PlayerRepository {
    fun findAll(): Uni<List<Player>>
    fun findByFilters(filter: PlayerFilter): Uni<List<Player>>
}