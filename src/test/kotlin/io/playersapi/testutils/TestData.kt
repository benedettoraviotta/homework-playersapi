package io.playersapi.testutils

import io.playersapi.application.dto.PlayerResponse
import io.playersapi.core.domain.Player
import io.playersapi.core.domain.PlayerStatus

object TestData {
    val players = listOf(
        Player(1, "Rafael Leao", "Winger", 1999, PlayerStatus.ACTIVE, "Milan"),
        Player(2, "Gianluigi Buffon", "Goalkeeper", 1978, PlayerStatus.RETIRED, "Retired"),
        Player(3, "Alessandro Bastoni", "Defender", 1999, PlayerStatus.ACTIVE, "Inter"),
        Player(4, "Sandi Lovric", "Midfielder", 1998, PlayerStatus.ACTIVE, "Udinese"),
        Player(5, "Filippo Inzaghi", "Forward", 1973, PlayerStatus.RETIRED, "Retired")
    )

     val playersResponse = listOf(
        PlayerResponse(1, "Rafael Leao", "Winger", 1999, PlayerStatus.ACTIVE, "Milan"),
        PlayerResponse(2, "Gianluigi Buffon", "Goalkeeper", 1978, PlayerStatus.RETIRED, "Retired"),
        PlayerResponse(3, "Alessandro Bastoni", "Defender", 1999, PlayerStatus.ACTIVE, "Inter"),
        PlayerResponse(4, "Sandi Lovric", "Midfielder", 1998, PlayerStatus.ACTIVE, "Udinese"),
        PlayerResponse(5, "Filippo Inzaghi", "Forward", 1973, PlayerStatus.RETIRED, "Retired")
    )
}