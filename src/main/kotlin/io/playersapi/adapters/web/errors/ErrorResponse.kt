package io.playersapi.adapters.web.errors

data class ErrorResponse(
    val message: String,
    val status: Int,
    val code: String? = null
)