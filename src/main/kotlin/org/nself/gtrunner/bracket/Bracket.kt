package org.nself.gtrunner.bracket

import org.nself.gtrunner.dataobject.Round
import org.nself.gtrunner.dataobject.Team

interface Bracket {

    fun getTeamMap(): Map<Int, Team>
    fun getBracketName(): String
    fun getLatestRoundId(): Int
    fun getRound(roundId: Int): Round
    fun makeNextRound(): Int
    fun simulateRound()

}