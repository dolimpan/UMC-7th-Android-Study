package com.example.flo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.ui.theme.HomeFragmentAdapter2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.relex.circleindicator.CircleIndicator3

class HomeFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0
    private lateinit var db: AppDatabase // RoomDatabase 인스턴스
    private lateinit var dbSong : AppDatabase
    private lateinit var dbAlbum : AppDatabase
    private lateinit var idText : TextView
    private lateinit var extractedUserId : String
    private lateinit var sharedPref : SharedPreferences
    private lateinit var token : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    private fun startAutoSlide() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (viewPager.adapter != null) {
                    currentPage = (currentPage + 1) % 2 // Page1 <-> Page2 (0, 1)
                    viewPager.setCurrentItem(currentPage, true)
                }
                handler.postDelayed(this, 5000) // 5초마다 호출
            }
        }, 5000)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "album_database"
        ).build()

        sharedPref = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        token = sharedPref.getString("jwt_token", null).toString()

        extractedUserId = token?.let { JwtUtils.getUserIdFromToken(it)}.toString()


        dbAlbum = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "liked_album"
        ).build()


        dbSong = Room.databaseBuilder(
                requireContext(),
            AppDatabase::class.java,
            "song_database"
        ).build()

        addAlbum("LILAC", "아이유",R.drawable.img_album_exp2)
        addAlbum("SUNRISE", "데이식스",R.drawable.day6_cover)
        addSong("Lilac", "아이유",214,false,R.drawable.img_album_exp2,false,"LILAC",R.raw.lilac)
        addSong("Coin", "아이유",192,false,R.drawable.img_album_exp2,false,"LILAC",R.raw.coin)
        addSong("놓아 놓아 놓아", "데이식스",230,false,R.drawable.day6_cover,false,"SUNRISE",R.raw.letgo)
        addSong("예뻤어", "데이식스",284,false,R.drawable.day6_cover,false,"SUNRISE",R.raw.pretty)
        Thread.sleep(1000) // 1초 딜레이

        loadAlbums()
        viewPager = view.findViewById(R.id.viewPager)
        val indicator = view.findViewById<CircleIndicator3>(R.id.circleIndicator)
        viewPager.adapter = HomeFragmentAdapter2(this)
        indicator.setViewPager(viewPager)
        startAutoSlide()
        val loginButton = view.findViewById<Button>(R.id.login)
        val registerButton = view.findViewById<Button>(R.id.register)
        val debugButton = view.findViewById<Button>(R.id.logut)
        idText = view.findViewById(R.id.display)
        reloadName(extractedUserId)
            loginButton.setOnClickListener()
        {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

        registerButton.setOnClickListener()
        {
            val intent = Intent(requireContext(), RegisterActivity::class.java)
            startActivity(intent)
        }

        debugButton.setOnClickListener()
        {
            Log.e("!", "!")
                val sharedPref = context?.getSharedPreferences("auth", Context.MODE_PRIVATE)
                with(sharedPref?.edit()) {
                    this?.remove("jwt_token") // 저장된 JWT 토큰 삭제
                    this?.apply() // 변경 사항 저장
                }
            token = sharedPref?.getString("jwt_token", null).toString() // 최신 값을 다시 가져오기
            extractedUserId = token?.let { JwtUtils.getUserIdFromToken(it)}.toString()
            reloadName(extractedUserId)
        }

    }

    private fun reloadName(string : String)
    {
        idText.text = string
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null) // 핸들러 제거
    }

    override fun onResume() {
        super.onResume()
        token = sharedPref?.getString("jwt_token", null).toString() // 최신 값을 다시 가져오기
        extractedUserId = token?.let { JwtUtils.getUserIdFromToken(it)}.toString()
        reloadName(extractedUserId)
    }

    fun navigateToRealAlbumFragment(title: String) {
        val fragment = RealAlbumFragment()
        val args = Bundle().apply {
            putString("title", title) // title 값 전달
        }
        fragment.arguments = args

        parentFragmentManager.beginTransaction()
            .add(R.id.frame3, fragment)
            .addToBackStack(null)
            .commit()
    }


    private fun addAlbum(title: String, singer: String, coverImg :Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val existingAlbum = db.AlbumTableDAO().Check(title, singer)
            if (existingAlbum == null) {
                db.AlbumTableDAO().insertAlbum(AlbumTable(title = title, coverImg = coverImg, singer = singer))
                Log.d("HomeFragment", "Album inserted: $title")
            } else {
                Log.d("HomeFragment", "Duplicate album: $title by $singer not inserted.")
            }
        }
    }



    private fun addSong(title:String, singer:String, second:Int, isPlaying:Boolean, coverImg: Int, liked:Boolean, albumIdx :String, mp3 : Int)
    {
        CoroutineScope(Dispatchers.IO).launch {
            val existingAlbum = dbSong.SongTableDAO().Check(title, singer)
            if (existingAlbum == null) {
                dbSong.SongTableDAO().insertSong(SongTable(title = title, singer = singer, second = second, isPlaying = isPlaying, coverImg = coverImg, liked = liked, albumIdx = albumIdx, mp3 = mp3 ))
                Log.d("HomeFragment", "Song inserted: $title")
            } else {
                Log.d("HomeFragment", "Duplicate Song: $title by $singer not inserted.")
            }
        }
    }

    private fun print()
    {
        CoroutineScope(Dispatchers.IO).launch {
            val allLikedAlbums = dbAlbum.LikedAlbumDAO().getAllLikedAlbums()
            for (album in allLikedAlbums) {
                Log.d("LikedAlbum", "ID: ${album.id}, UserID: ${album.userId}")
            }}
    }



    private fun loadAlbums() {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView)
        CoroutineScope(Dispatchers.IO).launch {
            val albums = db.AlbumTableDAO().getAllAlbums()
            albums.forEach { album ->
                Log.d("HomeFragment", "Loaded Album: ${album.title}, ${album.singer}, ${album.coverImg}")
            }
            val items = albums.map { album ->
                SampleData(album.title, album.singer, album.coverImg, -1,-1)
            }
            withContext(Dispatchers.Main) {
                recyclerView?.apply {
                    layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    adapter = HomeFragmentAdapter(items) { title ->
                        navigateToRealAlbumFragment(title.toString()) // 클릭된 title을 전달
                    }
                }
            }
        }
    }


}
