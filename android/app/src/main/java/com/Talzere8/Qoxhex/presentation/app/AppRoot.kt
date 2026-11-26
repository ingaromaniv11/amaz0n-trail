package com.Talzere8.Qoxhex.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Talzere8.Qoxhex.core.di.AppViewModelFactory
import com.Talzere8.Qoxhex.core.navigation.Screen
import com.Talzere8.Qoxhex.core.theme.BackgroundBase
import com.Talzere8.Qoxhex.presentation.collection.CollectionPanel
import com.Talzere8.Qoxhex.presentation.game.GameScreen
import com.Talzere8.Qoxhex.presentation.levelmap.LevelMapPanel
import com.Talzere8.Qoxhex.presentation.menu.MenuScreen
import com.Talzere8.Qoxhex.presentation.missions.MissionsPanel
import com.Talzere8.Qoxhex.presentation.result.ResultScreen
import com.Talzere8.Qoxhex.presentation.settings.SettingsPanel
import com.Talzere8.Qoxhex.presentation.splash.SplashScreen
import com.Talzere8.Qoxhex.presentation.tutorial.TutorialPanel

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.factory)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val canGoBack by appViewModel.canGoBack.collectAsStateWithLifecycle()

    BackHandler(enabled = canGoBack) { appViewModel.back() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = BackgroundBase)
    ) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                (fadeIn(animationSpec = tween(300)) +
                    slideInVertically(animationSpec = tween(300)) { full -> full / 14 })
                    .togetherWith(fadeOut(animationSpec = tween(400)))
            },
            modifier = Modifier.fillMaxSize()
        ) { current ->
            when (current) {
                is Screen.Splash -> SplashScreen(onFinished = { appViewModel.finishSplash() })
                is Screen.Menu -> MenuScreen(
                    onPlay = { appViewModel.startShift() },
                    onLevels = { appViewModel.navigate(Screen.LevelMap) },
                    onMissions = { appViewModel.navigate(Screen.Missions) },
                    onCollection = { appViewModel.navigate(Screen.Collection) },
                    onTutorial = { appViewModel.navigate(Screen.Tutorial) },
                    onSettings = { appViewModel.navigate(Screen.Settings) }
                )
                is Screen.Game -> GameScreen(
                    runId = current.runId,
                    onMenu = { appViewModel.toMenu() },
                    onShiftEnded = { result -> appViewModel.replace(Screen.Result(result)) }
                )
                is Screen.Result -> ResultScreen(
                    result = current.result,
                    onPlayAgain = { appViewModel.replace(Screen.Game(System.currentTimeMillis())) },
                    onMenu = { appViewModel.toMenu() },
                    onCollection = { appViewModel.navigate(Screen.Collection) }
                )
                is Screen.LevelMap -> LevelMapPanel(onMenu = { appViewModel.toMenu() })
                is Screen.Tutorial -> TutorialPanel(onMenu = { appViewModel.toMenu() })
                is Screen.Collection -> CollectionPanel(onMenu = { appViewModel.toMenu() })
                is Screen.Missions -> MissionsPanel(onMenu = { appViewModel.toMenu() })
                is Screen.Settings -> SettingsPanel(onMenu = { appViewModel.toMenu() })
            }
        }
    }
}
