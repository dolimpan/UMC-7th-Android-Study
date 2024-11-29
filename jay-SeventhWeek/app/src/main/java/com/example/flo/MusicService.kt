package com.example.flo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*
import android.content.SharedPreferences

class MusicService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private val queue = mutableListOf<SongData>() // 재생 대기열
    private var currentIndex = 0
    private val channelId = "MusicPlayerChannel"
    private var currentArtist: String? = null
    private var currentSong: String? = null
    private var currentAlbumImg: Int = -1
    private var currentSongId: Int = -1
    private var currentMp3: Int = -1


    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        //restoreCurrentIndex()
        startForeground(1, createNotification("Music Player", "No song playing"))

        CoroutineScope(Dispatchers.Main + Job()).launch {
            while (true) {
                delay(1000) // 1초마다 업데이트
                if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
                    val currentPosition = mediaPlayer.currentPosition
                    val duration = mediaPlayer.duration

                    // Broadcast로 SeekBar 데이터를 전달
                    val intent = Intent("com.example.flo.SEEK_BAR_UPDATE")
                    intent.putExtra("currentPosition", currentPosition)
                    intent.putExtra("duration", duration)
                    sendBroadcast(intent)
                }
            }

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        stopMusic()
        Log.d("MusicService", "Service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            Log.e("MusicService", "Received null intent.")
            return START_NOT_STICKY
        }


        val action = intent.action
        if (action == "START") {
            initial(intent)
        } else if (action == "PLAY") {
            playCurrentSong()
        } else if (action == "PAUSE") {
            stopMusic()
        } else if (action == "NEXT") {
            playNextSong()
        } else if (action == "PREV") {
            playPreviousSong()
        }

        return START_STICKY
    }

    private fun initial(intent: Intent) {
        intent.let {
            currentArtist = it.getStringExtra("artist")
            currentSong = it.getStringExtra("song")
            currentSongId = it.getIntExtra("songid", -1)
            currentMp3 = it.getIntExtra("mp3", -1)
            currentAlbumImg = it.getIntExtra("cover", -1)
            val playData =
                SongData(currentSongId, currentArtist, currentSong, currentMp3, currentAlbumImg)
            Log.d(
                "MusicService",
                "Updated data: artist=$currentArtist, song=$currentSong, songid=$currentSongId, mp3=$currentMp3"
            )
            addToQueue(playData)
        }
    }

    private fun addToQueue(songData: SongData) {
        queue.add(songData)
        currentIndex = queue.size -1
        Log.d(
            "MusicService",
            "Song added to queue: ${songData.title} by ${songData.artist} ${songData.id} ${songData.mp3ResId}"
        )
        playCurrentSong()
    }

    private fun playCurrentSong() {
        if (queue.isEmpty() || currentIndex >= queue.size) {
            Log.e("MusicService", "No song to play")

            return
        }
        stopMusic()
        //restorestoreCurrentreCurrentIndex()
        Log.e("제발2", "현재 재생중인 index는 ${currentIndex}")
        val currentSong = queue[currentIndex]
        Log.e("제발", "$currentSong")

        val intent = Intent("com.example.flo.ACTION_FROM_SERVICE")
        intent.putExtra("songName", currentSong.title)
        intent.putExtra("singer", currentSong.artist)
        intent.putExtra("cover", currentSong.coverImg)

        sendBroadcast(intent)

        mediaPlayer = MediaPlayer.create(this, currentSong.mp3ResId)
        mediaPlayer.isLooping = false
        mediaPlayer.start()

        // Start foreground mode with updated notification
        val notification =
            createNotification("Playing", "${currentSong.title} by ${currentSong.artist}")
        startForeground(1, notification)

        mediaPlayer.setOnCompletionListener {
            playNextSong()
        }
    }

    private fun stopMusic() {
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    private fun pauseMusic() {
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.pause()

            // Update notification and stop foreground mode
            val notification = createNotification(
                "Paused",
                "${queue[currentIndex].title} by ${queue[currentIndex].artist}"
            )
            stopForeground(false)
            val manager = getSystemService(NotificationManager::class.java)
            manager.notify(1, notification)
        }
    }


    private fun playNextSong() {
        if (currentIndex < queue.size - 1) {
            currentIndex++
            //saveCurrentIndex()
            playCurrentSong()
        } else {
            Log.d("MusicService", "Reached end of queue")
        }
    }

    private fun playPreviousSong() {
        if (currentIndex > 0) {
            currentIndex--
            //saveCurrentIndex()
            playCurrentSong()
        } else {
            Log.d("MusicService", "Reached start of queue")
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId, "Music Player",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun createNotification(title: String, content: String): Notification {
        return Notification.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.btn_actionbar_instagram) // 유효한 small icon 리소스 추가
            .build()
    }

    private fun updateNotification(title: String, content: String) {
        val notification = createNotification(title, content)
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(1, notification)
    }


    private fun saveCurrentIndex() {
        val sharedPreferences = getSharedPreferences("MusicServicePrefs", MODE_PRIVATE)

        sharedPreferences.edit()
            .putInt("currentIndex", currentIndex)
            .apply()
        Log.d("MusicService", "Saved currentIndex: $currentIndex")
    }

    private fun restoreCurrentIndex() {
        val sharedPreferences = getSharedPreferences("MusicServicePrefs", MODE_PRIVATE)
        currentIndex = sharedPreferences.getInt("currentIndex", 0)
        Log.d("MusicService", "Restored currentIndex: $currentIndex")
    }

}
data class SongData(
    val id: Int,
    val artist: String?,
    val title: String?,
    val mp3ResId: Int,
    val coverImg : Int
)
