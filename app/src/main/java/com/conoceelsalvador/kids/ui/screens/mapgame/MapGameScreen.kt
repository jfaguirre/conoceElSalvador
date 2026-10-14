package com.conoceelsalvador.kids.ui.screens.mapgame

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.conoceelsalvador.kids.data.model.Department
import com.conoceelsalvador.kids.data.model.ElSalvadorData
import com.conoceelsalvador.kids.ui.components.TorogozMascot
import com.conoceelsalvador.kids.ui.components.VictoryDialog
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MapGameScreen(
    nickname: String,
    viewModel: MapGameViewModel,
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var mapBounds by remember { mutableStateOf<LayoutCoordinates?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MapGameTopBar(
                timeSeconds = state.timeSeconds,
                mistakes = state.mistakes,
                discoveredCount = state.discoveredDepartmentIds.size,
                totalCount = ElSalvadorData.departments.size,
                onBack = onBackToMenu
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Barra de progreso visual
                LinearProgressIndicator(
                    progress = { state.discoveredDepartmentIds.size.toFloat() / ElSalvadorData.departments.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF00C853),
                    trackColor = Color(0xFFE2E8F0)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Mascota Torogoz con mensaje interactivo
                TorogozMascot(
                    message = state.torogozMessage,
                    mood = state.torogozMood
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tablero del Mapa de El Salvador
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            mapBounds = coordinates
                        }
                ) {
                    MapBoardView(
                        discoveredDepartmentIds = state.discoveredDepartmentIds,
                        highlightedDepartmentId = state.highlightedDepartmentId
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Zona de arrastre de la tarjeta activa
                state.currentCard?.let { card ->
                    DraggableCardArea(
                        department = card,
                        mapBounds = mapBounds,
                        onDropped = { normX, normY ->
                            viewModel.onCardDropped(card, normX, normY)
                        },
                        onSkip = { viewModel.skipCurrentCard() }
                    )
                } ?: run {
                    if (!state.isVictory) {
                        Text(
                            text = "¡Cargando departamentos...!",
                            fontSize = 16.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            // Diálogo de victoria al ganar
            if (state.isVictory && state.finalScore != null) {
                VictoryDialog(
                    nickname = nickname.ifBlank { "Explorador" },
                    score = state.finalScore!!,
                    onPlayAgain = { viewModel.startNewGame() },
                    onBackToMenu = onBackToMenu
                )
            }
        }
    }
}

@Composable
private fun MapGameTopBar(
    timeSeconds: Int,
    mistakes: Int,
    discoveredCount: Int,
    totalCount: Int,
    onBack: () -> Unit
) {
    val minutes = timeSeconds / 60
    val seconds = timeSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .background(Color.White, CircleShape)
                .border(1.dp, Color(0xFFCBD5E1), CircleShape)
                .size(40.dp)
        ) {
            Text(text = "⬅️", fontSize = 16.sp)
        }

        // Indicador de tiempo
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "⏱️ ", fontSize = 14.sp)
                Text(
                    text = timeFormatted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF1E293B)
                )
            }
        }

        // Indicador de errores
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "❌ ", fontSize = 13.sp)
                Text(
                    text = "$mistakes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (mistakes > 0) Color(0xFFDC2626) else Color(0xFF16A34A)
                )
            }
        }

        // Progreso de departamentos
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2FE)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBAE6FD))
        ) {
            Text(
                text = "$discoveredCount / $totalCount",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF0369A1),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun DraggableCardArea(
    department: Department,
    mapBounds: LayoutCoordinates?,
    onDropped: (normX: Float, normY: Float) -> Unit,
    onSkip: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var dragOffset by remember(department.id) { mutableStateOf(Offset.Zero) }
    var cardRootPos by remember(department.id) { mutableStateOf(Offset.Zero) }
    var isDragging by remember(department.id) { mutableStateOf(false) }

    var cardSize by remember(department.id) { mutableStateOf(androidx.compose.ui.unit.IntSize.Zero) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "👇 Mantén presionado y arrastra al mapa 👇",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF64748B),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Box(
            modifier = Modifier
                .zIndex(if (isDragging) 100f else 1f)
                .offset {
                    IntOffset(
                        dragOffset.x.roundToInt(),
                        dragOffset.y.roundToInt()
                    )
                }
                .onGloballyPositioned { coordinates ->
                    if (!isDragging) {
                        cardRootPos = coordinates.positionInRoot()
                        cardSize = coordinates.size
                    }
                }
                .pointerInput(department.id) {
                    detectDragGestures(
                        onDragStart = {
                            isDragging = true
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            dragOffset += dragAmount
                        },
                        onDragEnd = {
                            isDragging = false
                            if (mapBounds != null && mapBounds.isAttached) {
                                val mapPos = mapBounds.positionInRoot()
                                val mapWidth = mapBounds.size.width.toFloat()
                                val mapHeight = mapBounds.size.height.toFloat()

                                // Centro exacto de la tarjeta arrastrada
                                val centerCardX = cardRootPos.x + dragOffset.x + (cardSize.width / 2f)
                                val centerCardY = cardRootPos.y + dragOffset.y + (cardSize.height / 2f)

                                val normX = (centerCardX - mapPos.x) / mapWidth
                                val normY = (centerCardY - mapPos.y) / mapHeight

                                onDropped(normX, normY)
                            }
                            // Reestablecer posición de la tarjeta
                            dragOffset = Offset.Zero
                        },
                        onDragCancel = {
                            isDragging = false
                            dragOffset = Offset.Zero
                        }
                    )
                }
        ) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = department.color),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDragging) 12.dp else 4.dp),
                modifier = Modifier
                    .scale(if (isDragging) 1.08f else 1.0f)
                    .shadow(
                        elevation = if (isDragging) 10.dp else 4.dp,
                        shape = RoundedCornerShape(18.dp)
                    )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "📍 ", fontSize = 20.sp)
                    Column {
                        Text(
                            text = department.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Cabecera: ${department.capital}",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Botón para saltar de tarjeta si el niño se siente confundido
        OutlinedButton(
            onClick = onSkip,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "⏭️ Probar con otro departamento",
                fontSize = 13.sp,
                color = Color(0xFF475569)
            )
        }
    }
}
