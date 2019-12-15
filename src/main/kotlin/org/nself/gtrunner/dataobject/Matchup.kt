package org.nself.gtrunner.dataobject
class Matchup (val roundId: Int, val team1: Team, val team2: Team) {
    var winnerTeamId: Int = -1

    override fun toString(): String {
        return "$roundId: $team1 vs $team2, winner: $winnerTeamId"
    }

    // TODO: Shit, should I replace the teamIds in the constructor of this class with the teams themselves?
    fun prettyString(): String {
        return "$roundId: " +
                "${if (team1.teamId == winnerTeamId) ">> " else ""}${team1}${if (team1.teamId == winnerTeamId) " <<" else ""} vs " +
                "${if (team2.teamId == winnerTeamId) ">> " else ""}${team2}${if (team2.teamId == winnerTeamId) " <<" else ""}"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Matchup) {
            return false
        }
        return roundId == other.roundId
                && team1 == other.team1
                && team2 == other.team2
    }

    override fun hashCode(): Int {
        return roundId.hashCode() + team1.hashCode() + team2.hashCode()
    }
}