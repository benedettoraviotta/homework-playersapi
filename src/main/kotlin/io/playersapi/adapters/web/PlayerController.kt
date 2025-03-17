package io.playersapi.adapters.web

import io.playersapi.adapters.web.errors.ErrorCodes
import io.playersapi.application.dto.PlayerDTO
import io.playersapi.application.dto.PlayerFilterDTO
import io.playersapi.application.services.PlayerService
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/players")
@Produces(MediaType.APPLICATION_JSON)
class PlayerController @Inject constructor(private val playerService: PlayerService) {

    @GET
    fun getFilteredPlayers(
        // @BeanParam filter: PlayerFilterDTO da 404, problemi nel mappare i parametri
        @QueryParam("position") position: String?,
        @QueryParam("minBirthYear") minBirthYear: Int?,
        @QueryParam("maxBirthYear") maxBirthYear: Int?,
        @QueryParam("status") status: String?,
        @QueryParam("club") club: String?
    ): Response {
        val filter = PlayerFilterDTO(position, minBirthYear, maxBirthYear, status, club)
        if (filter.status != null && filter.statusEnum == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(ErrorCodes.INVALID_PLAYER_STATUS.copy(message = "Invalid status value: ${filter.status}"))
                .build()
        }

        val players: List<PlayerDTO> = playerService.filterPlayers(filter)
        return Response.ok(players).build()
    }
}
