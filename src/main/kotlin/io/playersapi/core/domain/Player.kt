package io.playersapi.core.domain

data class Player(
    val id: Long,
    val name: String,
    val position: String,
    val birthYear: Int,
    val status: PlayerStatus,
    val club: String
)
