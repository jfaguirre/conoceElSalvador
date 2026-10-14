package com.conoceelsalvador.kids.data.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.conoceelsalvador.kids.data.model.UserProfile

class PlayerDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    companion object {
        const val DATABASE_NAME = "conoce_el_salvador.db"
        const val DATABASE_VERSION = 1

        const val TABLE_PLAYERS = "players"
        const val COL_ID = "id"
        const val COL_NICKNAME = "nickname"
        const val COL_TOTAL_STARS = "total_stars"
        const val COL_MAP_BEST_SCORE = "map_best_score"
        const val COL_MAP_BEST_TIME = "map_best_time"
        const val COL_AVATAR_ID = "avatar_id"
        const val COL_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createPlayersTable = """
            CREATE TABLE $TABLE_PLAYERS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NICKNAME TEXT NOT NULL,
                $COL_TOTAL_STARS INTEGER NOT NULL DEFAULT 0,
                $COL_MAP_BEST_SCORE INTEGER NOT NULL DEFAULT 0,
                $COL_MAP_BEST_TIME INTEGER NOT NULL DEFAULT 0,
                $COL_AVATAR_ID TEXT NOT NULL DEFAULT 'torogoz',
                $COL_CREATED_AT INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createPlayersTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Reservado para futuras migraciones de esquema
    }

    fun insertPlayer(
        nickname: String,
        avatarId: String = "torogoz",
        totalStars: Int = 0,
        mapBestScore: Int = 0,
        mapBestTimeSeconds: Int = 0
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NICKNAME, nickname.trim())
            put(COL_TOTAL_STARS, totalStars)
            put(COL_MAP_BEST_SCORE, mapBestScore)
            put(COL_MAP_BEST_TIME, mapBestTimeSeconds)
            put(COL_AVATAR_ID, avatarId)
            put(COL_CREATED_AT, System.currentTimeMillis())
        }
        return db.insert(TABLE_PLAYERS, null, values)
    }

    fun getAllPlayers(): List<UserProfile> {
        val db = readableDatabase
        val list = mutableListOf<UserProfile>()
        val cursor = db.query(
            TABLE_PLAYERS,
            null,
            null,
            null,
            null,
            null,
            "$COL_CREATED_AT ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToUserProfile(it))
            }
        }
        return list
    }

    fun getPlayerById(id: Long): UserProfile? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_PLAYERS,
            null,
            "$COL_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        cursor.use {
            if (it.moveToNext()) {
                return cursorToUserProfile(it)
            }
        }
        return null
    }

    fun updatePlayerScore(id: Long, starsToAdd: Int, score: Int, timeSeconds: Int) {
        val current = getPlayerById(id) ?: return
        val newStars = current.totalStars + starsToAdd
        val newBestScore = maxOf(current.mapBestScore, score)
        val newBestTime = if (current.mapBestTimeSeconds == 0) {
            timeSeconds
        } else {
            minOf(current.mapBestTimeSeconds, timeSeconds)
        }

        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_TOTAL_STARS, newStars)
            put(COL_MAP_BEST_SCORE, newBestScore)
            put(COL_MAP_BEST_TIME, newBestTime)
        }
        db.update(TABLE_PLAYERS, values, "$COL_ID = ?", arrayOf(id.toString()))
    }

    fun updatePlayerNickname(id: Long, newNickname: String, avatarId: String? = null) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NICKNAME, newNickname.trim())
            if (avatarId != null) {
                put(COL_AVATAR_ID, avatarId)
            }
        }
        db.update(TABLE_PLAYERS, values, "$COL_ID = ?", arrayOf(id.toString()))
    }

    fun deletePlayer(id: Long): Boolean {
        val db = writableDatabase
        val rows = db.delete(TABLE_PLAYERS, "$COL_ID = ?", arrayOf(id.toString()))
        return rows > 0
    }

    fun getTopPlayersByScore(limit: Int = 50): List<UserProfile> {
        val db = readableDatabase
        val list = mutableListOf<UserProfile>()
        val cursor = db.query(
            TABLE_PLAYERS,
            null,
            null,
            null,
            null,
            null,
            "$COL_MAP_BEST_SCORE DESC, $COL_TOTAL_STARS DESC",
            limit.toString()
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToUserProfile(it))
            }
        }
        return list
    }

    fun getTopPlayersByStars(limit: Int = 50): List<UserProfile> {
        val db = readableDatabase
        val list = mutableListOf<UserProfile>()
        val cursor = db.query(
            TABLE_PLAYERS,
            null,
            null,
            null,
            null,
            null,
            "$COL_TOTAL_STARS DESC, $COL_MAP_BEST_SCORE DESC",
            limit.toString()
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToUserProfile(it))
            }
        }
        return list
    }

    fun getTopPlayersByTime(limit: Int = 50): List<UserProfile> {
        val db = readableDatabase
        val list = mutableListOf<UserProfile>()
        val query = """
            SELECT * FROM $TABLE_PLAYERS
            ORDER BY 
                CASE WHEN $COL_MAP_BEST_TIME > 0 THEN 0 ELSE 1 END ASC,
                $COL_MAP_BEST_TIME ASC,
                $COL_MAP_BEST_SCORE DESC
            LIMIT $limit
        """.trimIndent()

        val cursor = db.rawQuery(query, null)
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToUserProfile(it))
            }
        }
        return list
    }

    private fun cursorToUserProfile(c: Cursor): UserProfile {
        return UserProfile(
            id = c.getLong(c.getColumnIndexOrThrow(COL_ID)),
            nickname = c.getString(c.getColumnIndexOrThrow(COL_NICKNAME)),
            totalStars = c.getInt(c.getColumnIndexOrThrow(COL_TOTAL_STARS)),
            mapBestScore = c.getInt(c.getColumnIndexOrThrow(COL_MAP_BEST_SCORE)),
            mapBestTimeSeconds = c.getInt(c.getColumnIndexOrThrow(COL_MAP_BEST_TIME)),
            avatarId = c.getString(c.getColumnIndexOrThrow(COL_AVATAR_ID)),
            createdAt = c.getLong(c.getColumnIndexOrThrow(COL_CREATED_AT))
        )
    }
}
