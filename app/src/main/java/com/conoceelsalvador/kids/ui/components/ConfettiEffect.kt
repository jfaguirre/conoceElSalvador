package com.conoceelsalvador.kids.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

private data class Particle(
    val startX: Float,
    val startY: Float,
    val speedY: Float,
    val speedX: Float,
    val size: Float,
    val color: Color
)

@Composable
fun ConfettiEffect(
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2500, easing = LinearEasing)
        )
    }

    val colors = listOf(
        Color(0xFFFFD700), // Oro
        Color(0xFF00B0FF), // Azul cielo
        Color(0xFF00E676), // Verde esmeralda
        Color(0xFFFF1744), // Rojo brillante
        Color(0xFFFF9100), // Naranja
        Color(0xFFE040FB)  // Magenta
    )

    val particles = remember {
        List(60) {
            Particle(
                startX = Random.nextFloat(),
                startY = -0.1f - Random.nextFloat() * 0.4f,
                speedY = 0.8f + Random.nextFloat() * 0.6f,
                speedX = (Random.nextFloat() - 0.5f) * 0.3f,
                size = 12f + Random.nextFloat() * 16f,
                color = colors[Random.nextInt(colors.size)]
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val t = progress.value

        particles.forEach { p ->
            val currX = (p.startX + p.speedX * t).coerceIn(0f, 1f) * w
            val currY = (p.startY + p.speedY * t) * h
            if (currY in 0f..h) {
                drawRect(
                    color = p.color,
                    topLeft = Offset(currX, currY),
                    size = Size(p.size, p.size * 0.7f)
                )
            }
        }
    }
}
