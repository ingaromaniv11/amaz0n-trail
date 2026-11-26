package com.Talzere8.Qoxhex.presentation.app

import androidx.lifecycle.ViewModel
import com.Talzere8.Qoxhex.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val backStack = mutableListOf<Screen>()

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current != Screen.Splash) backStack.add(current)
        _screen.value = target
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun replace(target: Screen) {
        _screen.value = target
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun toMenu() {
        backStack.clear()
        _screen.value = Screen.Menu
        _canGoBack.value = false
    }

    fun startShift() {
        navigate(Screen.Game(System.currentTimeMillis()))
    }

    fun back() {
        if (backStack.isEmpty()) return
        val previous = backStack.removeAt(backStack.size - 1)
        _screen.value = previous
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun finishSplash() {
        backStack.clear()
        _screen.value = Screen.Menu
        _canGoBack.value = false
    }
}
