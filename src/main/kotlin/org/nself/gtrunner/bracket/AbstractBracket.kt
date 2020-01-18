package org.nself.gtrunner.bracket

import org.nself.gtrunner.dataobject.Round
import org.nself.gtrunner.dataobject.Team
import org.nself.gtrunner.strategy.SeedingStrategy

abstract class AbstractBracket(
    protected val name: String,
    protected val teamSet: Set<Team>,
    protected val seedingStrategy: SeedingStrategy
) : Bracket {

    protected var currentRoundId: Int = -1
    protected val roundList = mutableListOf<Round>()
    // How the hell do I name this 'teamMap' without conflicting with the interface method?
    protected val map = mutableMapOf<Int, Team>()

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

    override fun getBracketName(): String {
        return name
    }

    override fun getTeamMap(): Map<Int, Team> {
        return map
    }

    override fun naturalNumberOfRounds(): Int {
        throw UnsupportedOperationException("This method is not supported on this implementation of Bracket.")
    }

    override fun makeNextRound(): Int {
        currentRoundId++
        val round = seedingStrategy.generateNewRound(currentRoundId, teamSet)
        for (matchup in round.matchupSet) {
            matchup.team1.addScheduledMatch(matchup)
            matchup.team2.addScheduledMatch(matchup)
        }
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

    /**
     * Returns a string of the current records, starting from the team with the best record, and on down.
     */
    override fun currentRecordsString(): String {
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

    override fun allMatchupsSoFarString() : String {
        val str = StringBuilder()
        // TODO: there's gotta be a better way to do this newline thing.
        val newline = System.getProperty("line.separator")

        str.append("<><><><><> Showing All Matchups <><><><><>")
        str.append(newline + newline)

        for (i in 0 until roundList.size) {
            val round = roundList[i]
            str.append(round.prettyString())
            str.append(newline)
        }

        return str.toString()
    }

    protected fun teamSelector(t: Team) = t.record().first - t.record().second
}