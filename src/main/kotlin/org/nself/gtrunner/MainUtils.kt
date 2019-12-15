package org.nself.gtrunner

import org.nself.gtrunner.dataobject.Team
import java.io.File

object MainUtils {
    const val CSV_COLUMN_COUNT = 3

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
}