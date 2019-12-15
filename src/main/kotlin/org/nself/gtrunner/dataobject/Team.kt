package org.nself.gtrunner.dataobject

class Team(val teamId: Int, val name: String = "DefaultName", val initialRating: Float = 0f) {
    var matchHistory: MutableList<Matchup> = mutableListOf()

    fun addResult(result: Matchup) {
        matchHistory.add(result)
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

    fun matchCount(): Int {
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