package com.conoceelsalvador.kids

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.conoceelsalvador.kids.data.UserPreferencesRepository
import com.conoceelsalvador.kids.ui.screens.mapgame.MapGameScreen
import com.conoceelsalvador.kids.ui.screens.mapgame.MapGameViewModel
import com.conoceelsalvador.kids.ui.screens.menu.MainMenuScreen
import com.conoceelsalvador.kids.ui.screens.players.PlayerManagementScreen
import com.conoceelsalvador.kids.ui.screens.ranking.RankingsScreen
import com.conoceelsalvador.kids.ui.screens.welcome.WelcomeScreen

@Composable
fun MainNavigation() {
    val context = LocalContext.current
    val repository = remember { UserPreferencesRepository(context) }
    val activePlayer by repository.activePlayer.collectAsState()

    // Si ya tiene un jugador registrado y activo, va al menú principal; si no, a la bienvenida
    val initialKey = if (activePlayer.nickname.isNotBlank()) MainMenuNavKey else WelcomeNavKey
    val backStack = rememberNavBackStack(initialKey)

    NavDisplay(
        backStack = backStack,
        onBack = {
            if (backStack.size > 1) {
                backStack.removeLastOrNull()
            }
        },
        entryProvider = entryProvider {
            entry<WelcomeNavKey> {
                WelcomeScreen(
                    currentNickname = activePlayer.nickname,
                    onContinue = { newNickname, avatarId ->
                        repository.createPlayer(newNickname, avatarId)
                        backStack.clear()
                        backStack.add(MainMenuNavKey)
                    },
                    modifier = Modifier.fillMaxSize().safeDrawingPadding()
                )
            }

            entry<MainMenuNavKey> {
                MainMenuScreen(
                    userProfile = activePlayer,
                    onPlayMapGame = {
                        backStack.add(MapGameNavKey)
                    },
                    onOpenRankings = {
                        backStack.add(RankingsNavKey)
                    },
                    onManagePlayers = {
                        backStack.add(PlayerManagementNavKey)
                    },
                    onChangeNickname = {
                        backStack.add(PlayerManagementNavKey)
                    },
                    modifier = Modifier.fillMaxSize().safeDrawingPadding()
                )
            }

            entry<MapGameNavKey> {
                val mapGameViewModel = remember { MapGameViewModel(repository) }
                MapGameScreen(
                    nickname = activePlayer.nickname,
                    viewModel = mapGameViewModel,
                    onBackToMenu = {
                        backStack.removeLastOrNull()
                    },
                    modifier = Modifier.fillMaxSize().safeDrawingPadding()
                )
            }

            entry<RankingsNavKey> {
                RankingsScreen(
                    repository = repository,
                    activePlayerId = activePlayer.id,
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    modifier = Modifier.fillMaxSize().safeDrawingPadding()
                )
            }

            entry<PlayerManagementNavKey> {
                PlayerManagementScreen(
                    repository = repository,
                    onBack = {
                        if (repository.activePlayer.value.nickname.isBlank()) {
                            backStack.clear()
                            backStack.add(WelcomeNavKey)
                        } else {
                            backStack.removeLastOrNull()
                        }
                    },
                    modifier = Modifier.fillMaxSize().safeDrawingPadding()
                )
            }
        }
    )
}
