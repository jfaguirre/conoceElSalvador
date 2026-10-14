package com.conoceelsalvador.kids.ui.screens.welcome

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.conoceelsalvador.kids.data.model.DEFAULT_AVATARS
import com.conoceelsalvador.kids.data.model.UserProfile
import com.conoceelsalvador.kids.ui.components.MascotMood
import com.conoceelsalvador.kids.ui.components.TorogozAvatar

@Composable
fun WelcomeScreen(
    currentNickname: String = "",
    onContinue: (nickname: String, avatarId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var nicknameInput by remember { mutableStateOf(currentNickname) }
    var selectedAvatarId by remember { mutableStateOf("torogoz") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0284C7),
                        Color(0xFF38BDF8),
                        Color(0xFFF0FDF4)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Avatar amigable del Torogoz
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color(0xFFE0F2FE), CircleShape)
                        .border(3.dp, Color(0xFF0284C7), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    TorogozAvatar(
                        mood = MascotMood.HAPPY,
                        modifier = Modifier.size(80.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "¡Conoce El Salvador!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0369A1),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Aprende jugando con Torogocito",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de avatar
                Text(
                    text = "Elige tu avatar:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF475569)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(DEFAULT_AVATARS) { avatar ->
                        val isSelected = avatar.id == selectedAvatarId
                        Box(
                            modifier = Modifier
                                .size(44.dp)
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
                            Text(text = avatar.emoji, fontSize = 22.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "¿Cuál es tu nombre o apodo?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = nicknameInput,
                    onValueChange = { if (it.length <= 15) nicknameInput = it },
                    placeholder = { Text("Escribe tu nombre aquí...") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0284C7),
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val finalName = if (nicknameInput.isBlank()) "Explorador" else nicknameInput.trim()
                        onContinue(finalName, selectedAvatarId)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
                ) {
                    Text(
                        text = "🚀 ¡Comenzar la Aventura!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
