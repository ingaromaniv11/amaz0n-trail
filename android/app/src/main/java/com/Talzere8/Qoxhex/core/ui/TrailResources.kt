package com.Talzere8.Qoxhex.core.ui

import com.Talzere8.Qoxhex.R
import com.Talzere8.Qoxhex.domain.model.RouteColour
import com.Talzere8.Qoxhex.domain.model.Town

object TrailResources {

    fun sprite(slot: Int): Int = when (slot) {
        1 -> R.drawable.sprite_1
        2 -> R.drawable.sprite_2
        3 -> R.drawable.sprite_3
        4 -> R.drawable.sprite_4
        else -> R.drawable.sprite_5
    }

    fun cart(): Int = R.drawable.sprite_5

    fun background(): Int = R.drawable.bg_game

    fun logo(): Int = R.drawable.logo

    fun homeGlyph(): Int = R.drawable.icon_home

    fun pauseGlyph(): Int = R.drawable.icon_pause

    fun townLabel(town: Town): Int = when (town) {
        Town.RIVERSIDE -> R.string.town_riverside
        Town.OAK_PARK -> R.string.town_oak_park
        Town.NORTH_DOCK -> R.string.town_north_dock
        Town.HILLSIDE -> R.string.town_hillside
    }

    fun colourLabel(colour: RouteColour): Int = when (colour) {
        RouteColour.ORANGE -> R.string.colour_orange
        RouteColour.COBALT -> R.string.colour_cobalt
        RouteColour.PAPER -> R.string.colour_paper
        RouteColour.GRAPHITE -> R.string.colour_graphite
    }

    fun stageLabel(index: Int): Int = when (index) {
        1 -> R.string.stage_1
        2 -> R.string.stage_2
        3 -> R.string.stage_3
        4 -> R.string.stage_4
        5 -> R.string.stage_5
        else -> R.string.stage_6
    }

    fun stampLabel(id: Int): Int = when (id) {
        1 -> R.string.stamp_1
        2 -> R.string.stamp_2
        3 -> R.string.stamp_3
        4 -> R.string.stamp_4
        5 -> R.string.stamp_5
        6 -> R.string.stamp_6
        7 -> R.string.stamp_7
        8 -> R.string.stamp_8
        9 -> R.string.stamp_9
        10 -> R.string.stamp_10
        11 -> R.string.stamp_11
        else -> R.string.stamp_12
    }

    fun stampCondition(id: Int): Int = when (id) {
        1 -> R.string.stamp_cond_1
        2 -> R.string.stamp_cond_2
        3 -> R.string.stamp_cond_3
        4 -> R.string.stamp_cond_4
        5 -> R.string.stamp_cond_5
        6 -> R.string.stamp_cond_6
        7 -> R.string.stamp_cond_7
        8 -> R.string.stamp_cond_8
        9 -> R.string.stamp_cond_9
        10 -> R.string.stamp_cond_10
        11 -> R.string.stamp_cond_11
        else -> R.string.stamp_cond_12
    }

    fun missionLabel(id: Int): Int = when (id) {
        1 -> R.string.mission_1
        2 -> R.string.mission_2
        3 -> R.string.mission_3
        4 -> R.string.mission_4
        5 -> R.string.mission_5
        else -> R.string.mission_6
    }
}
