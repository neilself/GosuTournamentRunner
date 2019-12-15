package org.nself.gtrunner.strategy

import org.nself.gtrunner.dataobject.Round
import org.nself.gtrunner.dataobject.Team

/**
 * An interface for different 'strategies' for generating new rounds.
 */
interface SeedingStrategy {
    fun generateNewRound(roundId: Int, originalTeamSet: Set<Team>): Round
}