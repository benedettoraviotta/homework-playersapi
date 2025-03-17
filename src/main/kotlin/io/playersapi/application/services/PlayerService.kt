package io.playersapi.application.services

import io.playersapi.application.dto.PlayerDTO
import io.playersapi.core.domain.PlayerStatus
import io.playersapi.core.ports.PlayerRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@ApplicationScoped
class PlayerService @Inject constructor(private val playerRepository: PlayerRepository) {

    fun findAll(): List<PlayerDTO> {
        return playerRepository.findAll().map { player ->
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

    fun filterPlayers(
        position: String?,
        minBirthYear: Int?,
        maxBirthYear: Int?,
        status: PlayerStatus?,
        club: String?
    ): List<PlayerDTO> {
        if (position == null && minBirthYear == null && maxBirthYear == null && status == null && club == null) {
            return findAll()
        }

        return playerRepository.findByFilters(position, minBirthYear, maxBirthYear, status, club)
            .map { player ->
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
