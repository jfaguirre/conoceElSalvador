package com.conoceelsalvador.kids.data.model

data class AvatarOption(
    val id: String,
    val emoji: String,
    val name: String
)

val DEFAULT_AVATARS = listOf(
    AvatarOption("torogoz", "🦜", "Torogoz"),
    AvatarOption("izote", "🌺", "Flor de Izote"),
    AvatarOption("maquilishuat", "🌸", "Maquilíshuat"),
    AvatarOption("volcan", "🌋", "Volcán"),
    AvatarOption("pupusa", "🫓", "Pupusa"),
    AvatarOption("playa", "🌊", "El Tunco"),
    AvatarOption("tigrillo", "🐆", "Tigrillo"),
    AvatarOption("sol", "🌞", "Sol"),
    AvatarOption("estrella", "⭐", "Estrella"),
    AvatarOption("campeon", "👑", "Campeón")
)

data class UserProfile(
    val id: Long = 0,
    val nickname: String = "",
    val totalStars: Int = 0,
    val mapBestScore: Int = 0,
    val mapBestTimeSeconds: Int = 0,
    val avatarId: String = "torogoz",
    val createdAt: Long = System.currentTimeMillis()
) {
    val avatarEmoji: String
        get() = DEFAULT_AVATARS.firstOrNull { it.id == avatarId }?.emoji ?: "🦜"
}

data class GameScore(
    val timeSeconds: Int,
    val attempts: Int,
    val mistakes: Int,
    val stars: Int,
    val points: Int
) {
    companion object {
        fun calculate(timeSeconds: Int, mistakes: Int): GameScore {
            // Puntuación: base 1000 menos penalizaciones leves
            val timePenalty = timeSeconds * 2
            val mistakePenalty = mistakes * 40
            val points = (1000 - timePenalty - mistakePenalty).coerceAtLeast(100)

            // Cálculo de 1 a 3 estrellas amigable para niños
            val stars = when {
                mistakes <= 2 && timeSeconds <= 120 -> 3
                mistakes <= 5 -> 2
                else -> 1
            }

            return GameScore(
                timeSeconds = timeSeconds,
                attempts = 14 + mistakes,
                mistakes = mistakes,
                stars = stars,
                points = points
            )
        }
    }
}
