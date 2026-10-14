package com.conoceelsalvador.kids.ui.screens.players

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.conoceelsalvador.kids.data.UserPreferencesRepository
import com.conoceelsalvador.kids.data.model.DEFAULT_AVATARS
import com.conoceelsalvador.kids.data.model.UserProfile

@Composable
fun PlayerManagementScreen(
    repository: UserPreferencesRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val players by repository.allPlayers.collectAsState()
    val activePlayer by repository.activePlayer.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var playerToDelete by remember { mutableStateOf<UserProfile?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF1F5F9)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Barra superior
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

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "👥 Jugadores",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Crea, cambia o elimina exploradores",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                }

                // Botón + Agregar
                Button(
                    onClick = { showCreateDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                ) {
                    Text(text = "+ Nuevo", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            // Lista de jugadores
            if (players.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🤷‍♂️", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No hay jugadores registrados",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { showCreateDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(text = "➕ Crear Mi Primer Jugador", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Jugadores Guardados (${players.size}):",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF475569)
                        )
                    }

                    items(players, key = { it.id }) { player ->
                        val isActive = player.id == activePlayer.id
                        PlayerCard(
                            player = player,
                            isActive = isActive,
                            onSelect = { repository.selectPlayer(player.id) },
                            onDelete = { playerToDelete = player }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }

    // Diálogo para Crear Nuevo Jugador
    if (showCreateDialog) {
        CreatePlayerDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { nickname, avatarId ->
                repository.createPlayer(nickname, avatarId)
                showCreateDialog = false
            }
        )
    }

    // Diálogo para Confirmar Eliminación
    playerToDelete?.let { player ->
        AlertDialog(
            onDismissRequest = { playerToDelete = null },
            title = {
                Text(
                    text = "¿Eliminar a ${player.nickname}?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "Se borrarán sus ${player.totalStars} estrellas y récords en este dispositivo. Esta acción no se puede deshacer.",
                    fontSize = 14.sp,
                    color = Color(0xFF475569)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.deletePlayer(player.id)
                        playerToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Eliminar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { playerToDelete = null }) {
                    Text(text = "Cancelar", color = Color(0xFF64748B))
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}

@Composable
private fun PlayerCard(
    player: UserProfile,
    isActive: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) Color(0xFFE0F2FE) else Color.White
        ),
        border = if (isActive) {
            androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF0284C7))
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
        },
        elevation = CardDefaults.cardElevation(defaultElevation = if (isActive) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(
                        if (isActive) Color(0xFFBAE6FD) else Color(0xFFF1F5F9),
                        CircleShape
                    )
                    .border(
                        width = if (isActive) 2.dp else 1.dp,
                        color = if (isActive) Color(0xFF0284C7) else Color(0xFFCBD5E1),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = player.avatarEmoji, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Información del jugador
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = player.nickname,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (isActive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF0284C7), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Activo",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "⭐ ${player.totalStars} estrellas",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFD97706)
                    )
                    Text(
                        text = "🏆 ${player.mapBestScore} pts",
                        fontSize = 12.sp,
                        color = Color(0xFF0369A1)
                    )
                    if (player.mapBestTimeSeconds > 0) {
                        Text(
                            text = "⏱️ ${player.mapBestTimeSeconds}s",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            // Botón Seleccionar (si no está activo)
            if (!isActive) {
                OutlinedButton(
                    onClick = onSelect,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(text = "Elegir", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(6.dp))
            }

            // Botón Eliminar
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .background(Color(0xFFFEE2E2), CircleShape)
                    .size(36.dp)
            ) {
                Text(text = "🗑️", fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun CreatePlayerDialog(
    onDismiss: () -> Unit,
    onConfirm: (nickname: String, avatarId: String) -> Unit
) {
    var nicknameInput by remember { mutableStateOf("") }
    var selectedAvatarId by remember { mutableStateOf("torogoz") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "✨ Nuevo Explorador",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = Color(0xFF0F172A)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Elige tu avatar:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF475569)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Selector horizontal de avatares
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(DEFAULT_AVATARS) { avatar ->
                        val isSelected = avatar.id == selectedAvatarId
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color(0xFFE0F2FE) else Color(0xFFF1F5F9))
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF0284C7) else Color(0xFFCBD5E1),
                                    shape = CircleShape
                                )
                                .clickable { selectedAvatarId = avatar.id },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = avatar.emoji, fontSize = 24.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Nombre o Apodo:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF475569)
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = nicknameInput,
                    onValueChange = { if (it.length <= 15) nicknameInput = it },
                    placeholder = { Text("Ej. Mateo, Sofi, Papá...") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0284C7),
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalName = if (nicknameInput.isBlank()) "Explorador" else nicknameInput.trim()
                    onConfirm(finalName, selectedAvatarId)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "¡Crear y Jugar!", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancelar", color = Color(0xFF64748B))
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}
