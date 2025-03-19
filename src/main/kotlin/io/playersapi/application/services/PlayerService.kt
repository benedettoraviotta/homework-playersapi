package io.playersapi.application.services

import io.playersapi.adapters.web.errors.InvalidPlayerStatusException
import io.playersapi.application.dto.PlayerResponse
import io.playersapi.application.dto.PlayerFilter
import io.playersapi.core.ports.PlayerRepository
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.slf4j.LoggerFactory

@ApplicationScoped
class PlayerService @Inject constructor(private val playerRepository: PlayerRepository) {

    private val logger = LoggerFactory.getLogger(PlayerService::class.java)

     fun filterPlayers(filter: PlayerFilter): Uni<List<PlayerResponse>> {
        logger.debug("Filtering players with: {}", filter)

        if (filter.status != null && filter.statusEnum == null) {
            return Uni.createFrom().failure(InvalidPlayerStatusException(filter.status))
        }

        return playerRepository.findByFilters(filter)
            // .onFailure().retry().atMost(3) non sto riuscendo a testarlo, problemi col mock del repository
            .map { players ->
                players.map { player ->
                    PlayerResponse(
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
}
