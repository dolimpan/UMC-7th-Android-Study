package com.example.flo
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerActivity : ComponentActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var timerTextView: TextView
    private lateinit var timerEndView : TextView
    private lateinit var artistTextView : TextView
    private lateinit var songTextView : TextView
    private lateinit var playButton: ImageView
    private lateinit var prevButton : ImageView
    private lateinit var nextButton : ImageView
    private lateinit var exitButton: ImageView
    private lateinit var albumCover : ImageView
    private lateinit var heart : ImageView
    private var timerJob: Job? = null
    private var paused : Boolean = true
    private lateinit var seekBar: SeekBar
    private var songDuration = 60
    private var currentProgress = 0
    private lateinit var dbSong : AppDatabase
    private var like = false
    private lateinit var song : String



    private val seekBarUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val currentPosition = intent?.getIntExtra("currentPosition", 0) ?: 0
            val duration = intent?.getIntExtra("duration", 0) ?: 0

            seekBar.max = duration
            seekBar.progress = currentPosition
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("SeekBarData", Context.MODE_PRIVATE)
        val artist = intent.getStringExtra("artist")
        song = intent.getStringExtra("song").toString()
        val songid = intent.getIntExtra("songid" ,-1)
        val mp3 = intent.getIntExtra("mp3", -1)
        val coverimg = intent.getIntExtra("cover", -1)

        Log.e("ㅇ","ㅁㅁㅁㅁ!ㅁㅁㅁㅁ ${song} ${artist} ${songid} ${mp3}")

        mediaPlayer = MediaPlayer.create(this, R.raw.lilac)
        setContentView(R.layout.player_activity)
        dbSong = Room.databaseBuilder(
            this, AppDatabase::class.java,
            "song_database"
        ).build()
        timerEndView = findViewById(R.id.endTime)
        albumCover = findViewById(R.id.player_pannel_albumcover_iv)
        exitButton = findViewById(R.id.player_pannel_btn_small_iv)
        timerTextView = findViewById(R.id.startTime)
        playButton = findViewById(R.id.play)
        seekBar = findViewById(R.id.seekBar)
        artistTextView = findViewById(R.id.player_pannel_artist_tv)
        songTextView = findViewById(R.id.player_pannel_title_tv)
        artistTextView.text = artist
        songTextView.text = song
        seekBar.max = songDuration
        heart = findViewById(R.id.player_pannel_heart_iv)
        prevButton = findViewById(R.id.prev)
        nextButton = findViewById(R.id.next)


        playButton.setImageResource(R.drawable.btn_miniplay_pause)
        paused = false


        val filter2 = IntentFilter("com.example.flo.ACTION_FROM_SERVICE")
        registerReceiver(musicReceiver, filter2)

        val filter = IntentFilter("com.example.flo.SEEK_BAR_UPDATE")
        registerReceiver(seekBarUpdateReceiver, filter)

        Log.d("PlayerActivity", "Starting service with: artist=$artist, song=$song, songid=$songid, mp3=$mp3")
        val intent = Intent(this, MusicService::class.java).apply {
            putExtra("artist",  artist)
            putExtra("song", song)
            putExtra("songid", songid)
            putExtra("mp3", mp3)
            putExtra("cover",coverimg)
            action = "START" // 액션 전달
        }
        startService(intent) // 서비스 시작

        val playIntent = Intent(this, MusicService::class.java).apply {
            action = "PLAY" // 액션 전달
        }
        startService(playIntent)



        playButton.setOnClickListener {
            if(paused)
            {
                val playIntent = Intent(this, MusicService::class.java).apply {
                    action = "PLAY" // 액션 전달
                }
                startService(playIntent)
                paused = false
                playButton.setImageResource(R.drawable.btn_miniplay_pause)
            }
            else
            {
                val pauseIntent = Intent(this, MusicService::class.java).apply {
                        action = "PAUSE"
                }
                    startService(pauseIntent)
                paused = true
                playButton.setImageResource(R.drawable.btn_miniplayer_play)
            }
        }

        exitButton.setOnClickListener {
            val intent = Intent()
            intent.putExtra("song", "$song")
            setResult(RESULT_OK, intent)
            finish()
        }

        nextButton.setOnClickListener {
            val nextIntent = Intent(this, MusicService::class.java).apply {
                action = "NEXT"
            }
            startService(nextIntent)
        }

        prevButton.setOnClickListener {
            val prevIntent = Intent(this, MusicService::class.java).apply {
                action = "PREV"
            }
            startService(prevIntent)
        }

        val searchIndexId = song // 검색할 텍스트 값
        CoroutineScope(Dispatchers.IO).launch {
            val songData = dbSong.SongTableDAO().getSongByIndexId(searchIndexId)
            if (songData != null) {
                Log.d("MainActivity", "Found Song: ${songData.title}, ${songData.singer}")
                withContext(Dispatchers.Main) {
                    albumCover.setImageResource(songData.coverImg)
                    Log.e("aa", "aa${songData.coverImg}")
                    songDuration = songData.second
                    seekBar.max = songDuration
                    val minutes = songDuration / 60
                    val seconds = songDuration % 60
                    timerEndView.text = String.format("%01d:%02d", minutes, seconds)
                    if(songData.liked == true)
                    {
                        heart.setImageResource(R.drawable.ic_my_like_on)
                        like = true
                    }
                }
            } else {
                Log.e("MainActivity", "No song found with indexId: $searchIndexId")
            }
        }

        heart.setOnClickListener {
            if(like)
            {
                Toast.makeText(this, "좋아요 취소", Toast.LENGTH_SHORT).show()
                toggleLikedStatus()
                heart.setImageResource(R.drawable.ic_my_like_off)
            }
            else
            {
                Toast.makeText(this, "좋아요", Toast.LENGTH_SHORT).show()
                toggleLikedStatus()
                heart.setImageResource(R.drawable.ic_my_like_on)
            }
        }

    }

    private fun toggleLikedStatus() {
        CoroutineScope(Dispatchers.IO).launch {
            val songData = dbSong.SongTableDAO().getSongByIndexId(song) // 기존 liked 상태 확인
            if (songData != null) {
                val newLikedStatus = !songData.liked // 기존 상태 반전
                dbSong.SongTableDAO().updateLikedStatus(song, newLikedStatus) // 상태 업데이트
                Log.d("MainActivity", "Updated liked status for songId $song to $newLikedStatus")
            } else {
                Log.e("MainActivity", "No song found with id: $song")
            }
        }
    }


    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val progress = intent.getIntExtra("current_progress", 0)
            updateSeekBar(progress)  // 매초 호출
        }
    }



    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            val songData = dbSong.SongTableDAO().getSongByIndexId(song)
            if (songData != null) {
                Log.d("Lifecycle", "부울른값:  ${songData.liked.toString()}")

                if (songData.liked == true) {
                    heart.setImageResource(R.drawable.ic_my_like_on)
                    like = true
                }
            }
        }
    }
    private fun updateSeekBar(progress: Int) {
        seekBar.progress = progress
        val minutes = progress / 60
        val seconds = progress % 60
        timerTextView.text = String.format("%01d:%02d", minutes, seconds)
    }


    private val musicReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val data1 = intent.getStringExtra("songName")
            val data2 = intent.getStringExtra("singer")
            val data3 = intent.getIntExtra("cover", -1)
            Log.e("전송받음", "${data1} ${data2} ${data3}")
            artistTextView.text = data2
            songTextView.text = data1

            Log.e("bb", "bb${data3}")
            albumCover.setImageResource(data3)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}