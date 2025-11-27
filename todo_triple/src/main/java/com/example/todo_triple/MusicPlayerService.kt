package com.example.todo_triple

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.provider.OpenableColumns
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// Data class to hold track information
data class TrackInfo(val fileName: String)

class MusicPlayerService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val binder = MusicBinder()

    // StateFlow to communicate state to the UI
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentTrack = MutableStateFlow<TrackInfo?>(null)
    val currentTrack = _currentTrack.asStateFlow()

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "MusicPlayerServiceChannel"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    // Binder class for clients to access the service
    inner class MusicBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    @SuppressLint("ForegroundServiceType")
    fun play(context: Context, uri: Uri) {
        // Release previous player
        mediaPlayer?.release()

        // Create and prepare new player
        mediaPlayer = MediaPlayer.create(context, uri).apply {
            setOnCompletionListener {
                stopPlayback()
            }
        }
        mediaPlayer?.start()

        // Update state
        _isPlaying.value = true
        _currentTrack.value = TrackInfo(fileName = uri.getFileName(context) ?: "Unknown File")

        // Start as a foreground service
        startForeground(NOTIFICATION_ID, createNotification())
    }

    fun pause() {
        mediaPlayer?.takeIf { it.isPlaying }?.pause()
        _isPlaying.value = false
    }

    fun resume() {
        mediaPlayer?.start()
        _isPlaying.value = true
    }

    fun stopPlayback() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        _isPlaying.value = false
        _currentTrack.value = null
        stopForeground(true)
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Music Player Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music Player")
            .setContentText("Playing: ${_currentTrack.value?.fileName ?: "No track"}")
            .setSmallIcon(R.drawable.ic_play) // Replace with your app's icon
            .build()
    }
}

// Extension function to get file name from Uri
fun Uri.getFileName(context: Context): String? {
    // This is a common way to get file name from a content Uri
    return context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        cursor.getString(nameIndex)
    }
}
