package io.playersapi.adapters.web

import io.playersapi.adapters.web.errors.ErrorCodes
import io.playersapi.adapters.web.errors.InvalidPlayerStatusException
import io.playersapi.application.dto.PlayerFilter
import io.playersapi.application.services.PlayerService
import io.playersapi.testutils.TestData
import io.quarkus.test.InjectMock
import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.smallrye.mutiny.Uni
import jakarta.ws.rs.core.Response
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.stream.Stream

@QuarkusTest
@TestHTTPEndpoint(PlayerController::class)
class PlayerControllerTest {

    @InjectMock
    private lateinit var playerService: PlayerService

    @ParameterizedTest
    @MethodSource("filterArguments")
    fun `should return players based on filters`(
        filter: PlayerFilter,
        expectedStatusCode: Int,
        expectedSize: Int,
        expectedNames: List<String>?
    ) {
        if (expectedStatusCode == Response.Status.OK.statusCode) {
            `when`(playerService.filterPlayers(filter)).thenReturn(
                Uni.createFrom().item(
                    playersResponse.filter {
                        (filter.position == null || it.position == filter.position) &&
                                (filter.minBirthYear == null || it.birthYear >= filter.minBirthYear!!) &&
                                (filter.maxBirthYear == null || it.birthYear <= filter.maxBirthYear!!) &&
                                (filter.status == null || it.status.name == filter.status) && //Usa filter.status
                                (filter.club == null || it.club == filter.club)
                    }
                )
            )
        } else {
            `when`(playerService.filterPlayers(filter)).thenReturn(
                Uni.createFrom().failure(InvalidPlayerStatusException("INVALID_STATUS"))
            )
        }

        val request = given()
        filter.position?.let { request.queryParam("position", it) }
        filter.minBirthYear?.let { request.queryParam("minBirthYear", it) }
        filter.maxBirthYear?.let { request.queryParam("maxBirthYear", it) }
        filter.status?.let { request.queryParam("status", it) }
        filter.club?.let { request.queryParam("club", it) }

        request.`when`().get()
            .then()
            .statusCode(expectedStatusCode)
            .apply {
                if (expectedStatusCode == Response.Status.OK.statusCode) {
                    body("size()", `is`(expectedSize))
                    expectedNames?.forEachIndexed { index, name ->
                        body("[$index].name", `is`(name))
                    }
                } else {
                    body("message", `is`("Invalid player status: INVALID_STATUS"))
                    body("code", `is`(ErrorCodes.INVALID_PLAYER_STATUS))
                }
            }

        Mockito.verify(playerService).filterPlayers(filter)
        Mockito.verifyNoMoreInteractions(playerService)
    }

    companion object {
        val playersResponse = TestData.playersResponse

        @JvmStatic
        fun filterArguments(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(PlayerFilter(null, null, null, null, null), Response.Status.OK.statusCode, 5, listOf("Rafael Leao", "Gianluigi Buffon", "Alessandro Bastoni", "Sandi Lovric", "Filippo Inzaghi")),
                Arguments.of(PlayerFilter("Winger", null, null, null, null), Response.Status.OK.statusCode, 1, listOf("Rafael Leao")),
                Arguments.of(PlayerFilter(null, 1998, null, null, null), Response.Status.OK.statusCode, 3, listOf("Rafael Leao", "Alessandro Bastoni", "Sandi Lovric")),
                Arguments.of(PlayerFilter(null, null, 1978, null, null), Response.Status.OK.statusCode, 2, listOf("Gianluigi Buffon", "Filippo Inzaghi")),
                Arguments.of(PlayerFilter(null, null, null, "ACTIVE", null), Response.Status.OK.statusCode, 3, listOf("Rafael Leao", "Alessandro Bastoni", "Sandi Lovric")),
                Arguments.of(PlayerFilter(null, null, null, "RETIRED", null), Response.Status.OK.statusCode, 2, listOf("Gianluigi Buffon", "Filippo Inzaghi")),
                Arguments.of(PlayerFilter(null, null, null, null, "Milan"), Response.Status.OK.statusCode, 1, listOf("Rafael Leao")),
                Arguments.of(PlayerFilter("Midfielder", 1995, 2000, "ACTIVE", "Udinese"), Response.Status.OK.statusCode, 1, listOf("Sandi Lovric")),
                Arguments.of(PlayerFilter("Striker", 2000, null, "ACTIVE", "Juventus"), Response.Status.OK.statusCode, 0, emptyList<String>()),
                Arguments.of(PlayerFilter(null, null, null, "INVALID_STATUS", null), Response.Status.BAD_REQUEST.statusCode, 0, null)
            )
        }
    }
}