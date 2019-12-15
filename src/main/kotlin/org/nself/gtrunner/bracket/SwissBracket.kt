package org.nself.gtrunner.bracket

import org.nself.gtrunner.dataobject.Round
import org.nself.gtrunner.dataobject.Team
import org.nself.gtrunner.strategy.SeedingStrategy

class SwissBracket(private val bracketName: String, private val teamSet: Set<Team>, private val seedingStrategy: SeedingStrategy):
    Bracket {

    private var currentRoundId: Int = -1
    private val roundList = mutableListOf<Round>()
    // How the hell do I name this 'teamMap' without conflicting with the interface method?
    private val map = mutableMapOf<Int, Team>()

    init {
        for (team in teamSet) {
            map[team.teamId] = team
        }
        // TODO: check for duplicate team ids
    }

    override fun getLatestRoundId(): Int {
        return currentRoundId
    }

    override fun getRound(roundId: Int): Round {
        return roundList[roundId]
    }

    override fun makeNextRound(): Int {
        currentRoundId++
        val round = seedingStrategy.generateNewRound(currentRoundId, teamSet)
        roundList.add(round)
        return currentRoundId
    }

    override fun simulateRound() {
        val round = roundList[currentRoundId]
        for (matchup in round.matchupSet) {
            val team1Rating = matchup.team1.initialRating
            val team2Rating = matchup.team2.initialRating
            matchup.winnerTeamId = if (team1Rating > team2Rating) matchup.team1.teamId else matchup.team2.teamId
            matchup.team1.addResult(matchup)
            matchup.team2.addResult(matchup)
        }
    }
    override fun getBracketName(): String {
        return bracketName
    }
    override fun getTeamMap(): Map<Int, Team> {
        return map
    }

    /**
     * Returns a string of the current records, starting from the team with the best record, and on down.
     */
    fun currentRecordsString(): String {
        val str = StringBuilder()
        val newline = System.getProperty("line.separator")

        val teamList = teamSet.toMutableList()
        teamList.sortByDescending { teamSelector(it) }

        str.append("===== Round $currentRoundId =====")
        str.append(newline)

        for (team in teamList) {
            str.append("(${team.initialRating}) $team: ${team.record().first} - ${team.record().second}")
            str.append(newline)
        }

        return str.toString()
    }

    private fun teamSelector(t: Team) = t.record().first - t.record().second
}