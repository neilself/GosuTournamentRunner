package org.nself.gtrunner

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.InvalidArgumentException
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
import org.nself.gtrunner.bracket.SwissBracket
import org.nself.gtrunner.strategy.MatchDisparateSeedingStrategy
import org.nself.gtrunner.strategy.MatchSimilarSeedingStrategy
import org.nself.gtrunner.strategy.SeedingStrategy

class RunnerArgs(parser: ArgParser) {

    val strategyMap = mapOf<String, SeedingStrategy>(
        "match_disparate" to MatchDisparateSeedingStrategy(),
        "match_similar" to MatchSimilarSeedingStrategy()
    )

    val source by parser.storing("--source", help = "The source file for team and player data").default("example_teams_8.csv")
    val strategy by parser.storing("--strategy", help = "The desired seeding strategy to be used").default("match_disparate")

    fun getStrategy(strategyStr: String) : SeedingStrategy {
        return strategyMap[strategyStr] ?: throw InvalidArgumentException("Strategy must be one of: " + strategyMap.keys)
    }
}

fun main(args: Array<String>) = mainBody {
    ArgParser(args).parseInto(::RunnerArgs).run {
        val teamSet = MainUtils.generateTeamsFromCsvFile(source)
        val bracket =
            SwissBracket(
                "Ulti-org.nself.gtrunner.bracket.Bracket",
                teamSet,
                getStrategy(strategy)
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
}

