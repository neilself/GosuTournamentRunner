package org.nself.gtrunner.strategy.rule

import org.nself.gtrunner.dataobject.Matchup

/**
 * An interface for applying rules to matchup generation.
 */
interface MatchupFormationRule {

    /**
     * Returns whether a matchup is considered valid or not for this rule
     */
    fun isMatchupValid(matchup: Matchup): Boolean
}