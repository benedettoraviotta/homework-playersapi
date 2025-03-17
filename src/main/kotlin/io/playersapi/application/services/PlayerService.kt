package io.playersapi.application.services

import io.playersapi.application.dto.PlayerDTO
import io.playersapi.application.dto.PlayerFilterDTO
import io.playersapi.core.ports.PlayerRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@ApplicationScoped
class PlayerService @Inject constructor(private val playerRepository: PlayerRepository) {

    fun filterPlayers(filter: PlayerFilterDTO): List<PlayerDTO> {
        val players = playerRepository.findByFilters(filter)

        return players.map { player ->
            PlayerDTO(
                id = player.id,
                name = player.name,
                position = player.position,
                birthYear = player.birthYear,
                status = player.status,
                club = player.club
            )
        }
    }
}
