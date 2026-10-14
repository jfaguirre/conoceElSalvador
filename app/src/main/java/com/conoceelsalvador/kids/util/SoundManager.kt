package com.conoceelsalvador.kids.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

object SoundManager {
    private val scope = CoroutineScope(Dispatchers.Default)

    /**
     * Reproduce un sonido de éxito (campana brillante ascendente)
     */
    fun playSuccess() {
        scope.launch {
            playToneSequence(
                listOf(
                    Pair(523.25f, 100), // Do5
                    Pair(659.25f, 100), // Mi5
                    Pair(783.99f, 180)  // Sol5
                )
            )
        }
    }

    /**
     * Reproduce un sonido de error suave (sin desmotivar al niño)
     */
    fun playMistake() {
        scope.launch {
            playToneSequence(
                listOf(
                    Pair(260.0f, 120),
                    Pair(210.0f, 180)
                )
            )
        }
    }

    /**
     * Reproduce una fanfarria alegre de victoria al completar el mapa
     */
    fun playVictory() {
        scope.launch {
            playToneSequence(
                listOf(
                    Pair(523.25f, 120), // Do5
                    Pair(659.25f, 120), // Mi5
                    Pair(783.99f, 120), // Sol5
                    Pair(1046.50f, 300) // Do6 final
                )
            )
        }
    }

    /**
     * Sintetizador de tonos puros de audio senoidales con fade suave
     */
    private fun playToneSequence(notes: List<Pair<Float, Int>>) {
        try {
            val sampleRate = 44100
            for ((freq, durationMs) in notes) {
                val numSamples = (durationMs * sampleRate) / 1000
                val samples = ShortArray(numSamples)
                val angularFreq = 2.0 * PI * freq / sampleRate

                for (i in 0 until numSamples) {
                    // Envolvente de volumen (fade in y fade out para evitar chasquidos)
                    val attack = (numSamples * 0.1f).toInt().coerceAtLeast(1)
                    val decay = (numSamples * 0.2f).toInt().coerceAtLeast(1)
                    val envelope = when {
                        i < attack -> i.toFloat() / attack
                        i > numSamples - decay -> (numSamples - i).toFloat() / decay
                        else -> 1.0f
                    }
                    val sampleValue = (sin(angularFreq * i) * 16000 * envelope).toInt()
                    samples[i] = sampleValue.toShort()
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(samples.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(samples, 0, samples.size)
                audioTrack.play()
                Thread.sleep(durationMs.toLong() + 20)
                audioTrack.release()
            }
        } catch (e: Exception) {
            Log.e("SoundManager", "Error al reproducir audio: ${e.message}")
        }
    }
}
