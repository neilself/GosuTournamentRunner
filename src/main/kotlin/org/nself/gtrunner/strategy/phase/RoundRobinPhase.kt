package org.nself.gtrunner.strategy.phase

import org.nself.gtrunner.dataobject.Matchup
import org.nself.gtrunner.dataobject.Team
import org.nself.gtrunner.strategy.rule.MatchupFormationRule

class RoundRobinPhase : MatchupFormationPhase {
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

        val orderedTeamList = remainingTeamSet.toMutableList()
        orderedTeamList.sortBy {it.initialRating}

        val leftoverTeams = mutableListOf<Team>()

        // Create matches within record groups as much as possible
        val matchupsFound = mutableSetOf<Matchup>()
        PhaseUtils.findMatchupsWithinGrouping(
            roundId,
            ruleSet,
            orderedTeamList,
            matchupsFound,
            PhaseUtils.MatchingSearchPattern.FULL_SEARCH
        )

        // Remove the teams that got matched from their group map, keep track of leftover teams when found
        matchupSet.addAll(matchupsFound)
        leftoverTeams.addAll(orderedTeamList)

        // Reduce remainingTeamSet to just the leftover teams
        remainingTeamSet.clear()
        if (leftoverTeams.isNotEmpty()) {
            remainingTeamSet.addAll(leftoverTeams)
        }
    }

}