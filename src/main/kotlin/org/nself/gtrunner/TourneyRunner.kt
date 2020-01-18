package org.nself.gtrunner

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.InvalidArgumentException
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
import org.nself.gtrunner.bracket.Bracket
import org.nself.gtrunner.bracket.RoundRobinBracket
import org.nself.gtrunner.bracket.SwissBracket
import org.nself.gtrunner.dataobject.Team
import org.nself.gtrunner.strategy.MatchDisparateSeedingStrategy
import org.nself.gtrunner.strategy.MatchSimilarSeedingStrategy
import org.nself.gtrunner.strategy.SeedingStrategy

class RunnerArgs(parser: ArgParser) {

    val strategyMap = mapOf<String, SeedingStrategy>(
        "match_disparate" to MatchDisparateSeedingStrategy(),
        "match_similar" to MatchSimilarSeedingStrategy()
    )

    val bracketList = listOf<String>(
        "swiss",
        "round_robin"
    )

    val sourceArg by parser.storing("--source", help = "The source file for team and player data").default("example_teams_8.csv")
    val destArg by parser.storing("--dest", help = "The destination file for tournament output").default("gtrunner_output.csv")
    val strategyArg by parser.storing("--strategy", help = "The desired seeding strategy to be used").default("match_disparate")
    val bracketArg by parser.storing("--bracket", help = "The desired bracket type to be used.").default("swiss_bracket")

    fun getStrategy(strategyStr: String) : SeedingStrategy {
        return strategyMap[strategyStr] ?: throw InvalidArgumentException("Strategy must be one of: " + strategyMap.keys)
    }

    fun getBracket(bracketStr: String, teamSet: Set<Team>, strategy: SeedingStrategy) : Bracket {
        return when (bracketStr) {
            "swiss" -> SwissBracket("SwissBracket", teamSet, strategy)
            "round_robin" -> {
                println("Note that round robin brackets always have the same strategy: match_disparate")
                RoundRobinBracket("RoundRobinBracket", teamSet)
            }
            else -> throw InvalidArgumentException("Bracket must be one of: $bracketList")
        }
    }
}

fun main(args: Array<String>) = mainBody {
    ArgParser(args).parseInto(::RunnerArgs).run {
        val teamSet = MainUtils.generateTeamsFromCsvFile(sourceArg)
        val bracket = getBracket(bracketArg, teamSet, getStrategy(strategyArg))

        // TODO: Need to make this a command-line option or something. Also need to make it so round robin brackets set
        // their own number of rounds?
        val numberOfRounds = 7

        println(bracket.currentRecordsString())

        // TODO: The behavior here needs to be under some kind of control, either command line argument or interactive,
        //       or both. For brackets both with and without dependent rounds, you should be able to choose between
        //       merely generating (initial/all) rounds, or simulating the whole tournament.
        if (bracket.hasDependentRounds()) {
            for (i in 0 until numberOfRounds) {
                bracket.makeNextRound()
                println(bracket.getRound(bracket.getLatestRoundId()).prettyString())
                bracket.simulateRound()
                println("FIGHT!")
                println()
                println(bracket.getRound(bracket.getLatestRoundId()).prettyString())
                println(bracket.currentRecordsString())

                if (i == numberOfRounds - 1) {
                    MainUtils.outputResultsToCsvFile(bracket, destArg)
                }
            }
        } else {
            println("Name of bracket: " + bracket.getBracketName())
            println()
            for (i in 0 until bracket.naturalNumberOfRounds()) {
                println(bracket.allMatchupsSoFarString())
                bracket.makeNextRound()
            }
            println(bracket.allMatchupsSoFarString())
        }
    }
}

