package org.nself.gtrunner.strategy.phase

import org.nself.gtrunner.dataobject.Matchup
import org.nself.gtrunner.dataobject.Team
import org.nself.gtrunner.strategy.rule.MatchupFormationRule

/**
 * Matches teams within the same record groupings, matching the strongest team with the weakest team in that
 * grouping, then second strongest with second weakest, etc. Record groupings with an odd number of members will
 * leave a single member behind in remainingTeamSet.
 */
class MatchDisparatelyRatedTeamsPhase : MatchupFormationPhase {
    override fun applyPhase(
        roundId: Int,
        ruleSet: Set<MatchupFormationRule>,
        remainingTeamSet: MutableSet<Team>,
        matchupSet: MutableSet<Matchup>
    ) {
        // Group teams by record
        val teamGroupMap = PhaseUtils.createTeamGroupMapByRecord(remainingTeamSet)

        // Create ordered list of records
        val recordList = teamGroupMap.keys.toMutableList()
        PhaseUtils.sortRecordList(recordList)

        // Create matches within record groups as much as possible
        val leftoverTeams = mutableListOf<Team>()
        for (record in recordList) {
            val orderedTeamList = teamGroupMap[record]?.toMutableList() ?: continue
            orderedTeamList.sortBy { it.initialRating }

            val matchupsFound = mutableSetOf<Matchup>()
            PhaseUtils.findMatchupsWithinGrouping(
                roundId,
                ruleSet,
                orderedTeamList,
                matchupsFound,
                PhaseUtils.MatchingSearchPattern.OUTSIDE_IN
            )

            // Remove the teams that got matched from their group map, keep track of leftover teams when found
            matchupSet.addAll(matchupsFound)
            for (matchup in matchupsFound) {
                teamGroupMap[record]?.remove(matchup.team1)
                teamGroupMap[record]?.remove(matchup.team2)
            }
            leftoverTeams.addAll(orderedTeamList)
        }

        // Reduce remainingTeamSet to just the leftover teams
        remainingTeamSet.clear()
        if (leftoverTeams.isNotEmpty()) {
            remainingTeamSet.addAll(leftoverTeams)
        }
    }
}