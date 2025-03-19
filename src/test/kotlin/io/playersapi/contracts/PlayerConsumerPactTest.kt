package io.playersapi.contracts

import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.core.model.V4Pact
import au.com.dius.pact.core.model.annotations.Pact
import au.com.dius.pact.core.model.annotations.PactDirectory
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(PactConsumerTestExt::class)
@PactTestFor(providerName = "PlayerProvider", port = "8085")
@PactDirectory("src/test/resources/pacts")
@QuarkusTest
class PlayerConsumerPactTest {

    @BeforeEach
    fun setup() {
        RestAssured.baseURI = "http://localhost:8085"
    }

    @Pact(consumer = "PlayerConsumer", provider = "PlayerProvider")
    fun createPactNoFilters(builder: PactDslWithProvider): V4Pact {
        return builder
            .given("players exist")
            .uponReceiving("a request to get all players without filter")
            .path("/players")
            .method("GET")
            .willRespondWith()
            .status(200)
            .headers(mapOf("Content-Type" to "application/json"))
            .body("""
                [
                    {"id": 1, "name": "Rafael Leao", "position": "Winger", "birthYear": 1999, "status": "ACTIVE", "club": "Milan"},
                    {"id": 2, "name": "Gianluigi Buffon", "position": "Goalkeeper", "birthYear": 1978, "status": "RETIRED", "club": "Retired"},
                    {"id": 3, "name": "Alessandro Bastoni", "position": "Defender", "birthYear": 1999, "status": "ACTIVE", "club": "Inter"},
                    {"id": 4, "name": "Sandi Lovric", "position": "Midfielder", "birthYear": 1998, "status": "ACTIVE", "club": "Udinese"},
                    {"id": 5, "name": "Filippo Inzaghi", "position": "Forward", "birthYear": 1973, "status": "RETIRED", "club": "Retired"}
                ]
            """.trimIndent())
            .toPact(V4Pact::class.java)
    }

    @Test
    @PactTestFor(pactMethod = "createPactNoFilters")
    fun `test getFilteredPlayers with no filter` () {
        RestAssured.given()
            .`when`()
            .get("/players")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("[0].name", equalTo("Rafael Leao"))
            .body("[1].name", equalTo("Gianluigi Buffon"))
            .body("[2].name", equalTo("Alessandro Bastoni"))
            .body("[3].name", equalTo("Sandi Lovric"))
            .body("[4].name", equalTo("Filippo Inzaghi"))
    }

    @Pact(consumer = "PlayerConsumer", provider = "PlayerProvider")
    fun createPactForActivePlayers(builder: PactDslWithProvider): V4Pact {
        return builder
            .given("players exist")
            .uponReceiving("a request to get players filtered by status ACTIVE")
            .path("/players")
            .query("status=ACTIVE")
            .method("GET")
            .willRespondWith()
            .status(200)
            .headers(mapOf("Content-Type" to "application/json"))
            .body("""
                [
                    {"id": 1, "name": "Rafael Leao", "position": "Winger", "birthYear": 1999, "status": "ACTIVE", "club": "Milan"},
                    {"id": 3, "name": "Alessandro Bastoni", "position": "Defender", "birthYear": 1999, "status": "ACTIVE", "club": "Inter"},
                    {"id": 4, "name": "Sandi Lovric", "position": "Midfielder", "birthYear": 1998, "status": "ACTIVE", "club": "Udinese"}
                ]
            """.trimIndent())
            .toPact(V4Pact::class.java)
    }

    @Test
    @PactTestFor(pactMethod = "createPactForActivePlayers")
    fun `test getFilteredPlayers with ACTIVE status` () {
        RestAssured.given()
            .queryParam("status", "ACTIVE")
            .`when`()
            .get("/players")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("[0].name", equalTo("Rafael Leao"))
            .body("[1].name", equalTo("Alessandro Bastoni"))
            .body("[2].name", equalTo("Sandi Lovric"))
    }

    @Pact(consumer = "PlayerConsumer", provider = "PlayerProvider")
    fun createPactForRetiredPlayers(builder: PactDslWithProvider): V4Pact {
        return builder
            .given("players exist")
            .uponReceiving("a request to get players filtered by status RETIRED")
            .path("/players")
            .query("status=RETIRED")
            .method("GET")
            .willRespondWith()
            .status(200)
            .headers(mapOf("Content-Type" to "application/json"))
            .body("""
                [
                    {"id": 2, "name": "Gianluigi Buffon", "position": "Goalkeeper", "birthYear": 1978, "status": "RETIRED", "club": "Retired"},
                    {"id": 5, "name": "Filippo Inzaghi", "position": "Forward", "birthYear": 1973, "status": "RETIRED", "club": "Retired"}
                ]
            """.trimIndent())
            .toPact(V4Pact::class.java)
    }

