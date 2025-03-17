package io.playersapi.adapters.persistence

import io.playersapi.core.domain.Player
import io.playersapi.core.domain.PlayerStatus
import io.playersapi.core.ports.PlayerRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class InMemoryPlayerRepository : PlayerRepository {

    private val players = listOf(
        Player(1, "Rafael Leao", "Winger", 1999, PlayerStatus.ACTIVE, "Milan"),
        Player(2, "Gianluigi Buffon", "Goalkeeper", 1978, PlayerStatus.RETIRED, "Retired"),
        Player(3, "Alessandro Bastoni", "Defender", 1999, PlayerStatus.ACTIVE, "Inter"),
        Player(4, "Sandi Lovric", "Midfielder", 1998, PlayerStatus.ACTIVE, "Udinese"),
        Player(5, "Filippo Inzaghi", "Forward", 1973, PlayerStatus.RETIRED, "Retired")
    )

    override fun findAll(): List<Player> = players

    override fun findByFilters(
        position: String?,
        minBirthYear: Int?,
        maxBirthYear: Int?,
        status: PlayerStatus?,
        club: String?
    ): List<Player> {
        return players.filter { player ->
            (position == null || player.position == position) &&
                    (minBirthYear == null || player.birthYear >= minBirthYear) &&
                    (maxBirthYear == null || player.birthYear <= maxBirthYear) &&
                    (status == null || player.status == status) &&
                    (club == null || player.club == club)
        }
    }
}
