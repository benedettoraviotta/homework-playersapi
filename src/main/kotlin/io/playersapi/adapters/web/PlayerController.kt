package io.playersapi.adapters.web

import io.playersapi.application.dto.PlayerDTO
import io.playersapi.application.services.PlayerService
import io.playersapi.core.domain.PlayerStatus
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/players")
class PlayerController @Inject constructor(private val playerService: PlayerService) {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun getFilteredPlayers(
        @QueryParam("position") position: String?,
        @QueryParam("minBirthYear") minBirthYear: Int?,
        @QueryParam("maxBirthYear") maxBirthYear: Int?,
        @QueryParam("status") status: String?,
        @QueryParam("club") club: String?
    ): Response {
        val statusEnum: PlayerStatus? = status?.takeIf {
            runCatching { PlayerStatus.valueOf(it) }.isSuccess
        }?.let { PlayerStatus.valueOf(it) }

        if (statusEnum == null && status != null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("Invalid status value: $status")
                .build()
        }

        val players: List<PlayerDTO> = playerService.filterPlayers(position, minBirthYear, maxBirthYear, statusEnum, club)
        return Response.ok(players).build()
    }
}
