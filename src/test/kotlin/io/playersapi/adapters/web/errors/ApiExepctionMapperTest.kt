package io.playersapi.adapters.web.errors

import jakarta.ws.rs.core.Response
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ApiExceptionMapperTest {

    private val mapper = ApiExceptionMapper()

    @Test
    fun `toResponse should map PlayerException to a BAD_REQUEST response`() {
        val exception = InvalidPlayerStatusException("invalid-status")
        val response = mapper.toResponse(exception)

        assertEquals(Response.Status.BAD_REQUEST.statusCode, response.status)

        val entity = response.entity as ErrorResponse
        assertEquals("Invalid player status: invalid-status", entity.message)
        assertEquals(ErrorCodes.INVALID_PLAYER_STATUS, entity.code)
    }

    @Test
    fun `toResponse should map generic Exception to an INTERNAL_SERVER_ERROR response`() {
        val exception = RuntimeException("Generic error")

        val response = mapper.toResponse(exception)

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.statusCode, response.status)

        val entity = response.entity as ErrorResponse
        assertEquals("An unexpected error occurred.", entity.message)
        assertEquals(ErrorCodes.INTERNAL_ERROR, entity.code)
    }
}