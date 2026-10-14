package com.conoceelsalvador.kids.data

import android.content.Context
import android.content.SharedPreferences
import com.conoceelsalvador.kids.data.db.PlayerDatabaseHelper
import com.conoceelsalvador.kids.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class RankingCriteria {
    SCORE,
    STARS,
    TIME
}

class UserPreferencesRepository(context: Context) {
    private val dbHelper = PlayerDatabaseHelper(context)
    private val prefs: SharedPreferences =
        context.getSharedPreferences("conoce_el_salvador_prefs", Context.MODE_PRIVATE)

    private val _activePlayer = MutableStateFlow(UserProfile())
    val activePlayer: StateFlow<UserProfile> = _activePlayer.asStateFlow()
    val userProfile: StateFlow<UserProfile> = _activePlayer.asStateFlow()

    private val _allPlayers = MutableStateFlow<List<UserProfile>>(emptyList())
    val allPlayers: StateFlow<List<UserProfile>> = _allPlayers.asStateFlow()

    init {
        migrateFromPrefsIfNeeded()
        reloadPlayers()
    }

    private fun migrateFromPrefsIfNeeded() {
        val existingPlayers = dbHelper.getAllPlayers()
        if (existingPlayers.isEmpty()) {
            val oldNickname = prefs.getString(KEY_OLD_NICKNAME, "") ?: ""
            if (oldNickname.isNotBlank()) {
                val oldStars = prefs.getInt(KEY_OLD_STARS, 0)
                val oldScore = prefs.getInt(KEY_OLD_MAP_BEST_SCORE, 0)
                val oldTime = prefs.getInt(KEY_OLD_MAP_BEST_TIME, 0)
                val newId = dbHelper.insertPlayer(
                    nickname = oldNickname,
                    avatarId = "torogoz",
                    totalStars = oldStars,
                    mapBestScore = oldScore,
                    mapBestTimeSeconds = oldTime
                )
                prefs.edit().putLong(KEY_ACTIVE_PLAYER_ID, newId).apply()
            }
        }
    }

    private fun reloadPlayers() {
        val players = dbHelper.getAllPlayers()
        _allPlayers.value = players

        if (players.isEmpty()) {
            _activePlayer.value = UserProfile()
            return
        }

        val activeId = prefs.getLong(KEY_ACTIVE_PLAYER_ID, -1L)
        val selected = players.find { it.id == activeId } ?: players.first()

        prefs.edit().putLong(KEY_ACTIVE_PLAYER_ID, selected.id).apply()
        _activePlayer.value = selected
    }

    fun createPlayer(nickname: String, avatarId: String = "torogoz"): UserProfile {
        val trimmed = nickname.trim().ifBlank { "Explorador" }
        val newId = dbHelper.insertPlayer(nickname = trimmed, avatarId = avatarId)
        prefs.edit().putLong(KEY_ACTIVE_PLAYER_ID, newId).apply()
        reloadPlayers()
        return _activePlayer.value
    }

    fun selectPlayer(playerId: Long) {
        val target = dbHelper.getPlayerById(playerId)
        if (target != null) {
            prefs.edit().putLong(KEY_ACTIVE_PLAYER_ID, playerId).apply()
            reloadPlayers()
        }
    }

    fun deletePlayer(playerId: Long): Boolean {
        val success = dbHelper.deletePlayer(playerId)
        if (success) {
            val currentActiveId = prefs.getLong(KEY_ACTIVE_PLAYER_ID, -1L)
            if (currentActiveId == playerId) {
                // Selecciona el primer jugador restante
                val remaining = dbHelper.getAllPlayers()
                if (remaining.isNotEmpty()) {
                    prefs.edit().putLong(KEY_ACTIVE_PLAYER_ID, remaining.first().id).apply()
                } else {
                    prefs.edit().remove(KEY_ACTIVE_PLAYER_ID).apply()
                }
            }
            reloadPlayers()
        }
        return success
    }

    fun saveNickname(nickname: String) {
        val current = _activePlayer.value
        if (current.id > 0) {
            dbHelper.updatePlayerNickname(current.id, nickname)
            reloadPlayers()
        } else {
            createPlayer(nickname)
        }
    }

    fun updatePlayerInfo(playerId: Long, nickname: String, avatarId: String) {
        dbHelper.updatePlayerNickname(playerId, nickname, avatarId)
        reloadPlayers()
    }

    fun saveGameResult(starsEarned: Int, score: Int, timeSeconds: Int) {
        val current = _activePlayer.value
        if (current.id <= 0) return

        dbHelper.updatePlayerScore(
            id = current.id,
            starsToAdd = starsEarned,
            score = score,
            timeSeconds = timeSeconds
        )
        reloadPlayers()
    }

    fun getRankings(criteria: RankingCriteria): List<UserProfile> {
        return when (criteria) {
            RankingCriteria.SCORE -> dbHelper.getTopPlayersByScore()
            RankingCriteria.STARS -> dbHelper.getTopPlayersByStars()
            RankingCriteria.TIME -> dbHelper.getTopPlayersByTime()
        }
    }

    companion object {
        private const val KEY_ACTIVE_PLAYER_ID = "active_player_id"

        // Claves antiguas para migración transparente
        private const val KEY_OLD_NICKNAME = "user_nickname"
        private const val KEY_OLD_STARS = "user_total_stars"
        private const val KEY_OLD_MAP_BEST_SCORE = "map_best_score"
        private const val KEY_OLD_MAP_BEST_TIME = "map_best_time"
    }
}
