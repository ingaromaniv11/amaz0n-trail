package com.Talzere8.Qoxhex.data.local

import android.content.Context
import android.content.SharedPreferences

class PreferencesStore(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

    fun readInt(key: String, fallback: Int): Int = prefs.getInt(key, fallback)

    fun writeInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun readBoolean(key: String, fallback: Boolean): Boolean = prefs.getBoolean(key, fallback)

    fun writeBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun readString(key: String, fallback: String): String = prefs.getString(key, fallback) ?: fallback

    fun writeString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun removeKeys(keys: List<String>) {
        val editor = prefs.edit()
        for (key in keys) editor.remove(key)
        editor.apply()
    }

    companion object {
        const val STORE_NAME = "parcel_trail_prefs"
        const val KEY_BEST_SCORE = "best_score"
        const val KEY_STAGES_UNLOCKED = "stages_unlocked"
        const val KEY_SELECTED_STAGE = "selected_stage"
        const val KEY_TOTAL_DELIVERED = "total_delivered"
        const val KEY_PERFECT_SHIFTS = "perfect_shifts"
        const val KEY_STAGE_BEST = "stage_best_"
        const val KEY_STAMPS = "stamps"
        const val KEY_MISSION_SLOT = "mission_slot_"
        const val KEY_MISSION_PROGRESS = "mission_progress_"
        const val KEY_RELAXED_TIMER = "opt_relaxed_timer"
        const val KEY_COLOUR_CODES = "opt_colour_codes"
        const val KEY_ROUTE_HINTS = "opt_route_hints"
    }
}
