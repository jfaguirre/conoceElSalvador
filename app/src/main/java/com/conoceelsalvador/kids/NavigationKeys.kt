package com.conoceelsalvador.kids

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object WelcomeNavKey : NavKey

@Serializable
data object MainMenuNavKey : NavKey

@Serializable
data object MapGameNavKey : NavKey

@Serializable
data object RankingsNavKey : NavKey

@Serializable
data object PlayerManagementNavKey : NavKey
