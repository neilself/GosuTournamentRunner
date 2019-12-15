package org.nself.gtrunner.strategy.phase

import org.nself.gtrunner.dataobject.Matchup
import org.nself.gtrunner.dataobject.Team
import org.nself.gtrunner.strategy.rule.MatchupFormationRule

interface MatchupFormationPhase {
    /**
     * Function for applying the phase to generate more matchups. As teams are used in matchups, they should be removed
     * from the remainingTeamSet.
     */
    fun applyPhase(roundId: Int, ruleSet: Set<MatchupFormationRule>, remainingTeamSet: MutableSet<Team>, matchupSet: MutableSet<Matchup>)
}