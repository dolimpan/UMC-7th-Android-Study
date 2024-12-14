package com.example.flo
import ViewPagerAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.fragment.app.Fragment
import com.example.flo.databinding.MainActivityBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.commit
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.room.Room
import com.example.flo.PlayerActivity
import org.json.JSONArray
import org.json.JSONObject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Job

class MainActivity : AppCompatActivity() {

    lateinit var binding: MainActivityBinding
    private lateinit var artist: TextView
    private lateinit var song: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    private var monitoringJob: Job? = null
    private lateinit var seekBar: SeekBar
    private lateinit var db: AppDatabase // RoomDatabase 인스턴스


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewPager = binding.viewPager
        val bottomNavigationView = binding.bottomNavigationView
        sharedPreferences = getSharedPreferences("SeekBarData", Context.MODE_PRIVATE)
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "song_database" // 데이터베이스 이름
        ).build()
        seekBar = findViewById(R.id.seekBar)
        registerReceiver(updateReceiver, IntentFilter("com.example.flo.UPDATE_SEEKBAR"))
        viewPager.adapter = ViewPagerAdapter(this)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> viewPager.currentItem = 0
                R.id.nav_discover -> viewPager.currentItem = 1
                R.id.nav_search -> viewPager.currentItem = 2
                R.id.nav_profile -> viewPager.currentItem = 3
            }
            true
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNavigationView.menu.getItem(position).isChecked = true
            }
        })

        artist = findViewById(R.id.artistName)
        song = findViewById(R.id.songTitle)

        val AFR = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val resultValue = data?.getStringExtra("song")
                Toast.makeText(this, "$resultValue", Toast.LENGTH_SHORT).show()
            }
        }

        binding.miniPlayer.setOnClickListener {
            val text1 = artist.text.toString()
            val text2 = song.text.toString()

            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("artist", text1)
                putExtra("song", text2)
            }
            AFR.launch(intent)
        }

        sharedPreferences = getSharedPreferences("AlbumData", Context.MODE_PRIVATE)
        saveAlbumData("1", "아이유 앨범", "아이유", listOf("아이유노래1","아이유노래2"))
        saveAlbumData("2", "방탄 앨범", "방탄소년단", listOf("방탄노래1","방탄노래2"))
        saveAlbumData("3", "랜덤 앨범", "Various Artist", listOf("그냥노래1","그냥노래2"))
    }

    private fun saveAlbumData(albumId : String, albumName: String, albumArtist: String, albumSongs: List<String>) {
        val editor = sharedPreferences.edit()
        val songsJson = gson.toJson(albumSongs)
        editor.putString("album_id", albumId)
        editor.putString("album_name", albumName)
        editor.putString("album_artist", albumArtist)
        editor.putString("album_songs", songsJson)
        editor.apply() // 저장
    }


    private fun loadAlbumData(): Triple<String, String, List<String>>? {
        val albumName = sharedPreferences.getString("album_name", null)
        val albumArtist = sharedPreferences.getString("album_artist", null)
        val songsJson = sharedPreferences.getString("album_songs", null)

        if (albumName != null && albumArtist != null && songsJson != null) {
            val songsType = object : TypeToken<List<String>>() {}.type
            val albumSongs: List<String> = gson.fromJson(songsJson, songsType)
            return Triple(albumName, albumArtist, albumSongs)
        }
        return null
    }
    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val progress = intent.getIntExtra("current_progress", 0)
            updateSeekBar(progress)  // 매초 호출
        }
    }

    private fun updateSeekBar(progress: Int) {
        seekBar.progress = progress
    }
}
