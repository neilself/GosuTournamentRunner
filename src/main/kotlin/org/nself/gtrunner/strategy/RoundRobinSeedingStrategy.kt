package org.nself.gtrunner.strategy

import org.nself.gtrunner.dataobject.Matchup
import org.nself.gtrunner.dataobject.Round
import org.nself.gtrunner.dataobject.Team
import org.nself.gtrunner.strategy.phase.RemoveOddTeamOutPhase
import org.nself.gtrunner.strategy.phase.RoundRobinPhase
import org.nself.gtrunner.strategy.rule.MatchupFormationRule
import org.nself.gtrunner.strategy.rule.NoRematchesRule

/**
 * This very simple strategy simply matches teams with other teams that they haven't played yet, starting with the
 * largest differences and working down to the smallest ones. If this strategy is used to generate a round where no
 * further matches are possible due to teams having already played each other, an exception is thrown.
 */
class RoundRobinSeedingStrategy : SeedingStrategy {
    override fun generateNewRound(roundId: Int, originalTeamSet: Set<Team>): Round {
        val mutableTeamSet = originalTeamSet.toMutableSet()
        val matchupSet = mutableSetOf<Matchup>()
        val noRematchesRuleSet = mutableSetOf<MatchupFormationRule>(NoRematchesRule())

        // Remove one team if there's an odd number
        RemoveOddTeamOutPhase().applyPhase(roundId, noRematchesRuleSet, mutableTeamSet, matchupSet)

        RoundRobinPhase()
            .applyPhase(roundId, noRematchesRuleSet, mutableTeamSet, matchupSet)

        if (matchupSet.isEmpty()) {
            throw IllegalStateException("Was unable to form any matchups in round $roundId, due to teams having " +
                    "played each other already. This should not occur in a Round Robin Group.")
        }

        return Round(roundId, matchupSet)
    }
}