package com.conoceelsalvador.kids.ui.screens.mapgame

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.conoceelsalvador.kids.data.model.Department
import com.conoceelsalvador.kids.data.model.ElSalvadorData
import kotlin.math.roundToInt

@Composable
fun MapBoardView(
    discoveredDepartmentIds: Set<String>,
    highlightedDepartmentId: String? = null,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.85f) // Proporción geométrica alargada de El Salvador
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFE0F2FE)) // Color suave simulando el Pacífico y cielo
            .border(2.dp, Color(0xFFBAE6FD), RoundedCornerShape(20.dp))
            .padding(6.dp)
    ) {
        val boardWidth = maxWidth
        val boardHeight = maxHeight

        // Fondo y silueta del Océano Pacífico al sur
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Línea de costa al sur
            val oceanPath = Path().apply {
                moveTo(0f, h * 0.88f)
                quadraticTo(w * 0.25f, h * 0.95f, w * 0.5f, h * 0.92f)
                quadraticTo(w * 0.75f, h * 0.98f, w, h * 0.90f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(oceanPath, color = Color(0xFF7DD3FC).copy(alpha = 0.45f))
        }

        // Renderizado de cada uno de los 14 departamentos
        ElSalvadorData.departments.forEach { department ->
            val isDiscovered = discoveredDepartmentIds.contains(department.id)
            val isHighlighted = department.id == highlightedDepartmentId

            DepartmentZone(
                department = department,
                isDiscovered = isDiscovered,
                isHighlighted = isHighlighted,
                boardWidth = boardWidth.value,
                boardHeight = boardHeight.value
            )
        }
    }
}

@Composable
private fun DepartmentZone(
    department: Department,
    isDiscovered: Boolean,
    isHighlighted: Boolean,
    boardWidth: Float,
    boardHeight: Float
) {
    val targetWidth = (boardWidth * department.normWidth).dp
    val targetHeight = (boardHeight * department.normHeight).dp
    val offsetX = (boardWidth * (department.normX - department.normWidth / 2f)).dp
    val offsetY = (boardHeight * (department.normY - department.normHeight / 2f)).dp

    val animatedColor by animateColorAsState(
        targetValue = when {
            isDiscovered -> department.color
            isHighlighted -> Color(0xFFFEF08A) // Amarillo brillante de guía
            else -> Color(0xFFF1F5F9).copy(alpha = 0.75f) // Silueta suave
        },
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "depColor_${department.id}"
    )

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = (offsetX.value * density).roundToInt(),
                    y = (offsetY.value * density).roundToInt()
                )
            }
            .size(targetWidth, targetHeight)
            .shadow(
                elevation = if (isDiscovered) 4.dp else 0.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = animatedColor,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = if (isHighlighted) 2.5.dp else 1.2.dp,
                color = when {
                    isDiscovered -> Color.White
                    isHighlighted -> Color(0xFFEAB308)
                    else -> Color(0xFF94A3B8)
                },
                shape = RoundedCornerShape(12.dp)
            )
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isDiscovered) {
            Text(
                text = department.name,
                fontSize = 10.sp,
                lineHeight = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        } else {
            // Icono sutil de incógnita para que el niño sepa dónde colocar
            Text(
                text = "?",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isHighlighted) Color(0xFFCA8A04) else Color(0xFF94A3B8),
                textAlign = TextAlign.Center
            )
        }
    }
}
