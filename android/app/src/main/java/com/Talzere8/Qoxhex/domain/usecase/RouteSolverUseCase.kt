package com.Talzere8.Qoxhex.domain.usecase

import com.Talzere8.Qoxhex.core.config.GameConfig
import com.Talzere8.Qoxhex.domain.model.Lattice

class RouteSolverUseCase {

    fun reachableBays(blocked: Set<Int>): Set<Int> {
        var frontier = setOf(0)
        for (row in 0 until GameConfig.ROWS) {
            val next = mutableSetOf<Int>()
            for (index in frontier) {
                if (!blocked.contains(Lattice.branchKey(row, index, false))) next.add(index)
                if (!blocked.contains(Lattice.branchKey(row, index, true))) next.add(index + 1)
            }
            frontier = next
            if (frontier.isEmpty()) return emptySet()
        }
        return frontier
    }

    fun layoutValid(blocked: Set<Int>): Boolean {
        if (reachableBays(blocked).size < GameConfig.COLS) return false
        var frontier = setOf(0)
        for (row in 0 until GameConfig.ROWS) {
            val next = mutableSetOf<Int>()
            for (index in frontier) {
                val sides = openSides(blocked, row, index)
                if (!sides.first && !sides.second) return false
                if (sides.first) next.add(index)
                if (sides.second) next.add(index + 1)
            }
            frontier = next
        }
        return true
    }

    fun canReach(blocked: Set<Int>, bayIndex: Int): Boolean =
        reachableBays(blocked).contains(bayIndex)

    fun openSides(blocked: Set<Int>, row: Int, index: Int): Pair<Boolean, Boolean> =
        Pair(
            !blocked.contains(Lattice.branchKey(row, index, false)),
            !blocked.contains(Lattice.branchKey(row, index, true))
        )
}
