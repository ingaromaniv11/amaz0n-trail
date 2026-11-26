package com.Talzere8.Qoxhex.domain.usecase

import com.Talzere8.Qoxhex.core.config.GameConfig
import com.Talzere8.Qoxhex.domain.model.Bay
import com.Talzere8.Qoxhex.domain.model.Lattice
import com.Talzere8.Qoxhex.domain.model.Parcel
import com.Talzere8.Qoxhex.domain.model.RouteColour
import com.Talzere8.Qoxhex.domain.model.ShiftLayout
import com.Talzere8.Qoxhex.domain.model.Stage
import com.Talzere8.Qoxhex.domain.model.Town
import java.util.Random

class GenerateShiftUseCase(private val solver: RouteSolverUseCase) {

    fun seedFor(stageIndex: Int, attempt: Int): Long =
        (stageIndex.toLong() * 7919L) xor (attempt.toLong() * 104729L)

    operator fun invoke(stage: Stage, attempt: Int): ShiftLayout {
        val seed = seedFor(stage.index, attempt)
        val random = Random(seed)
        val bays = buildBays(random)
        val blocked = tapeShelves(random, stage.tapedShelves)
        val parcels = buildParcels(random, bays, stage.quota, blocked)
        return ShiftLayout(seed, stage.index, bays, blocked, parcels)
    }

    private fun buildBays(random: Random): List<Bay> {
        val pairs = mutableListOf<Pair<RouteColour, Town>>()
        for (colour in RouteColour.entries) {
            for (town in Town.entries) {
                pairs.add(Pair(colour, town))
            }
        }
        pairs.shuffle(random)
        val chosen = mutableListOf<Pair<RouteColour, Town>>()
        val usedColours = mutableMapOf<RouteColour, Int>()
        for (pair in pairs) {
            if (chosen.size >= GameConfig.COLS) break
            val taken = usedColours[pair.first] ?: 0
            if (taken >= 2) continue
            chosen.add(pair)
            usedColours[pair.first] = taken + 1
        }
        var cursor = 0
        while (chosen.size < GameConfig.COLS && cursor < pairs.size) {
            val candidate = pairs[cursor]
            if (!chosen.contains(candidate)) chosen.add(candidate)
            cursor++
        }
        return chosen.mapIndexed { index, pair -> Bay(index, pair.first, pair.second) }
    }

    private fun tapeShelves(random: Random, wanted: Int): Set<Int> {
        if (wanted <= 0) return emptySet()
        var best = emptySet<Int>()
        for (attempt in 0 until GameConfig.GENERATOR_ATTEMPTS) {
            val order = Lattice.allBranches().toMutableList()
            order.shuffle(random)
            val taped = mutableSetOf<Int>()
            for (key in order) {
                if (taped.size >= wanted) break
                taped.add(key)
                if (!solver.layoutValid(taped)) taped.remove(key)
            }
            if (taped.size > best.size) best = taped.toSet()
            if (best.size >= wanted) return best
        }
        return best
    }

    private fun buildParcels(
        random: Random,
        bays: List<Bay>,
        quota: Int,
        blocked: Set<Int>
    ): List<Parcel> {
        val parcels = mutableListOf<Parcel>()
        var previous = -1
        for (number in 1..quota) {
            var bayIndex = bays[random.nextInt(bays.size)].index
            var guard = 0
            while ((bayIndex == previous || !solver.canReach(blocked, bayIndex)) && guard < 24) {
                bayIndex = bays[random.nextInt(bays.size)].index
                guard++
            }
            if (!solver.canReach(blocked, bayIndex)) {
                bayIndex = solver.reachableBays(blocked).firstOrNull() ?: bayIndex
            }
            previous = bayIndex
            val bay = bays[bayIndex]
            parcels.add(
                Parcel(
                    number = number,
                    colour = bay.colour,
                    town = bay.town,
                    rush = number == GameConfig.RUSH_PARCEL_NUMBER
                )
            )
        }
        return parcels
    }
}
