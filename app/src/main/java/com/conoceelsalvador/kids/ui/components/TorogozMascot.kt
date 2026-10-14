package com.conoceelsalvador.kids.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class MascotMood {
    HAPPY,
    EXCITED,
    THINKING,
    VICTORY
}

@Composable
fun TorogozMascot(
    message: String,
    modifier: Modifier = Modifier,
    mood: MascotMood = MascotMood.HAPPY
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Personaje animado del Torogoz
        TorogozAvatar(
            mood = mood,
            modifier = Modifier.size(76.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Burbuja de diálogo amigable
        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 2.dp,
                    color = Color(0xFF009688), // Color turquesa característico
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = message,
                fontSize = 13.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E293B)
            )
        }
    }
}

@Composable
fun TorogozAvatar(
    modifier: Modifier = Modifier,
    mood: MascotMood = MascotMood.HAPPY
) {
    val infiniteTransition = rememberInfiniteTransition(label = "TorogozAnim")

    // Movimiento pendular de la cola en forma de raqueta
    val tailSwing by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tailSwing"
    )

    // Suave respiración/rebote del cuerpo
    val bodyBounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bodyBounce"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Dibujar Cola con Plumas en Raqueta (con rotación oscilante)
        rotate(degrees = tailSwing, pivot = Offset(w * 0.35f, h * 0.65f)) {
            drawTorogozTail(w, h)
        }

        // 2. Dibujar Cuerpo del Torogoz
        val cy = (h * 0.45f) + bodyBounce

        // Espalda y alas (Verde oliva y canela)
        drawCircle(
            color = Color(0xFF2E7D32), // Verde esmeralda/oliva
            radius = w * 0.28f,
            center = Offset(w * 0.45f, cy)
        )

        // Pecho (Canela / rufo)
        drawCircle(
            color = Color(0xFFD87D36), // Tono rufo/naranja
            radius = w * 0.20f,
            center = Offset(w * 0.52f, cy + (h * 0.05f))
        )

        // Cabeza
        drawCircle(
            color = Color(0xFF1B5E20),
            radius = w * 0.22f,
            center = Offset(w * 0.52f, cy - (h * 0.16f))
        )

        // Corona / ceja turquesa brillante (Marca distintiva del Torogoz)
        drawRoundRect(
            color = Color(0xFF00E5FF), // Celeste turquesa eléctrico
            topLeft = Offset(w * 0.38f, cy - (h * 0.34f)),
            size = Size(w * 0.30f, h * 0.09f),
            cornerRadius = CornerRadius(8f, 8f)
        )

        // Antifaz negro del ojo
        drawRoundRect(
            color = Color(0xFF212121),
            topLeft = Offset(w * 0.45f, cy - (h * 0.22f)),
            size = Size(w * 0.32f, h * 0.09f),
            cornerRadius = CornerRadius(6f, 6f)
        )

        // Ojo grande amigable estilo caricatura
        val eyeCenter = Offset(w * 0.60f, cy - (h * 0.17f))
        drawCircle(
            color = Color.White,
            radius = w * 0.08f,
            center = eyeCenter
        )
        // Pupila brillante
        drawCircle(
            color = Color(0xFF1E293B),
            radius = w * 0.05f,
            center = Offset(w * 0.62f, cy - (h * 0.17f))
        )
        drawCircle(
            color = Color.White,
            radius = w * 0.02f,
            center = Offset(w * 0.63f, cy - (h * 0.19f))
        )

        // Pico largo ligeramente curvado
        val beakPath = Path().apply {
            moveTo(w * 0.70f, cy - (h * 0.20f))
            lineTo(w * 0.95f, cy - (h * 0.14f))
            lineTo(w * 0.70f, cy - (h * 0.09f))
            close()
        }
        drawPath(beakPath, color = Color(0xFF263238))

        // Ala con detalle azul turquesa
        val wingPath = Path().apply {
            moveTo(w * 0.25f, cy - (h * 0.05f))
            lineTo(w * 0.45f, cy + (h * 0.22f))
            lineTo(w * 0.20f, cy + (h * 0.20f))
            close()
        }
        drawPath(wingPath, color = Color(0xFF00B0FF))
    }
}

private fun DrawScope.drawTorogozTail(w: Float, h: Float) {
    val shaftColor = Color(0xFF37474F)
    val racquetColor = Color(0xFF00E5FF)

    // Pluma 1 (Eje delgado)
    drawLine(
        color = shaftColor,
        start = Offset(w * 0.35f, h * 0.60f),
        end = Offset(w * 0.22f, h * 0.88f),
        strokeWidth = 3f
    )
    // Raqueta 1 (Paleta turquesa al final)
    drawOval(
        color = racquetColor,
        topLeft = Offset(w * 0.16f, h * 0.84f),
        size = Size(w * 0.13f, h * 0.15f)
    )

    // Pluma 2 (Eje delgado)
    drawLine(
        color = shaftColor,
        start = Offset(w * 0.38f, h * 0.62f),
        end = Offset(w * 0.28f, h * 0.94f),
        strokeWidth = 3f
    )
    // Raqueta 2 (Paleta turquesa al final)
    drawOval(
        color = racquetColor,
        topLeft = Offset(w * 0.22f, h * 0.89f),
        size = Size(w * 0.13f, h * 0.15f)
    )
}
