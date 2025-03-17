package io.playersapi.adapters.web

import io.playersapi.application.dto.PlayerDTO
import io.playersapi.application.dto.PlayerFilterDTO
import io.playersapi.application.services.PlayerService
import io.playersapi.core.domain.PlayerStatus
import io.quarkus.test.InjectMock
import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import jakarta.ws.rs.core.Response
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`

@QuarkusTest
@TestHTTPEndpoint(PlayerController::class)
class PlayerControllerTest {

    @InjectMock
    private lateinit var playerService: PlayerService

    private val players = listOf(
        PlayerDTO(1, "Rafael Leao", "Winger", 1999, PlayerStatus.ACTIVE, "Milan"),
        PlayerDTO(2, "Gianluigi Buffon", "Goalkeeper", 1978, PlayerStatus.RETIRED, "Retired"),
        PlayerDTO(3, "Alessandro Bastoni", "Defender", 1999, PlayerStatus.ACTIVE, "Inter"),
        PlayerDTO(4, "Sandi Lovric", "Midfielder", 1998, PlayerStatus.ACTIVE, "Udinese"),
        PlayerDTO(5, "Filippo Inzaghi", "Forward", 1973, PlayerStatus.RETIRED, "Retired")
    )

    @Test
    fun `should return players filtered by status ACTIVE`() {
        `when` ( playerService.filterPlayers(
            PlayerFilterDTO(null, null, null, PlayerStatus.ACTIVE.name, null)
        )).thenReturn(players.filter { it.status == PlayerStatus.ACTIVE })

        given()
            .queryParam("status", "ACTIVE")
            .`when`()
            .get()
            .then()
            .statusCode(Response.Status.OK.statusCode)
            .body("size()", `is`(3))
            .body("[0].name", `is`("Rafael Leao"))
            .body("[1].name", `is`("Alessandro Bastoni"))
            .body("[2].name", `is`("Sandi Lovric"))
    }

    @Test
    fun `should return players filtered by status RETIRED`() {
        `when` ( playerService.filterPlayers(
            PlayerFilterDTO(null, null, null, PlayerStatus.RETIRED.name, null)
            )).thenReturn(players.filter { it.status == PlayerStatus.RETIRED })

        given()
            .queryParam("status", "RETIRED")
            .`when`()
            .get()
            .then()
            .statusCode(Response.Status.OK.statusCode)
            .body("size()", `is`(2))
            .body("[0].name", `is`("Gianluigi Buffon"))
            .body("[1].name", `is`("Filippo Inzaghi"))
    }

    @Test
    fun `should return players filtered by position Defender`() {
        `when` ( playerService.filterPlayers(
         PlayerFilterDTO("Defender", null, null, null, null)
        )).thenReturn(players.filter { it.position == "Defender" })

        given()
            .queryParam("position", "Defender")
            .`when`()
            .get()
            .then()
            .statusCode(Response.Status.OK.statusCode)
            .body("size()", `is`(1))
            .body("[0].name", `is`("Alessandro Bastoni"))
    }

    @Test
    fun `should return players filtered by min birth year 1998`() {
        `when` ( playerService.filterPlayers(
            PlayerFilterDTO(null, 1998, null, null, null)
        )).thenReturn(players.filter { it.birthYear >= 1998 })

        given()
            .queryParam("minBirthYear", 1998)
            .`when`()
            .get()
            .then()
            .statusCode(Response.Status.OK.statusCode)
            .body("size()", `is`(3))
            .body("[0].name", `is`("Rafael Leao"))
            .body("[1].name", `is`("Alessandro Bastoni"))
            .body("[2].name", `is`("Sandi Lovric"))
    }

    @Test
    fun `should return players filtered by max birth year 1978`() {
        `when` ( playerService.filterPlayers(
            PlayerFilterDTO(null, null, 1978, null, null)
        )).thenReturn(players.filter { it.birthYear <= 1978 })

        given()
            .queryParam("maxBirthYear", 1978)
            .`when`()
            .get()
            .then()
            .statusCode(Response.Status.OK.statusCode)
            .body("size()", `is`(2))
            .body("[0].name", `is`("Gianluigi Buffon"))
            .body("[1].name", `is`("Filippo Inzaghi"))
    }

    @Test
    fun `should return players filtered by club Milan`() {
        `when` ( playerService.filterPlayers(
            PlayerFilterDTO(null, null, null, null, "Milan")
        )).thenReturn(players.filter { it.club == "Milan" })

        given()
            .queryParam("club", "Milan")
            .`when`()
            .get()
            .then()
            .statusCode(Response.Status.OK.statusCode)
            .body("size()", `is`(1))
            .body("[0].name", `is`("Rafael Leao"))
    }

    @Test
    fun `should return players matching multiple filters`() {
        `when` ( playerService.filterPlayers(
            PlayerFilterDTO("Midfielder", 1995, 2000, PlayerStatus.ACTIVE.name, "Udinese")
        )).thenReturn(
                players.filter {
                    it.position == "Midfielder" && it.birthYear in 1995..2000 && it.status == PlayerStatus.ACTIVE && it.club == "Udinese"
                })

        given()
            .queryParam("position", "Midfielder")
            .queryParam("minBirthYear", 1995)
            .queryParam("maxBirthYear", 2000)
            .queryParam("status", "ACTIVE")
            .queryParam("club", "Udinese")
            .`when`()
            .get()
            .then()
            .statusCode(Response.Status.OK.statusCode)
            .body("size()", `is`(1))
            .body("[0].name", `is`("Sandi Lovric"))
    }

    @Test
    fun `should return empty list when no players match filters`() {
        `when` ( playerService.filterPlayers(
            PlayerFilterDTO("Striker", 2000, null, PlayerStatus.ACTIVE.name, "Juventus")
            )).thenReturn(emptyList())

        given()
            .queryParam("position", "Striker")
            .queryParam("minBirthYear", 2000)
            .queryParam("status", "ACTIVE")
            .queryParam("club", "Juventus")
            .`when`()
            .get()
            .then()
            .statusCode(Response.Status.OK.statusCode)
            .body("size()", `is`(0))
    }

    @Test
    fun `should return all players when no filters are provided`() {
        `when` ( playerService.filterPlayers(
            PlayerFilterDTO(null, null, null, null, null)
            )).thenReturn(players)

        given()
            .`when`()
            .get()
            .then()
            .statusCode(Response.Status.OK.statusCode)
            .body("size()", `is`(5))
    }

    @Test
    fun `should return all players when the only filter is unknown`() {
        `when` ( playerService.filterPlayers(
            PlayerFilterDTO(null, null, null, null, null)
        )).thenReturn(players)

        given()
            .queryParam("unknown", "filter")
            .`when`()
            .get()
            .then()
            .statusCode(Response.Status.OK.statusCode)
            .body("size()", `is`(5))
    }

    @Test
    fun `should return players filtered by club Milan ignoring unknown filter`() {
        `when` ( playerService.filterPlayers(
            PlayerFilterDTO(null, null, null, null, "Milan")
        )).thenReturn(players.filter { it.club == "Milan" })

        given()
            .queryParam("club", "Milan")
            .queryParam("unknown", "filter")
            .`when`()
            .get()
            .then()
            .statusCode(Response.Status.OK.statusCode)
            .body("size()", `is`(1))
            .body("[0].name", `is`("Rafael Leao"))
    }

    @Test
    fun `should return 400 for invalid status`() {
        given()
            .queryParam("status", "INVALID_STATUS")
            .`when`()
            .get()
            .then()
            .statusCode(Response.Status.BAD_REQUEST.statusCode)
            .body("message", `is` ("Invalid status value: INVALID_STATUS"))
            .body("code" , `is` ("INVALID_PLAYER_STATUS"))
    }

}
