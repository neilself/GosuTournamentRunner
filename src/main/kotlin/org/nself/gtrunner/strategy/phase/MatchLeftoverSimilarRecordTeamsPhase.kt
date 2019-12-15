package org.nself.gtrunner.strategy.phase

import org.nself.gtrunner.dataobject.Matchup
import org.nself.gtrunner.dataobject.Team
import org.nself.gtrunner.strategy.rule.MatchupFormationRule

/**
 * Intended to match teams that do not have the same record, that may be leftover from previous phases that were
 * unable to match all present teams. Teams will be matched after being ordered by their records, with teams with
 * similar records being matched together. Does not deal with odd numbers of teams, so one may be leftover at the
 * end if starting with an odd number.
 */
class MatchLeftoverSimilarRecordTeamsPhase : MatchupFormationPhase {
    override fun applyPhase(roundId: Int, ruleSet: Set<MatchupFormationRule>, remainingTeamSet: MutableSet<Team>, matchupSet: MutableSet<Matchup>) {
        val orderedTeamList = remainingTeamSet.toMutableList()
        orderedTeamList.sortBy{recordSelector(it.record())}

        PhaseUtils.findMatchupsWithinGrouping(roundId, ruleSet, orderedTeamList, matchupSet, PhaseUtils.MatchingSearchPattern.BEGINNING_TO_END)

        for (matchup in matchupSet) {
            remainingTeamSet.remove(matchup.team1)
            remainingTeamSet.remove(matchup.team2)
        }
    }

    /**
     * Used for comparing Pairs that represent win-loss records. First = win count, second = loss count.
     */
    private fun recordSelector(p: Pair<Int, Int>) = p.first - p.second
}