    @Test
    @PactTestFor(pactMethod = "createPactForRetiredPlayers")
    fun `test getFilteredPlayers with RETIRED status` () {
        RestAssured.given()
            .queryParam("status", "RETIRED")
            .`when`()
            .get("/players")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("[0].name", equalTo("Gianluigi Buffon"))
            .body("[1].name", equalTo("Filippo Inzaghi"))
    }

    @Pact(consumer = "PlayerConsumer", provider = "PlayerProvider")
    fun createPactForPosition(builder: PactDslWithProvider): V4Pact {
        return builder
            .given("players exist")
            .uponReceiving("a request to get players filtered by position Defender")
            .path("/players")
            .query("position=Defender")
            .method("GET")
            .willRespondWith()
            .status(200)
            .headers(mapOf("Content-Type" to "application/json"))
            .body("""
                [
                    {"id": 3, "name": "Alessandro Bastoni", "position": "Defender", "birthYear": 1999, "status": "ACTIVE", "club": "Inter"}
                ]
            """.trimIndent())
            .toPact(V4Pact::class.java)
    }

    @Test
    @PactTestFor(pactMethod = "createPactForPosition")
    fun `test getFilteredPlayers with Defender position` () {
        RestAssured.given()
            .queryParam("position", "Defender")
            .`when`()
            .get("/players")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("[0].name", equalTo("Alessandro Bastoni"))
    }

    @Pact(consumer = "PlayerConsumer", provider = "PlayerProvider")
    fun createPactForMinBirthYear(builder: PactDslWithProvider): V4Pact {
        return builder
            .given("players exist")
            .uponReceiving("a request to get players with min birth year 1998")
            .path("/players")
            .query("minBirthYear=1998")
            .method("GET")
            .willRespondWith()
            .status(200)
            .headers(mapOf("Content-Type" to "application/json"))
            .body("""
                [
                    {"id": 1, "name": "Rafael Leao", "position": "Winger", "birthYear": 1999, "status": "ACTIVE", "club": "Milan"},
                    {"id": 3, "name": "Alessandro Bastoni", "position": "Defender", "birthYear": 1999, "status": "ACTIVE", "club": "Inter"},
                    {"id": 4, "name": "Sandi Lovric", "position": "Midfielder", "birthYear": 1998, "status": "ACTIVE", "club": "Udinese"}
                ]
            """.trimIndent())
            .toPact(V4Pact::class.java)
    }

    @Test
    @PactTestFor(pactMethod = "createPactForMinBirthYear")
    fun `test getFilteredPlayers with min birth year filter` () {
        RestAssured.given()
            .queryParam("minBirthYear", "1998")
            .`when`()
            .get("/players")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("[0].name", equalTo("Rafael Leao"))
            .body("[1].name", equalTo("Alessandro Bastoni"))
            .body("[2].name", equalTo("Sandi Lovric"))
    }

    @Pact(consumer = "PlayerConsumer", provider = "PlayerProvider")
    fun createPactForMaxBirthYear(builder: PactDslWithProvider): V4Pact {
        return builder
            .given("players exist")
            .uponReceiving("a request to get players with max birth year 1978")
            .path("/players")
            .query("maxBirthYear=1978")
            .method("GET")
            .willRespondWith()
            .status(200)
            .headers(mapOf("Content-Type" to "application/json"))
            .body("""
                [
                    {"id": 2, "name": "Gianluigi Buffon", "position": "Goalkeeper", "birthYear": 1978, "status": "RETIRED", "club": "Retired"},
                    {"id": 5, "name": "Filippo Inzaghi", "position": "Forward", "birthYear": 1973, "status": "RETIRED", "club": "Retired"}
                ]
            """.trimIndent())
            .toPact(V4Pact::class.java)
    }

    @Test
    @PactTestFor(pactMethod = "createPactForMaxBirthYear")
    fun `test getFilteredPlayers with max birth year filter` () {
        RestAssured.given()
            .queryParam("maxBirthYear", "1978")
            .`when`()
            .get("/players")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("[0].name", equalTo("Gianluigi Buffon"))
            .body("[1].name", equalTo("Filippo Inzaghi"))
    }

