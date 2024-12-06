package com.example.flo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import androidx.room.Room
import kotlinx.coroutines.*

class BackgroundService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private var timerJob: Job? = null
    private var currentProgress = 0
    private val channelId = "PlayerServiceChannel"

    // DB 및 Queue 관련 변수
    private lateinit var db: AppDatabase
    private val playingQueue = mutableListOf<Int>() // 노래 인덱스 관리
    private var currentIndex = 0
    private var prevIndex = -1 // 이전 곡 인덱스

    override fun onCreate() {
        super.onCreate()
        // DB 초기화
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "song_database"
        ).build()

        startForegroundService()
    }

    private fun playCurrentSong() {
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }

        val currentSongId = playingQueue.getOrNull(currentIndex)
        if (currentSongId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val song = db.SongTableDAO().getSongByIndexId(currentSongId.toString())
                withContext(Dispatchers.Main) {
                    if (song != null) {
                        mediaPlayer = MediaPlayer.create(this@BackgroundService, song.coverImg)
                        mediaPlayer.isLooping = false
                        mediaPlayer.start()
                        Log.d("BackgroundService", "Now playing: ${song.title} by ${song.singer}")
                        startTimer() // Timer 시작
                    } else {
                        Log.e("BackgroundService", "Song not found for ID: $currentSongId")
                    }
                }
            }
        } else {
            Log.e("BackgroundService", "No song found at index: $currentIndex")
        }
    }

    private fun startTimer() {
        timerJob?.cancel() // 기존 타이머 중지
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
                    currentProgress = mediaPlayer.currentPosition / 1000
                    sendSeekBarUpdate() // SeekBar 업데이트 Broadcast 전송
                    delay(1000L)
                }
            }
        }
    }

    private fun sendSeekBarUpdate() {
        val intent = Intent("com.example.flo.UPDATE_SEEKBAR").apply {
            putExtra("current_progress", currentProgress)
            putExtra("song_duration", mediaPlayer.duration / 1000) // 전체 길이도 전송
        }
        sendBroadcast(intent)
        Log.d("BackgroundService", "Broadcast sent with progress: $currentProgress")
    }

    fun addQueue(songId: Int) {
        if (!playingQueue.contains(songId)) {
            playingQueue.add(songId)
            Log.d("BackgroundService", "Song with ID $songId added to queue.")
        } else {
            Log.e("BackgroundService", "Song with ID $songId is already in the queue.")
        }
    }

    fun logCurrentQueue() {
        if (playingQueue.isEmpty()) {
            Log.d("BackgroundService", "The queue is empty.")
        } else {
            Log.d("BackgroundService", "Current Queue:")
            CoroutineScope(Dispatchers.IO).launch {
                playingQueue.forEach { songId ->
                    val song = db.SongTableDAO().getSongByIndexId(songId.toString())
                    withContext(Dispatchers.Main) {
                        if (song != null) {
                            Log.d("BackgroundService", "ID: $songId, Title: ${song.title}, Singer: ${song.singer}")
                        } else {
                            Log.e("BackgroundService", "Song not found for ID: $songId")
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
        timerJob?.cancel()
    }
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId,
            "Player Service Channel",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }


    private fun startForegroundService() {
        createNotificationChannel()
        val notification = Notification.Builder(this, channelId)
            .setContentTitle("Music Player")
            .setContentText("Playing music...")
            .build()
        startForeground(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}


