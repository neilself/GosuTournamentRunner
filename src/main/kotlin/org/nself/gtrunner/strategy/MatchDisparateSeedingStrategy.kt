package org.nself.gtrunner.strategy

import org.nself.gtrunner.dataobject.Matchup
import org.nself.gtrunner.dataobject.Round
import org.nself.gtrunner.dataobject.Team
import org.nself.gtrunner.strategy.phase.MatchDisparatelyRatedTeamsPhase
import org.nself.gtrunner.strategy.phase.MatchLeftoverSimilarRecordTeamsPhase
import org.nself.gtrunner.strategy.phase.RemoveOddTeamOutPhase
import org.nself.gtrunner.strategy.rule.MatchupFormationRule
import org.nself.gtrunner.strategy.rule.NoRematchesRule

/**
 * A seeding strategy that matches teams starting with the largest differences within a given group with the same
 * matchHistory. This is the 'classical' style of seeding commonly seen in elimination tournaments, with the strongest
 * player matched versus the weakest, the second-strongest versus the second-weakest, etc.
 */
class MatchDisparateSeedingStrategy : SeedingStrategy {

    override fun generateNewRound(roundId: Int, originalTeamSet: Set<Team>): Round {
        val mutableTeamSet = originalTeamSet.toMutableSet()
        val matchupSet = mutableSetOf<Matchup>()
        val emptyRuleSet = mutableSetOf<MatchupFormationRule>()
        val noRematchesRuleSet = mutableSetOf<MatchupFormationRule>(NoRematchesRule())

        // Remove one team if there's an odd number
        RemoveOddTeamOutPhase().applyPhase(roundId, noRematchesRuleSet, mutableTeamSet, matchupSet)

        // Create matchups trying to avoid rematches
        MatchDisparatelyRatedTeamsPhase()
            .applyPhase(roundId, noRematchesRuleSet, mutableTeamSet, matchupSet)
        MatchLeftoverSimilarRecordTeamsPhase()
            .applyPhase(roundId, noRematchesRuleSet, mutableTeamSet, matchupSet)

        // Create matchups for remaining teams without trying to avoid rematches
        MatchDisparatelyRatedTeamsPhase()
            .applyPhase(roundId, emptyRuleSet, mutableTeamSet, matchupSet)
        MatchLeftoverSimilarRecordTeamsPhase()
            .applyPhase(roundId, emptyRuleSet, mutableTeamSet, matchupSet)

        return Round(roundId, matchupSet)
    }
}