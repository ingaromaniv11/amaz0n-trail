package com.Talzere8.Qoxhex.data.repository

import com.Talzere8.Qoxhex.data.local.PreferencesStore
import com.Talzere8.Qoxhex.domain.repository.CollectionRepository

class CollectionRepositoryImpl(private val store: PreferencesStore) : CollectionRepository {

    override fun unlocked(): Set<Int> {
        val raw = store.readString(PreferencesStore.KEY_STAMPS, "")
        if (raw.isBlank()) return emptySet()
        return raw.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()
    }

    override fun unlock(stampId: Int) {
        if (stampId <= 0) return
        val current = unlocked()
        if (current.contains(stampId)) return
        val updated = (current + stampId).sorted().joinToString(",")
        store.writeString(PreferencesStore.KEY_STAMPS, updated)
    }

    override fun clear() {
        store.removeKeys(listOf(PreferencesStore.KEY_STAMPS))
    }
}
