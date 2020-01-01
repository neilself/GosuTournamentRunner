package org.nself.gtrunner

import org.nself.gtrunner.bracket.SwissBracket
import org.nself.gtrunner.dataobject.Team
import org.nself.gtrunner.strategy.phase.PhaseUtils
import java.io.File

object MainUtils {
    const val CSV_COLUMN_COUNT = 3
    val outputColumns = arrayOf("Team", "Wins", "Losses")
    val separator = System.getProperty("line.separator")

    fun generateTeamsFromCsvFile(filename: String): Set<Team> {
        val teamSet = mutableSetOf<Team>()
        val stringList = File(filename).readLines()

        for (i in 0 until stringList.size) {
            if (i != 0) {
                val arr = stringList[i].split(",").toTypedArray()
                check(arr.size == CSV_COLUMN_COUNT)
                teamSet.add(Team(arr[0].toInt(), arr[1], arr[2].toFloat()))
            }
        }

        return teamSet
    }

    fun outputResultsToCsvFile(bracket: SwissBracket, filename: String) {
        File(filename).printWriter().use { out ->
            for (col in outputColumns) {
                out.print("$col, ")
            }
            out.print(separator)

            val teamMap = bracket.getTeamMap()
            val teamGroupMap = PhaseUtils.createTeamGroupMapByRecord(bracket.getTeamMap().values.toSet())
            val recordList = teamGroupMap.keys.toMutableList()
            PhaseUtils.sortRecordList(recordList)
            for (record in recordList) {
                for (team in teamMap.values) {
                    if (team.record().equals(record)) {
                        out.print("${team.name}, ${team.record().first}, ${team.record().second},$separator")
                    }
                }
            }
        }
    }
}