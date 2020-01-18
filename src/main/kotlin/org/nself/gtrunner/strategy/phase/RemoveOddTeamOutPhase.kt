package org.nself.gtrunner.strategy.phase

import org.nself.gtrunner.dataobject.Matchup
import org.nself.gtrunner.dataobject.Team
import org.nself.gtrunner.strategy.rule.MatchupFormationRule
import kotlin.random.Random

/**
 * If there are an odd number of teams in remainingTeamSet, removes one, picking from whichever teams are tied for
 * most matches played. If there are an even number of teams, does nothing. Does not create any matchups.
 */
class RemoveOddTeamOutPhase : MatchupFormationPhase {
    override fun applyPhase(roundId: Int, ruleSet: Set<MatchupFormationRule>, remainingTeamSet: MutableSet<Team>, matchupSet: MutableSet<Matchup>) {
        if (remainingTeamSet.size % 2 == 0) {
            return
        }
        // Find the maximum number of matches played
        var maxMatchCount = 0
        for (team in remainingTeamSet) {
            if (team.finishedMatchCount() > maxMatchCount) {
                maxMatchCount = team.finishedMatchCount()
            }
        }

        // Create a list of teams that share the max number of matches played
        val constrainedTeamList = mutableListOf<Team>()
        for (team in remainingTeamSet) {
            if (team.finishedMatchCount() == maxMatchCount) {
                constrainedTeamList.add(team)
            }
        }

        // Pick one from this list at random (not so random if one team has played more than any other)
        val loser = constrainedTeamList[Random.nextInt(constrainedTeamList.size)]

        // BA-LEETED
        remainingTeamSet.remove(loser)
    }
}