package io.playersapi.application.dto

import io.playersapi.core.domain.PlayerStatus


data class PlayerFilter(
    val position: String? = null,
    val minBirthYear: Int? = null,
    val maxBirthYear: Int? = null,
    val status: String? = null,
    val club: String? = null,
) {
    val statusEnum: PlayerStatus? = status?.let {
        runCatching { PlayerStatus.valueOf(it.uppercase()) }.getOrNull()
    }
}
