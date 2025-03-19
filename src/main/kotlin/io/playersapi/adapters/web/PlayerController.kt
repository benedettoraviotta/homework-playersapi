package io.playersapi.adapters.web

import io.playersapi.application.dto.PlayerFilter
import io.playersapi.application.services.PlayerService
import io.smallrye.mutiny.Uni
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.slf4j.LoggerFactory


@Path("/players")
@Produces(MediaType.APPLICATION_JSON)
class PlayerController @Inject constructor(private val playerService: PlayerService) {

    private val logger = LoggerFactory.getLogger(PlayerController::class.java)

    @GET
    fun getFilteredPlayers(
        // @BeanParam filter: PlayerFilterDTO da 404, problemi nel mappare i parametri
        @QueryParam("position") position: String?,
        @QueryParam("minBirthYear") minBirthYear: Int?,
        @QueryParam("maxBirthYear") maxBirthYear: Int?,
        @QueryParam("status") status: String?,
        @QueryParam("club") club: String?
    ): Uni<Response> {
        val filter = PlayerFilter(position, minBirthYear, maxBirthYear, status, club)

        logger.debug("Received request with filter: {}", filter)

        return playerService.filterPlayers(filter).map { players -> Response.ok(players).build() }
    }
}
