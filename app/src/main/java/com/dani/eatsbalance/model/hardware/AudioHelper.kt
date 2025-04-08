package com.dani.eatsbalance.model.hardware


import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import java.io.File
import java.io.IOException
import java.util.Locale
import java.util.UUID

class AudioHelper(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var audioFilePath: String? = null
    private var tts: TextToSpeech? = null
    private var isRecording = false
    private var isPlaying = false
    private var ttsInitialized = false

    init {
        initTTS()
    }

    private fun initTTS() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "El idioma no está soportado")
                } else {
                    ttsInitialized = true
                }
            } else {
                Log.e("TTS", "Inicialización fallida")
            }
        }
    }

    fun startRecording(): Boolean {
        if (isRecording) return false

        // Crear un archivo para la grabación de audio
        val audioDir = File(context.filesDir, "audio")
        if (!audioDir.exists()) {
            audioDir.mkdir()
        }

        audioFilePath = "${audioDir.absolutePath}/meal_${UUID.randomUUID()}.3gp"

        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }

        try {
            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(audioFilePath)
                prepare()
                start()
                isRecording = true
            }
            return true
        } catch (e: IOException) {
            Log.e("AudioHelper", "Error al iniciar la grabación: ${e.message}")
            e.printStackTrace()
            releaseRecorder()
            return false
        }
    }

    fun stopRecording(): String? {
        if (!isRecording) return null

        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            return audioFilePath
        } catch (e: Exception) {
            Log.e("AudioHelper", "Error al detener la grabación: ${e.message}")
            e.printStackTrace()
            releaseRecorder()
            return null
        }
    }

    fun playAudio(filePath: String?): Boolean {
        if (isPlaying || filePath == null) return false

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(filePath)
                prepare()
                setOnCompletionListener {
                    releasePlayer()
                }
                start()
                isPlaying = true
            }
            return true
        } catch (e: IOException) {
            Log.e("AudioHelper", "Error al reproducir el audio: ${e.message}")
            e.printStackTrace()
            releasePlayer()
            return false
        }
    }

    fun stopPlayback() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
        isPlaying = false
    }

    fun speak(text: String) {
        if (!ttsInitialized) {
            Log.e("TTS", "TTS no inicializado")
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        } else {
            @Suppress("DEPRECATION")
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    fun shutdown() {
        releaseRecorder()
        releasePlayer()
        tts?.stop()
        tts?.shutdown()
    }

    private fun releaseRecorder() {
        try {
            mediaRecorder?.apply {
                if (isRecording) {
                    stop()
                }
                release()
            }
            mediaRecorder = null
            isRecording = false
        } catch (e: Exception) {
            Log.e("AudioHelper", "Error al liberar el grabador: ${e.message}")
        }
    }

    private fun releasePlayer() {
        try {
            mediaPlayer?.apply {
                if (isPlaying()) {
                    stop()
                }
                release()
            }
            mediaPlayer = null
            isPlaying = false
        } catch (e: Exception) {
            Log.e("AudioHelper", "Error al liberar el reproductor: ${e.message}")
        }
    }

    fun isRecording(): Boolean = isRecording

    fun isPlaying(): Boolean = isPlaying
}