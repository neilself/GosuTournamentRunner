package org.nself.gtrunner.bracket

import org.nself.gtrunner.dataobject.Team
import org.nself.gtrunner.strategy.SeedingStrategy

class SwissBracket(name: String, teamSet: Set<Team>, seedingStrategy: SeedingStrategy) :
    AbstractBracket(name, teamSet, seedingStrategy) {

    override fun hasDependentRounds(): Boolean {
        return true
    }
}