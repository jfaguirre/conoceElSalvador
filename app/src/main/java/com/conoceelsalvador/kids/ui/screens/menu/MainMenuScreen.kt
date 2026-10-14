package com.conoceelsalvador.kids.ui.screens.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.conoceelsalvador.kids.data.model.UserProfile
import com.conoceelsalvador.kids.ui.components.MascotMood
import com.conoceelsalvador.kids.ui.components.TorogozAvatar

@Composable
fun MainMenuScreen(
    userProfile: UserProfile,
    onPlayMapGame: () -> Unit,
    onOpenRankings: () -> Unit,
    onManagePlayers: () -> Unit,
    onChangeNickname: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF1F5F9)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Cabecera con Torogoz, Avatar del Jugador y saludo personalizado
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF0284C7)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TorogozAvatar(
                                mood = MascotMood.HAPPY,
                                modifier = Modifier.size(60.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            // Avatar elegido por el niño
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(Color.White.copy(alpha = 0.25f), CircleShape)
                                    .border(1.5.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = userProfile.avatarEmoji, fontSize = 24.sp)
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "¡Hola, ${userProfile.nickname.ifBlank { "Explorador" }}!",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "⭐ ${userProfile.totalStars} Estrellas acumuladas",
                                    fontSize = 13.sp,
                                    color = Color(0xFFFEF08A)
                                )
                                if (userProfile.mapBestScore > 0) {
                                    Text(
                                        text = "🏆 Récord: ${userProfile.mapBestScore} pts (${userProfile.mapBestTimeSeconds}s)",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                            }

                            // Botón de Jugadores
                            IconButton(
                                onClick = onManagePlayers,
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.25f), CircleShape)
                                    .size(38.dp)
                            ) {
                                Text(text = "👥", fontSize = 16.sp)
                            }
                        }
                    }
                }
            }

            // Sección Salón de la Fama y Jugadores
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Tarjeta Rápida de Rankings
                    QuickActionCard(
                        title = "🏆 Salón de Fama",
                        subtitle = "Ver Rankings",
                        backgroundColor = Color(0xFFFEF3C7),
                        accentColor = Color(0xFFB45309),
                        onClick = onOpenRankings,
                        modifier = Modifier.weight(1f)
                    )

                    // Tarjeta Rápida de Jugadores
                    QuickActionCard(
                        title = "👥 Jugadores",
                        subtitle = "Cambiar / Crear",
                        backgroundColor = Color(0xFFE0F2FE),
                        accentColor = Color(0xFF0284C7),
                        onClick = onManagePlayers,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Text(
                    text = "Elige un Minijuego:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            // Minijuego 1: El Mapa de El Salvador (Activo)
            item {
                MinigameCard(
                    title = "El Mapa de El Salvador",
                    subtitle = "Arrastra los 14 departamentos a su lugar correcto",
                    icon = "🗺️",
                    badgeText = "¡Disponible!",
                    badgeColor = Color(0xFF00C853),
                    isUnlocked = true,
                    onClick = onPlayMapGame
                )
            }

            // Minijuego 2: El Rincón de la Pupusa (Próximamente)
            item {
                MinigameCard(
                    title = "El Rincón de la Pupusa",
                    subtitle = "Descubre las ricas comidas típicas y sus ingredientes",
                    icon = "🫓",
                    badgeText = "Próximamente",
                    badgeColor = Color(0xFF94A3B8),
                    isUnlocked = false,
                    onClick = {}
                )
            }

            // Minijuego 3: Símbolos Patrios (Próximamente)
            item {
                MinigameCard(
                    title = "Nuestros Símbolos Patrios",
                    subtitle = "Aprende sobre el Torogoz, Maquilíshuat y la Flor de Izote",
                    icon = "🌺",
                    badgeText = "Próximamente",
                    badgeColor = Color(0xFF94A3B8),
                    isUnlocked = false,
                    onClick = {}
                )
            }

            // Minijuego 4: Mitos y Leyendas (Próximamente)
            item {
                MinigameCard(
                    title = "Mitos y Leyendas",
                    subtitle = "Conoce las historias de El Cipitio, El Cadejo y volcanes",
                    icon = "🌋",
                    badgeText = "Próximamente",
                    badgeColor = Color(0xFF94A3B8),
                    isUnlocked = false,
                    onClick = {}
                )
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    backgroundColor: Color,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = accentColor.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun MinigameCard(
    title: String,
    subtitle: String,
    icon: String,
    badgeText: String,
    badgeColor: Color,
    isUnlocked: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isUnlocked, onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) Color.White else Color(0xFFF8FAFC)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 4.dp else 1.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isUnlocked) Color(0xFFE2E8F0) else Color(0xFFE2E8F0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del juego
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = if (isUnlocked) Color(0xFFE0F2FE) else Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) Color(0xFF1E293B) else Color(0xFF64748B)
                    )

                    Box(
                        modifier = Modifier
                            .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 16.sp
                )
            }
        }
    }
}
