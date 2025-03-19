package io.playersapi.adapters.web.errors

import jakarta.ws.rs.core.Response


open class PlayerException(
    message: String,
    val status: Response.Status,
    val code: String? = null
) : RuntimeException(message)

class InvalidPlayerStatusException(status: String) : PlayerException(
    message = "Invalid player status: $status",
    status = Response.Status.BAD_REQUEST,
    code = ErrorCodes.INVALID_PLAYER_STATUS
)