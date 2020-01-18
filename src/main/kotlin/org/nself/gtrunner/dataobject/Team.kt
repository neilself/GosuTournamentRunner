package org.nself.gtrunner.dataobject

class Team(val teamId: Int, val name: String = "DefaultName", val initialRating: Float = 0f) {
    val matchSchedule: MutableList<Matchup> = mutableListOf()
    val matchHistory: MutableList<Matchup> = mutableListOf()

    fun addResult(result: Matchup) {
        // Remove completed matchup from match schedule
        for (matchup in matchSchedule) {
            if (matchup.roundId == result.roundId
                && matchup.team1.teamId == result.team1.teamId
                && matchup.team2.teamId == result.team2.teamId) {
                matchSchedule.remove(matchup)
            }
        }
        matchHistory.add(result)
    }

    fun addScheduledMatch(matchup: Matchup) {
        matchSchedule.add(matchup)
    }

    fun winCount(): Int {
        var count = 0
        for (result in matchHistory) {
            if (result.winnerTeamId == teamId) {
                count++
            }
        }
        return count
    }

    fun lossCount(): Int {
        var count = 0
        for (result in matchHistory) {
            if (result.winnerTeamId != teamId) {
                count++
            }
        }
        return count
    }

    fun finishedMatchCount(): Int {
        return matchHistory.size
    }

    fun record(): Pair<Int, Int> {
        return Pair(winCount(), lossCount())
    }

    override fun toString(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Team) {
            return false
        }
        return teamId == other.teamId
                && name == other.name
                && initialRating == other.initialRating
    }

    override fun hashCode(): Int {
        return teamId.hashCode() + name.hashCode() + initialRating.hashCode()
    }
}