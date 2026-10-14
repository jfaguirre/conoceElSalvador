package com.conoceelsalvador.kids.ui.screens.ranking

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.conoceelsalvador.kids.data.RankingCriteria
import com.conoceelsalvador.kids.data.UserPreferencesRepository
import com.conoceelsalvador.kids.data.model.UserProfile
import com.conoceelsalvador.kids.ui.components.MascotMood
import com.conoceelsalvador.kids.ui.components.TorogozAvatar

@Composable
fun RankingsScreen(
    repository: UserPreferencesRepository,
    activePlayerId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCriteria by remember { mutableStateOf(RankingCriteria.SCORE) }
    val players = remember(selectedCriteria, repository.allPlayers) {
        repository.getRankings(selectedCriteria)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF1F5F9)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Barra Superior
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .background(Color.White, CircleShape)
                        .size(42.dp)
                ) {
                    Text(text = "←", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "🏆 Salón de la Fama",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Rankings de Exploradores",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Selector de Criterio de Ranking (Puntos, Estrellas, Tiempo)
            RankingTabs(
                selected = selectedCriteria,
                onSelect = { selectedCriteria = it },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Contenido del Ranking
            if (players.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyRankingsView()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Podio visual para los primeros 3 lugares
                    if (players.isNotEmpty()) {
                        item {
                            PodiumView(
                                players = players,
                                criteria = selectedCriteria,
                                activePlayerId = activePlayerId
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    item {
                        Text(
                            text = "Tabla de Posiciones",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155),
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                        )
                    }

                    // Lista completa
                    itemsIndexed(players) { index, player ->
                        RankingPlayerCard(
                            rank = index + 1,
                            player = player,
                            criteria = selectedCriteria,
                            isActive = player.id == activePlayerId
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun RankingTabs(
    selected: RankingCriteria,
    onSelect: (RankingCriteria) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TabButton(
                label = "🏆 Puntos",
                isSelected = selected == RankingCriteria.SCORE,
                onClick = { onSelect(RankingCriteria.SCORE) },
                modifier = Modifier.weight(1f)
            )
            TabButton(
                label = "⭐ Estrellas",
                isSelected = selected == RankingCriteria.STARS,
                onClick = { onSelect(RankingCriteria.STARS) },
                modifier = Modifier.weight(1f)
            )
            TabButton(
                label = "⏱️ Tiempo",
                isSelected = selected == RankingCriteria.TIME,
                onClick = { onSelect(RankingCriteria.TIME) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TabButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) Color.White else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color(0xFF0284C7) else Color(0xFF64748B)
        )
    }
}

@Composable
private fun PodiumView(
    players: List<UserProfile>,
    criteria: RankingCriteria,
    activePlayerId: Long
) {
    val first = players.getOrNull(0)
    val second = players.getOrNull(1)
    val third = players.getOrNull(2)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎖️ Los Mejores del Salvador",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0284C7)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // Segundo Lugar (Plata)
                if (second != null) {
                    PodiumStep(
                        player = second,
                        rank = 2,
                        podiumHeight = 90.dp,
                        podiumColor = Color(0xFF94A3B8),
                        badge = "🥈",
                        criteria = criteria,
                        isActive = second.id == activePlayerId,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                // Primer Lugar (Oro - más alto)
                if (first != null) {
                    PodiumStep(
                        player = first,
                        rank = 1,
                        podiumHeight = 120.dp,
                        podiumColor = Color(0xFFF59E0B),
                        badge = "🥇",
                        criteria = criteria,
                        isActive = first.id == activePlayerId,
                        modifier = Modifier.weight(1.15f)
                    )
                }

                // Tercer Lugar (Bronce)
                if (third != null) {
                    PodiumStep(
                        player = third,
                        rank = 3,
                        podiumHeight = 70.dp,
                        podiumColor = Color(0xFFB45309),
                        badge = "🥉",
                        criteria = criteria,
                        isActive = third.id == activePlayerId,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun PodiumStep(
    player: UserProfile,
    rank: Int,
    podiumHeight: androidx.compose.ui.unit.Dp,
    podiumColor: Color,
    badge: String,
    criteria: RankingCriteria,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = badge, fontSize = 24.sp)

        // Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    if (isActive) Color(0xFFE0F2FE) else Color(0xFFF1F5F9),
                    CircleShape
                )
                .border(
                    width = if (isActive) 2.5.dp else 1.dp,
                    color = if (isActive) Color(0xFF0284C7) else podiumColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = player.avatarEmoji, fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = player.nickname,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color(0xFF1E293B)
        )

        Text(
            text = formatMetricValue(player, criteria),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = podiumColor
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Bloque del podio
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(podiumHeight)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(podiumColor, podiumColor.copy(alpha = 0.75f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$rank",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun RankingPlayerCard(
    rank: Int,
    player: UserProfile,
    criteria: RankingCriteria,
    isActive: Boolean
) {
    val rankBadge = when (rank) {
        1 -> "🥇"
        2 -> "🥈"
        3 -> "🥉"
        else -> "#$rank"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) Color(0xFFE0F2FE) else Color.White
        ),
        border = if (isActive) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF0284C7)) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isActive) 3.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Posición
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFF1F5F9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rankBadge,
                    fontSize = if (rank <= 3) 18.sp else 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xFFF8FAFC), CircleShape)
                    .border(1.dp, Color(0xFFE2E8F0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = player.avatarEmoji, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Nombre y detalles
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = player.nickname,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (isActive) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF0284C7), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Tú",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = "⭐ ${player.totalStars}",
                        fontSize = 12.sp,
                        color = Color(0xFFD97706)
                    )
                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = if (player.mapBestTimeSeconds > 0) "⏱️ ${player.mapBestTimeSeconds}s" else "⏱️ --",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Métrica destacada del criterio actual
            Box(
                modifier = Modifier
                    .background(
                        when (criteria) {
                            RankingCriteria.SCORE -> Color(0xFFFEF3C7)
                            RankingCriteria.STARS -> Color(0xFFFEF08A)
                            RankingCriteria.TIME -> Color(0xFFE0F2FE)
                        },
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = formatMetricValue(player, criteria),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = when (criteria) {
                        RankingCriteria.SCORE -> Color(0xFFB45309)
                        RankingCriteria.STARS -> Color(0xFF854D0E)
                        RankingCriteria.TIME -> Color(0xFF0369A1)
                    }
                )
            }
        }
    }
}

private fun formatMetricValue(player: UserProfile, criteria: RankingCriteria): String {
    return when (criteria) {
        RankingCriteria.SCORE -> "${player.mapBestScore} pts"
        RankingCriteria.STARS -> "${player.totalStars} ⭐"
        RankingCriteria.TIME -> {
            if (player.mapBestTimeSeconds > 0) {
                "${player.mapBestTimeSeconds}s"
            } else {
                "Sin récord"
            }
        }
    }
}

@Composable
private fun EmptyRankingsView() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TorogozAvatar(mood = MascotMood.HAPPY, modifier = Modifier.size(90.dp))
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "¡Aún no hay campeones!",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
        Text(
            text = "Juega el mapa de El Salvador o crea nuevos jugadores para comenzar a competir.",
            fontSize = 14.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}
