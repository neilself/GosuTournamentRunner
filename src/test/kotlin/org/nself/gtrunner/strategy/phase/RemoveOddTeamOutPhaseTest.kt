package org.nself.gtrunner.strategy.phase

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.nself.gtrunner.dataobject.Matchup
import org.nself.gtrunner.dataobject.Team
import org.nself.gtrunner.strategy.rule.MatchupFormationRule

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class RemoveOddTeamOutPhaseTest {

    private val oddTeamSet = mutableSetOf<Team>(
        Team(0, "Alpha Sun", 40f),
        Team(1, "Beta Earth", 30f),
        Team(2, "Gamma Sea", 20f),
        Team(3, "Delta Moon", 10f),
        Team(4, "Epsilon Star", 5f)
    )

    private val evenTeamSet = mutableSetOf<Team>(
        Team(0, "Alpha Sun", 40f),
        Team(1, "Beta Earth", 30f),
        Team(2, "Gamma Sea", 20f),
        Team(3, "Delta Moon", 10f)
    )

    private val removeOddPhase = RemoveOddTeamOutPhase()
    private val rules = setOf<MatchupFormationRule>()
    private val matchups =  mutableSetOf<Matchup>()

    @Test
    fun testOddNumberOfTeams() {
        val teamSet = oddTeamSet.toMutableSet()

        removeOddPhase.applyPhase(0, rules, teamSet, matchups)

        val firstMissingTeam = findMissingTeam(oddTeamSet, teamSet)
        var atLeastOneDifferentMissingTeam = false

        for (i in 1..100) {
            val loopTeamSet = oddTeamSet.toMutableSet()
            removeOddPhase.applyPhase(i, rules, loopTeamSet, matchups)
            val loopMissingTeam = findMissingTeam(oddTeamSet, loopTeamSet)
            if (!loopMissingTeam.equals(firstMissingTeam)) {
                atLeastOneDifferentMissingTeam = true
                break
            }
        }

        Assertions.assertTrue(
            atLeastOneDifferentMissingTeam,
            "Every missing team found using the RemoveOddTeamOutPhaseTest was the same."
        )
    }

    @Test
    fun testEvenNumberOfTeams() {
        for (i in 0 until 100) {
            val loopTeamSet = evenTeamSet.toMutableSet()
            removeOddPhase.applyPhase(i, rules, loopTeamSet, matchups)
            if (!loopTeamSet.equals(evenTeamSet)) {
                Assertions.fail<String>("RemoveOddTeamOutPhase removed a team when the team set has an even number of teams.")
            }
        }
    }

    private fun findMissingTeam(originalTeamSet: Set<Team>, reducedTeamSet: Set<Team>): Team {
        require(originalTeamSet.size == reducedTeamSet.size + 1) {
            "Size of original team set must be exactly one more than size of reduced team set."
        }

        val newSet = originalTeamSet.minus(reducedTeamSet)
        require(newSet.size == 1) {
            "There must be only one team present in the original team set once the reduced team set has been subtracted from it."
        }

        return newSet.iterator().next()
    }
}