package org.nself.gtrunner.strategy.phase

import org.nself.gtrunner.dataobject.Matchup
import org.nself.gtrunner.dataobject.Team
import org.nself.gtrunner.strategy.rule.MatchupFormationRule

object PhaseUtils {

    /**
     * A DFS algorithm that attempts to find valid matchups within the given grouping recursively. At each level, it
     * tries out pairs until it finds a valid matchup, then recurses down with the matched teams having been removed
     * from the team list and added as a match to the match set. Base case is when there are 0 or 1 teams, since no
     * pairs are possible with a single team.
     *
     * Depending on the rules, it's possible to fail to find many matchups (e.g. if the teams have all played each other
     * already and one of the rules is 'no rematches', then no matchups will be created).
     */
    @JvmStatic
    fun findMatchupsWithinGrouping(
        roundId: Int,
        ruleSet: Set<MatchupFormationRule>,
        orderedTeamList: MutableList<Team>,
        matchupsSoFar: MutableSet<Matchup>,
        searchPattern: MatchingSearchPattern
    ) {
        if (orderedTeamList.size <= 1) {
            return
        }

        val indexObject = getInitialIndicesForFindingMatchups(orderedTeamList.size, searchPattern)
        while (indexObject.i != indexObject.j) {
            val matchup = Matchup(roundId, orderedTeamList[indexObject.i], orderedTeamList[indexObject.j])
            var isValid = true

            for (rule in ruleSet) {
                if (!rule.isMatchupValid(matchup)) {
                    isValid = false
                    if (roundId == 3) {
                        if (isValid) {
                            println("Matchup valid: " + matchup)
                        } else {
                            println("Matchup NOT valid: " + matchup)
                        }
                    }
                }
            }

            if (!isValid) {
                getNextIndicesForFindingMatchups(orderedTeamList.size, indexObject, searchPattern)
                continue
            }

            val newTeamList = orderedTeamList.toMutableList()
            newTeamList.remove(matchup.team1)
            newTeamList.remove(matchup.team2)
            val newMatchupSet = matchupsSoFar.toMutableSet()
            newMatchupSet.add(matchup)

            findMatchupsWithinGrouping(roundId, ruleSet, newTeamList, newMatchupSet, searchPattern)

            // If there are 1 or 0 teams remaining in newTeamList, we were able to successfully construct enough
            // matchups, so return
            if (newTeamList.size <= 1) {
                matchupsSoFar.clear()
                matchupsSoFar.addAll(newMatchupSet)
                orderedTeamList.clear()
                orderedTeamList.addAll(newTeamList)
                return
            }

            // Otherwise, increment/decrement one of the indices, and then either the loop will run again with another
            // possible matchup at this stage of recursion, OR we'll exit the while loop and return without enough
            // matchups created, a failure. :(
            getNextIndicesForFindingMatchups(orderedTeamList.size, indexObject, searchPattern)
        }

        return
    }

    /**
     * Creates groupings of teams within a map based on records. Records are pairs of ints, with the first representing
     * the number of wins, and the second representing the number of losses. These are used as keys in the map.
     */
    @JvmStatic
    fun createTeamGroupMapByRecord(teamSet: Set<Team>): Map<Pair<Int, Int>, MutableSet<Team>> {
        val teamGroupMap = mutableMapOf<Pair<Int, Int>, MutableSet<Team>>()
        for (team in teamSet) {
            if (teamGroupMap.keys.contains(team.record())) {
                teamGroupMap[team.record()]?.add(team)
            } else {
                teamGroupMap[team.record()] = mutableSetOf(team)
            }
        }
        return teamGroupMap
    }

    /**
     * Sorts the list of records, based on (# wins - # losses)
     */
    fun sortRecordList(recordList: MutableList<Pair<Int, Int>>) {
        recordList.sortBy(this::recordSelector)
    }

    /**
     * Used for comparing Pairs that represent win-loss records. First = win count, second = loss count.
     */
    private fun recordSelector(p: Pair<Int, Int>) = p.first - p.second

    private fun getInitialIndicesForFindingMatchups(size: Int, searchPattern: MatchingSearchPattern): IndexObject {
        return when (searchPattern) {
            MatchingSearchPattern.BEGINNING_TO_END -> {
                IndexObject(0, 1, LastChangedIndex.LAST_CHANGED_I)
            }
            MatchingSearchPattern.OUTSIDE_IN -> {
                IndexObject(0, size - 1, LastChangedIndex.LAST_CHANGED_J)
            }
            MatchingSearchPattern.FULL_SEARCH -> {
                IndexObject(0, 1, LastChangedIndex.LAST_CHANGED_I)
            }
        }
    }

    private fun getNextIndicesForFindingMatchups(
        size: Int,
        indexObject: IndexObject,
        searchPattern: MatchingSearchPattern
    ) {
        when (searchPattern) {
            MatchingSearchPattern.BEGINNING_TO_END -> {
                if (indexObject.j == size - 1) {
                    indexObject.i++
                } else {
                    indexObject.j++
                }
            }
            MatchingSearchPattern.OUTSIDE_IN -> {
                if (indexObject.lastChanged == LastChangedIndex.LAST_CHANGED_I) {
                    indexObject.j--
                    indexObject.lastChanged = LastChangedIndex.LAST_CHANGED_J
                } else {
                    indexObject.i++
                    indexObject.lastChanged = LastChangedIndex.LAST_CHANGED_I
                }
            }
            MatchingSearchPattern.FULL_SEARCH -> {
                if (indexObject.j == size - 1) {
                    // Last index for j, start new outside loop
                    indexObject.i++
                    indexObject.j = indexObject.i + 1
                    if (indexObject.j >= size) {
                        // We're done
                        indexObject.j = indexObject.i
                    }
                } else {
                    indexObject.j++
                }
            }
        }
    }

    private class IndexObject(var i: Int, var j: Int, var lastChanged: LastChangedIndex)

    enum class LastChangedIndex {
        LAST_CHANGED_I,
        LAST_CHANGED_J
    }

    enum class MatchingSearchPattern {
        BEGINNING_TO_END,
        OUTSIDE_IN,
        FULL_SEARCH // N^2 double loop bubble/insertion/selection sort style of search, where you start at i = 0, j = 1,
                    // increment j in the inner loop, and i in the outer loop.
    }
}