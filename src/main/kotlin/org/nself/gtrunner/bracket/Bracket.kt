package org.nself.gtrunner.bracket

import org.nself.gtrunner.dataobject.Round
import org.nself.gtrunner.dataobject.Team

interface Bracket {

    fun getTeamMap(): Map<Int, Team>
    fun getBracketName(): String
    fun getLatestRoundId(): Int
    fun getRound(roundId: Int): Round
    /**
     * Indicates whether the match-ups for each round depend on the results
     * of the prior round(s).
     */
    fun hasDependentRounds(): Boolean
    fun naturalNumberOfRounds(): Int
    fun makeNextRound(): Int
    fun simulateRound()
    fun currentRecordsString(): String
    fun allMatchupsSoFarString(): String
}