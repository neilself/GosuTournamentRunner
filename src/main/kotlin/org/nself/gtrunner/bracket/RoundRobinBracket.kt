package org.nself.gtrunner.bracket

import org.nself.gtrunner.dataobject.Team
import org.nself.gtrunner.strategy.RoundRobinSeedingStrategy

class RoundRobinBracket(name: String, teamSet: Set<Team>) :
    AbstractBracket(name, teamSet, RoundRobinSeedingStrategy()) {
    override fun hasDependentRounds(): Boolean {
        return false
    }

    override fun naturalNumberOfRounds(): Int {
        // TODO: This appears to be accurate for even numbers of teams (so nobody sits out rounds), but we need to
        //       investigate whether it's still accurate for odd numbers of teams.
        return teamSet.size - 1
    }
}