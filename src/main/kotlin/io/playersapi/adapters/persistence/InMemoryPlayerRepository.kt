package io.playersapi.adapters.persistence

import io.playersapi.application.dto.PlayerFilterDTO
import io.playersapi.core.domain.PlayerResource
import io.playersapi.core.domain.PlayerStatus
import io.playersapi.core.ports.PlayerRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class InMemoryPlayerRepository : PlayerRepository {

    private val playerResources = listOf(
        PlayerResource(1, "Rafael Leao", "Winger", 1999, PlayerStatus.ACTIVE, "Milan"),
        PlayerResource(2, "Gianluigi Buffon", "Goalkeeper", 1978, PlayerStatus.RETIRED, "Retired"),
        PlayerResource(3, "Alessandro Bastoni", "Defender", 1999, PlayerStatus.ACTIVE, "Inter"),
        PlayerResource(4, "Sandi Lovric", "Midfielder", 1998, PlayerStatus.ACTIVE, "Udinese"),
        PlayerResource(5, "Filippo Inzaghi", "Forward", 1973, PlayerStatus.RETIRED, "Retired")
    )

    override fun findAll(): List<PlayerResource> = playerResources

    override fun findByFilters(filter: PlayerFilterDTO): List<PlayerResource> {
        return playerResources.filter { player ->
            (filter.position == null || player.position == filter.position) &&
                    (filter.minBirthYear == null || player.birthYear >= filter.minBirthYear) &&
                    (filter.maxBirthYear == null || player.birthYear <= filter.maxBirthYear) &&
                    (filter.status == null || player.status == filter.statusEnum) &&
                    (filter.club == null || player.club == filter.club)
        }
    }
}