    @Pact(consumer = "PlayerConsumer", provider = "PlayerProvider")
    fun createPactForClub(builder: PactDslWithProvider): V4Pact {
        return builder
            .given("players exist")
            .uponReceiving("a request to get players filtered by club Milan")
            .path("/players")
            .query("club=Milan")
            .method("GET")
            .willRespondWith()
            .status(200)
            .headers(mapOf("Content-Type" to "application/json"))
            .body("""
                [
                    {"id": 1, "name": "Rafael Leao", "position": "Winger", "birthYear": 1999, "status": "ACTIVE", "club": "Milan"}
                ]
            """.trimIndent())
            .toPact(V4Pact::class.java)
    }

    @Test
    @PactTestFor(pactMethod = "createPactForClub")
    fun `test getFilteredPlayers with club filter` () {
        RestAssured.given()
            .queryParam("club", "Milan")
            .`when`()
            .get("/players")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("[0].name", equalTo("Rafael Leao"))
    }

    @Pact(consumer = "PlayerConsumer", provider = "PlayerProvider")
    fun createPactForStatusAndPosition(builder: PactDslWithProvider): V4Pact {
        return builder
            .given("There are players with specific status and position")
            .uponReceiving("Request players filtered by ACTIVE status and Defender position")
            .path("/players")
            .query("status=ACTIVE&position=Defender")
            .method("GET")
            .willRespondWith()
            .status(200)
            .headers(mapOf("Content-Type" to "application/json"))
            .body(
                """
            [
                {"id": 3, "name": "Alessandro Bastoni", "position": "Defender", "birthYear": 1999, "status": "ACTIVE", "club": "Inter"}
            ]
            """.trimIndent()
            )
            .toPact(V4Pact::class.java)
    }

    @Test
    @PactTestFor(pactMethod = "createPactForStatusAndPosition")
    fun `test getFilteredPlayers with Defender position and ACTIVE status` () {
        RestAssured.given()
            .queryParams(mapOf(
                "status" to "ACTIVE",
                "position" to "Defender"
            ))
            .`when`()
            .get("/players")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("[0].name", equalTo("Alessandro Bastoni"))
    }

    @Pact(consumer = "PlayerConsumer", provider = "PlayerProvider")
    fun createPactForStatusMinBirthYearAndClub(builder: PactDslWithProvider): V4Pact {
        return builder
            .given("There are players with specific status, birth year, and club")
            .uponReceiving("Request players filtered by ACTIVE status, minBirthYear, and club")
            .path("/players")
            .query("status=ACTIVE&minBirthYear=1995&club=Milan")
            .method("GET")
            .willRespondWith()
            .status(200)
            .headers(mapOf("Content-Type" to "application/json"))
            .body(
                """
            [
                {"id": 1, "name": "Rafael Leao", "position": "Winger", "birthYear": 1999, "status": "ACTIVE", "club": "Milan"}
            ]
            """.trimIndent()
            )
            .toPact(V4Pact::class.java)
    }

    @Test
    @PactTestFor(pactMethod = "createPactForStatusMinBirthYearAndClub")
    fun `200 - test getFilteredPlayers with ACTIVE status and filter on minBirthYear and club` () {
        RestAssured.given()
            .queryParams(mapOf(
                "status" to "ACTIVE",
                "minBirthYear" to "1995",
                "club" to "Milan"
            ))
            .`when`()
            .get("/players")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("[0].name", equalTo("Rafael Leao"))
    }

    @Pact(consumer = "PlayerConsumer", provider = "PlayerProvider")
    fun createPactForBirthYearRange(builder: PactDslWithProvider): V4Pact {
        return builder
            .given("There are players with different birth years")
            .uponReceiving("Request players filtered by minBirthYear and maxBirthYear")
            .path("/players")
            .query("minBirthYear=1999&maxBirthYear=2005")
            .method("GET")
            .willRespondWith()
            .status(200)
            .headers(mapOf("Content-Type" to "application/json"))
            .body(
                """
            [
                {"id": 1, "name": "Rafael Leao", "position": "Winger", "birthYear": 1999, "status": "ACTIVE", "club": "Milan"},
                {"id": 3, "name": "Alessandro Bastoni", "position": "Defender", "birthYear": 1999, "status": "ACTIVE", "club": "Inter"}
            ]
            """.trimIndent()
            )
            .toPact(V4Pact::class.java)
    }

    @Test
    @PactTestFor(pactMethod = "createPactForBirthYearRange")
    fun `200 - test getFilteredPlayers with birth year range` () {
        RestAssured.given()
            .queryParams(mapOf(
                "minBirthYear" to "1999",
                "maxBirthYear" to "2005",
            ))
            .`when`()
            .get("/players")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("[0].name", equalTo("Rafael Leao"))
            .body("[1].name", equalTo("Alessandro Bastoni"))
    }

