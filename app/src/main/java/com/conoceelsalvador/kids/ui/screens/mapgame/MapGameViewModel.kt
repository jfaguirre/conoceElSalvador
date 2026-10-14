package com.conoceelsalvador.kids.ui.screens.mapgame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conoceelsalvador.kids.data.UserPreferencesRepository
import com.conoceelsalvador.kids.data.model.Department
import com.conoceelsalvador.kids.data.model.ElSalvadorData
import com.conoceelsalvador.kids.data.model.GameScore
import com.conoceelsalvador.kids.ui.components.MascotMood
import com.conoceelsalvador.kids.util.SoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.hypot

data class MapGameState(
    val discoveredDepartmentIds: Set<String> = emptySet(),
    val remainingDepartments: List<Department> = ElSalvadorData.departments.shuffled(),
    val currentCard: Department? = null,
    val timeSeconds: Int = 0,
    val mistakes: Int = 0,
    val isVictory: Boolean = false,
    val finalScore: GameScore? = null,
    val torogozMessage: String = "",
    val torogozMood: MascotMood = MascotMood.HAPPY,
    val highlightedDepartmentId: String? = null
)

class MapGameViewModel(
    private val repository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapGameState())
    val uiState: StateFlow<MapGameState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        startNewGame()
    }

    fun startNewGame() {
        timerJob?.cancel()
        val shuffled = ElSalvadorData.departments.shuffled()
        val firstCard = shuffled.firstOrNull()

        _uiState.value = MapGameState(
            discoveredDepartmentIds = emptySet(),
            remainingDepartments = shuffled,
            currentCard = firstCard,
            timeSeconds = 0,
            mistakes = 0,
            isVictory = false,
            finalScore = null,
            torogozMessage = "¡Hola! Arrastra la etiqueta de \"${firstCard?.name}\" a su lugar en el mapa.",
            torogozMood = MascotMood.HAPPY
        )

        startTimer()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (!_uiState.value.isVictory) {
                    _uiState.update { it.copy(timeSeconds = it.timeSeconds + 1) }
                }
            }
        }
    }

    /**
     * Valida si la etiqueta soltada en coordenadas relativas (0.0 a 1.0) corresponde al departamento
     */
    fun onCardDropped(droppedDepartment: Department, dropNormX: Float, dropNormY: Float) {
        val target = droppedDepartment
        val distance = hypot(dropNormX - target.normX, dropNormY - target.normY)
        // Tolerancia de distancia generosa (aproximadamente 22% del tablero) para facilidad de niños
        val isMatch = distance <= 0.22f

        if (isMatch) {
            handleSuccess(target)
        } else {
            handleMistake(droppedDepartment)
        }
    }

    private fun handleSuccess(department: Department) {
        SoundManager.playSuccess()

        val newDiscovered = _uiState.value.discoveredDepartmentIds + department.id
        val newRemaining = _uiState.value.remainingDepartments.filter { it.id != department.id }
        val nextCard = newRemaining.firstOrNull()

        val isVictory = newDiscovered.size >= ElSalvadorData.departments.size

        if (isVictory) {
            timerJob?.cancel()
            SoundManager.playVictory()
            val score = GameScore.calculate(
                timeSeconds = _uiState.value.timeSeconds,
                mistakes = _uiState.value.mistakes
            )

            // Guardar progreso en el repositorio
            repository.saveGameResult(
                starsEarned = score.stars,
                score = score.points,
                timeSeconds = score.timeSeconds
            )

            _uiState.update {
                it.copy(
                    discoveredDepartmentIds = newDiscovered,
                    remainingDepartments = emptyList(),
                    currentCard = null,
                    isVictory = true,
                    finalScore = score,
                    torogozMessage = "¡EXTRAORDINARIO! ¡Armaste todo El Salvador completo!",
                    torogozMood = MascotMood.VICTORY
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    discoveredDepartmentIds = newDiscovered,
                    remainingDepartments = newRemaining,
                    currentCard = nextCard,
                    torogozMessage = "¡Excelente! ${department.funFact}",
                    torogozMood = MascotMood.EXCITED
                )
            }
        }
    }

    private fun handleMistake(department: Department) {
        SoundManager.playMistake()

        _uiState.update {
            it.copy(
                mistakes = it.mistakes + 1,
                torogozMessage = "¡Casi! \"${department.name}\" va en otra parte del mapa. ¡Intenta de nuevo!",
                torogozMood = MascotMood.THINKING
            )
        }
    }

    fun skipCurrentCard() {
        val remaining = _uiState.value.remainingDepartments
        if (remaining.size > 1) {
            val current = _uiState.value.currentCard ?: return
            val rotated = remaining.filter { it.id != current.id } + current
            val newCurrent = rotated.first()
            _uiState.update {
                it.copy(
                    remainingDepartments = rotated,
                    currentCard = newCurrent,
                    torogozMessage = "Probemos ahora con \"${newCurrent.name}\". ¿Dónde estará en el mapa?",
                    torogozMood = MascotMood.HAPPY
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
