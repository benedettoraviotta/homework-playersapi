package io.playersapi.adapters.web.errors

import jakarta.ws.rs.core.Response

data class ErrorResponse(
    val message: String,
    val status: Int,
    val code: String? = null
)

object ErrorCodes {

    val INVALID_PLAYER_STATUS = ErrorResponse("Invalid status value", Response.Status.BAD_REQUEST.statusCode, "INVALID_PLAYER_STATUS")
}

