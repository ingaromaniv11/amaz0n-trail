package com.Talzere8.Qoxhex.data.repository

import com.Talzere8.Qoxhex.data.local.PreferencesStore
import com.Talzere8.Qoxhex.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val store: PreferencesStore) : SettingsRepository {

    override fun relaxedTimer(): Boolean =
        store.readBoolean(PreferencesStore.KEY_RELAXED_TIMER, false)

    override fun colourCodes(): Boolean =
        store.readBoolean(PreferencesStore.KEY_COLOUR_CODES, false)

    override fun routeHints(): Boolean =
        store.readBoolean(PreferencesStore.KEY_ROUTE_HINTS, true)

    override fun setRelaxedTimer(value: Boolean) {
        store.writeBoolean(PreferencesStore.KEY_RELAXED_TIMER, value)
    }

    override fun setColourCodes(value: Boolean) {
        store.writeBoolean(PreferencesStore.KEY_COLOUR_CODES, value)
    }

    override fun setRouteHints(value: Boolean) {
        store.writeBoolean(PreferencesStore.KEY_ROUTE_HINTS, value)
    }
}