    @Pact(consumer = "PlayerConsumer", provider = "PlayerProvider")
    fun createPactForAllFilters(builder: PactDslWithProvider): V4Pact {
        return builder
            .given("There are players with various attributes")
            .uponReceiving("Request players filtered by status, position, minBirthYear, maxBirthYear, and club")
            .path("/players")
            .query("status=ACTIVE&position=Defender&minBirthYear=1990&maxBirthYear=2000&club=Inter")
            .method("GET")
            .willRespondWith()
            .status(200)
            .headers(mapOf("Content-Type" to "application/json"))
            .body(
                """
            [
                {"id": 3, "name": "Alessandro Bastoni", "position": "Defender", "birthYear": 1999, "status": "ACTIVE", "club": "Inter"}
            ]
            """.trimIndent()
            )
            .toPact(V4Pact::class.java)
    }

    @Test
    @PactTestFor(pactMethod = "createPactForAllFilters")
    fun `200 - test getFilteredPlayers with all filters` () {
        RestAssured.given()
            .queryParams(mapOf(
                "status" to "ACTIVE",
                "position" to "Defender",
                "minBirthYear" to "1990",
                "maxBirthYear" to "2000",
                "club" to "Inter",
            ))
            .`when`()
            .get("/players")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("[0].name", equalTo("Alessandro Bastoni"))
    }

    @Pact(consumer = "PlayerConsumer", provider = "PlayerProvider")
    fun createPactForOneUnknownFilter(builder: PactDslWithProvider): V4Pact {
        return builder
            .given("Return all players without filter")
            .uponReceiving("Request players with wrong filter, ignore it and return all ")
            .path("/players")
            .query("unknown=filter")
            .method("GET")
            .willRespondWith()
            .status(200)
            .headers(mapOf("Content-Type" to "application/json"))
            .body("""
                [
                    {"id": 1, "name": "Rafael Leao", "position": "Winger", "birthYear": 1999, "status": "ACTIVE", "club": "Milan"},
                    {"id": 2, "name": "Gianluigi Buffon", "position": "Goalkeeper", "birthYear": 1978, "status": "RETIRED", "club": "Retired"},
                    {"id": 3, "name": "Alessandro Bastoni", "position": "Defender", "birthYear": 1999, "status": "ACTIVE", "club": "Inter"},
                    {"id": 4, "name": "Sandi Lovric", "position": "Midfielder", "birthYear": 1998, "status": "ACTIVE", "club": "Udinese"},
                    {"id": 5, "name": "Filippo Inzaghi", "position": "Forward", "birthYear": 1973, "status": "RETIRED", "club": "Retired"}
                ]
            """.trimIndent())
            .toPact(V4Pact::class.java)
    }

    @Test
    @PactTestFor(pactMethod = "createPactForOneUnknownFilter")
    fun `200 - test getFilteredPlayers with unknown filter` () {
        RestAssured.given()
            .queryParams("unknown","filter")
            .`when`()
            .get("/players")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("[0].name", equalTo("Rafael Leao"))
            .body("[1].name", equalTo("Gianluigi Buffon"))
            .body("[2].name", equalTo("Alessandro Bastoni"))
            .body("[3].name", equalTo("Sandi Lovric"))
            .body("[4].name", equalTo("Filippo Inzaghi"))
    }

    @Pact(consumer = "PlayerConsumer", provider = "PlayerProvider")
    fun createPactForInvalidStatus(builder: PactDslWithProvider): V4Pact {
        return builder
            .given("players exist")
            .uponReceiving("a request with invalid status")
            .path("/players")
            .query("status=WRONG")
            .method("GET")
            .willRespondWith()
            .status(400)
            .headers(mapOf("Content-Type" to "application/json"))
            .body("""
                {
                    "message":"Invalid status value: WRONG",
                    "status":400,
                    "code":"INVALID_PLAYER_STATUS"
                }
            """.trimIndent()
            )
            .toPact(V4Pact::class.java)
    }

    @Test
    @PactTestFor(pactMethod = "createPactForInvalidStatus")
    fun `400 - test getFilteredPlayers with invalid status filter` () {
        RestAssured.given()
            .queryParams("status","WRONG")
            .`when`()
            .get("/players")
            .then()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("message", `is` ("Invalid status value: WRONG"))
            .body("code" , `is` ("INVALID_PLAYER_STATUS"))
    }
}
