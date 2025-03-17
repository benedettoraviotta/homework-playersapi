package io.playersapi.core.ports

import io.playersapi.application.dto.PlayerFilterDTO
import io.playersapi.core.domain.PlayerResource
import io.playersapi.core.domain.PlayerStatus

interface PlayerRepository {
    fun findAll(): List<PlayerResource>
    fun findByFilters(filter: PlayerFilterDTO): List<PlayerResource>
}