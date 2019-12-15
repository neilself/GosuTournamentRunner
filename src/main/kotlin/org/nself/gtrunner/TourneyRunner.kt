package org.nself.gtrunner

import org.nself.gtrunner.bracket.SwissBracket
import org.nself.gtrunner.strategy.MatchSimilarSeedingStrategy

fun main(args: Array<String>) {
    val teamSet = MainUtils.generateTeamsFromCsvFile("example_teams_8.csv")

    val bracket =
        SwissBracket(
            "Ulti-org.nself.gtrunner.bracket.Bracket",
            teamSet,
            MatchSimilarSeedingStrategy()
        )
    val numberOfRounds = 4

    println(bracket.currentRecordsString())
    for (i in 0 until numberOfRounds) {
        bracket.makeNextRound()
        println(bracket.getRound(bracket.getLatestRoundId()).prettyString())
        bracket.simulateRound()
        println("FIGHT!")
        println()
        println(bracket.getRound(bracket.getLatestRoundId()).prettyString())
        println(bracket.currentRecordsString())
    }
}

