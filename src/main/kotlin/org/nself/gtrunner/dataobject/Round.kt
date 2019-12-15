package org.nself.gtrunner.dataobject

class Round(val roundId: Int, val matchupSet: Set<Matchup>) {
    override fun toString(): String {
        val str = StringBuilder()
        val newline = System.getProperty("line.separator")

        for (matchup in matchupSet) {
            str.append(matchup.toString())
            str.append(newline)
        }

        return str.toString()
    }

    fun prettyString(): String {
        val str = StringBuilder()
        val newline = System.getProperty("line.separator")

        for (matchup in matchupSet) {
            str.append(matchup.prettyString())
            str.append(newline)
        }

        return str.toString()
    }
}