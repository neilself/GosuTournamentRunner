package org.nself.gtrunner.strategy.rule

import org.nself.gtrunner.dataobject.Matchup

/**
 * Considers a matchup invalid if the two teams in question have played each other previously.
 */
class NoRematchesRule : MatchupFormationRule {
    override fun isMatchupValid(matchup: Matchup): Boolean {
        for (priorMatchup in matchup.team1.matchHistory) {
            if (priorMatchup.team1.teamId == matchup.team2.teamId
                || priorMatchup.team2.teamId == matchup.team2.teamId) {
                return false
            }
        }
        return true
    }
}