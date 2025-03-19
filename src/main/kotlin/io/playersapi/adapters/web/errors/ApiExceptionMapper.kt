package io.playersapi.adapters.web.errors

import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import jakarta.ws.rs.core.Response
import org.slf4j.LoggerFactory

@Provider
class ApiExceptionMapper : ExceptionMapper<Throwable> {

    private val logger = LoggerFactory.getLogger(ApiExceptionMapper::class.java)

    override fun toResponse(exception: Throwable): Response {
        val errorResponse = when (exception) {
            is PlayerException -> {
                ErrorResponse(
                    message = exception.message ?: "An error occurred",
                    status = exception.status.statusCode,
                    code = exception.code
                )
            }
            else -> {
                logger.error("Unexpected exception: ${exception.message}", exception)
                ErrorResponse(
                    message = "An unexpected error occurred.",
                    status = Response.Status.INTERNAL_SERVER_ERROR.statusCode,
                    code = ErrorCodes.INTERNAL_ERROR
                )
            }
        }

        return Response.status(errorResponse.status)
            .entity(errorResponse)
            .build()
    }
}