package org.nself.gtrunner.strategy.phase

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.nself.gtrunner.GTRunnerTest
import org.nself.gtrunner.MainUtils
import org.nself.gtrunner.dataobject.Matchup
import org.nself.gtrunner.dataobject.Team
import org.nself.gtrunner.strategy.rule.MatchupFormationRule

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class MatchSimilarlyRatedTeamsPhaseTest: GTRunnerTest() {

    private val matchSimilarPhase = MatchSimilarlyRatedTeamsPhase()
    private val rules = setOf<MatchupFormationRule>()
    private val matchupSet =  mutableSetOf<Matchup>()

    @Test
    fun testMatchingSimilarlyRatedTeams() {
        val teamSet = MainUtils.generateTeamsFromCsvFile("${testResourcesDir}example_teams_8.csv")
        val teamList = teamSet.toMutableList()
        teamList.sortBy { it.initialRating }
        val expectedMatchupListOfSets = generateExpectedMatchups(teamList)
        check(expectedMatchupListOfSets.size == 3) {
            "Should have exactly 3 rounds' worth of matchupSet."
        }

        for (i in 0 until 3) {
            matchupSet.clear()
            matchSimilarPhase.applyPhase(i, rules, teamSet.toMutableSet(), matchupSet)
            Assertions.assertEquals(expectedMatchupListOfSets[i].size, matchupSet.size, "Number of matchups generated for the round is wrong.")

            // Create results so that next round will group teams properly based on records
            for (matchup in matchupSet) {
                val team1Rating = matchup.team1.initialRating
                val team2Rating = matchup.team2.initialRating
                matchup.winnerTeamId = if (team1Rating > team2Rating) matchup.team1.teamId else matchup.team2.teamId
                matchup.team1.addResult(matchup)
                matchup.team2.addResult(matchup)
            }

            Assertions.assertTrue(matchupSet.minus(expectedMatchupListOfSets[i]).isEmpty(), "Generated matchup set did not match exactly with expected matchup set.")
        }
    }

    private fun generateExpectedMatchups(teamList: List<Team>): List<Set<Matchup>> {
        require(teamList.size == 8) {
            "This function is hard-coded to expect 8 teams."
        }

        val firstMatchupSet = mutableSetOf<Matchup>(
            Matchup(0, teamList[0], teamList[1]),
            Matchup(0, teamList[2], teamList[3]),
            Matchup(0, teamList[4], teamList[5]),
            Matchup(0, teamList[6], teamList[7])
        )

        val secondMatchupSet = mutableSetOf<Matchup>(
            Matchup(1, teamList[0], teamList[2]),
            Matchup(1, teamList[1], teamList[3]),
            Matchup(1, teamList[4], teamList[6]),
            Matchup(1, teamList[5], teamList[7])
        )

        val thirdMatchupSet = mutableSetOf<Matchup>(
            Matchup(2, teamList[0], teamList[4]),
            Matchup(2, teamList[1], teamList[2]),
            Matchup(2, teamList[5], teamList[6]),
            Matchup(2, teamList[3], teamList[7])
        )

        return listOf(firstMatchupSet, secondMatchupSet, thirdMatchupSet)
    }
}