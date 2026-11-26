package com.Talzere8.Qoxhex.core.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.Talzere8.Qoxhex.presentation.app.AppViewModel
import com.Talzere8.Qoxhex.presentation.collection.CollectionViewModel
import com.Talzere8.Qoxhex.presentation.game.GameViewModel
import com.Talzere8.Qoxhex.presentation.levelmap.LevelMapViewModel
import com.Talzere8.Qoxhex.presentation.menu.MenuViewModel
import com.Talzere8.Qoxhex.presentation.missions.MissionsViewModel
import com.Talzere8.Qoxhex.presentation.result.ResultViewModel
import com.Talzere8.Qoxhex.presentation.settings.SettingsViewModel
import com.Talzere8.Qoxhex.presentation.splash.SplashViewModel

object AppViewModelFactory {

    val factory: ViewModelProvider.Factory = viewModelFactory {
        initializer { AppViewModel() }
        initializer { SplashViewModel() }
        initializer {
            MenuViewModel(
                ServiceLocator.progressRepository,
                ServiceLocator.collectionRepository
            )
        }
        initializer {
            GameViewModel(
                ServiceLocator.progressRepository,
                ServiceLocator.collectionRepository,
                ServiceLocator.missionRepository,
                ServiceLocator.settingsRepository,
                ServiceLocator.generateShift,
                ServiceLocator.routeSolver,
                ServiceLocator.scoreDelivery,
                ServiceLocator.evaluateShift
            )
        }
        initializer {
            ResultViewModel(
                ServiceLocator.progressRepository,
                ServiceLocator.collectionRepository
            )
        }
        initializer { LevelMapViewModel(ServiceLocator.progressRepository) }
        initializer { CollectionViewModel(ServiceLocator.collectionRepository) }
        initializer {
            MissionsViewModel(
                ServiceLocator.missionRepository,
                ServiceLocator.collectionRepository
            )
        }
        initializer {
            SettingsViewModel(
                ServiceLocator.settingsRepository,
                ServiceLocator.progressRepository,
                ServiceLocator.collectionRepository,
                ServiceLocator.missionRepository
            )
        }
    }
}
