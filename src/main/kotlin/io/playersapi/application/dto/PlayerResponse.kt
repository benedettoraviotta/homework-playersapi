package io.playersapi.application.dto

import io.playersapi.core.domain.PlayerStatus

data class PlayerResponse(
    val id: Long,
    val name: String,
    val position: String,
    val birthYear: Int,
    val status: PlayerStatus,
    val club: String
)
