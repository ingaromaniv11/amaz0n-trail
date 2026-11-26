package com.Talzere8.Qoxhex.core.di

import android.content.Context
import com.Talzere8.Qoxhex.data.local.PreferencesStore
import com.Talzere8.Qoxhex.data.repository.CollectionRepositoryImpl
import com.Talzere8.Qoxhex.data.repository.MissionRepositoryImpl
import com.Talzere8.Qoxhex.data.repository.ProgressRepositoryImpl
import com.Talzere8.Qoxhex.data.repository.SettingsRepositoryImpl
import com.Talzere8.Qoxhex.domain.repository.CollectionRepository
import com.Talzere8.Qoxhex.domain.repository.MissionRepository
import com.Talzere8.Qoxhex.domain.repository.ProgressRepository
import com.Talzere8.Qoxhex.domain.repository.SettingsRepository
import com.Talzere8.Qoxhex.domain.usecase.EvaluateShiftUseCase
import com.Talzere8.Qoxhex.domain.usecase.GenerateShiftUseCase
import com.Talzere8.Qoxhex.domain.usecase.RouteSolverUseCase
import com.Talzere8.Qoxhex.domain.usecase.ScoreDeliveryUseCase

object ServiceLocator {

    private var store: PreferencesStore? = null

    val routeSolver: RouteSolverUseCase by lazy { RouteSolverUseCase() }
    val generateShift: GenerateShiftUseCase by lazy { GenerateShiftUseCase(routeSolver) }
    val scoreDelivery: ScoreDeliveryUseCase by lazy { ScoreDeliveryUseCase() }
    val evaluateShift: EvaluateShiftUseCase by lazy { EvaluateShiftUseCase() }

    val progressRepository: ProgressRepository by lazy { ProgressRepositoryImpl(requireStore()) }
    val collectionRepository: CollectionRepository by lazy { CollectionRepositoryImpl(requireStore()) }
    val missionRepository: MissionRepository by lazy { MissionRepositoryImpl(requireStore()) }
    val settingsRepository: SettingsRepository by lazy { SettingsRepositoryImpl(requireStore()) }

    fun init(context: Context) {
        if (store == null) store = PreferencesStore(context.applicationContext)
    }

    private fun requireStore(): PreferencesStore {
        val current = store
        if (current != null) return current
        throw IllegalStateException(ERROR_NOT_READY)
    }

    private const val ERROR_NOT_READY = "service locator not initialised"
}
