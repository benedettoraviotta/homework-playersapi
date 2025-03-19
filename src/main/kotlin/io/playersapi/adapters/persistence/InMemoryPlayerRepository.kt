package io.playersapi.adapters.persistence

import io.playersapi.application.dto.PlayerFilter
import io.playersapi.core.domain.Player
import io.playersapi.core.domain.PlayerStatus
import io.playersapi.core.ports.PlayerRepository
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import org.slf4j.LoggerFactory

@ApplicationScoped
class InMemoryPlayerRepository : PlayerRepository {

    private val logger = LoggerFactory.getLogger(InMemoryPlayerRepository::class.java)

    private val players = listOf(
        Player(1, "Rafael Leao", "Winger", 1999, PlayerStatus.ACTIVE, "Milan"),
        Player(2, "Gianluigi Buffon", "Goalkeeper", 1978, PlayerStatus.RETIRED, "Retired"),
        Player(3, "Alessandro Bastoni", "Defender", 1999, PlayerStatus.ACTIVE, "Inter"),
        Player(4, "Sandi Lovric", "Midfielder", 1998, PlayerStatus.ACTIVE, "Udinese"),
        Player(5, "Filippo Inzaghi", "Forward", 1973, PlayerStatus.RETIRED, "Retired")
    )

    override fun findAll(): Uni<List<Player>> {
        logger.debug("Return all players without filters")
        return Uni.createFrom().item(players)
    }

    override fun findByFilters(filter: PlayerFilter): Uni<List<Player>> {
        val players = players.filter { player ->
            (filter.position == null || player.position == filter.position) &&
                    (filter.minBirthYear == null || player.birthYear >= filter.minBirthYear) &&
                    (filter.maxBirthYear == null || player.birthYear <= filter.maxBirthYear) &&
                    (filter.status == null || player.status == filter.statusEnum) &&
                    (filter.club == null || player.club == filter.club)
        }
        logger.debug("Found {} players", players.size)
        return Uni.createFrom().item(players)
    }
}
