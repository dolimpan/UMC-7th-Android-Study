package com.example.flo
import android.content.Context
import android.media.Image
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

class RealAlbumFragment : Fragment() {
    private lateinit var dbSong : AppDatabase
    private lateinit var db : AppDatabase
    private lateinit var dbAlbum : AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.album_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbSong = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "song_database"
        ).build()

        db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "album_database"
        ).build()

        dbAlbum = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "liked_album"
        ).build()


        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val albumImage = view.findViewById<ImageView>(R.id.album_pannel_album_iv)
        val albumTitle = view.findViewById<TextView>(R.id.album_pannel_title_tv)
        val albumSinger = view.findViewById<TextView>(R.id.album_pannel_artist_tv)
        val albumLiked = view.findViewById<ImageView>(R.id.album_pannel_heart_iv)
        var liked = false
        val sharedPref = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = sharedPref.getString("jwt_token", null)
        val extractedUserId = token?.let { JwtUtils.getUserIdFromToken(it) }

        // 전달받은 title 값
        val title = arguments?.getString("title")
        val Title = title.toString()
        var idx by Delegates.notNull<Int>()
        Log.d("RealAlbumFragment", "Received title: $title")





        CoroutineScope(Dispatchers.IO).launch {
            idx = db.AlbumTableDAO().getIdByTitle(Title)!!
            if (title != null) {
                val songs = dbSong.SongTableDAO().getSongsByAlbumIdx(title)

                withContext(Dispatchers.Main) {
                    albumImage.setImageResource(songs[0].coverImg)
                    albumTitle.text = songs[0].albumIdx
                    albumSinger.text = songs[0].singer
                }

                if (idx != null) {
                    if(dbAlbum.LikedAlbumDAO().isAlbumLiked(idx.toInt(), extractedUserId.toString()))
                        {
                            albumLiked.setImageResource(R.drawable.ic_my_like_on)
                            liked = true
                        }


                }
                val sampleDataList = songs.map { song ->
                    SampleData(song.title, song.singer, song.coverImg, song.id, song.mp3)
                }

                // MainThread에서 RecyclerView 업데이트
                withContext(Dispatchers.Main) {
                    recyclerView.layoutManager =
                        androidx.recyclerview.widget.LinearLayoutManager(requireContext())
                    recyclerView.adapter = RealAlbumAdapter(sampleDataList)
                }

            } else {
                Log.e("RealAlbumFragment", "No albumIdx received.")
            }
        }

        albumLiked.setOnClickListener{
            print()
            if(liked)
            {
                delete(idx, extractedUserId.toString())
                albumLiked.setImageResource(R.drawable.ic_my_like_off)
            }
            else
            {
                insert(idx, extractedUserId.toString())
                albumLiked.setImageResource(R.drawable.ic_my_like_on)
                liked = true
            }
        }


        }
    private fun delete(idx : Int, user : String)
    {
        CoroutineScope(Dispatchers.IO).launch {
            dbAlbum.LikedAlbumDAO().deleteLikedAlbum((idx.toInt()), user.toString())
        }
    }

    private fun insert(idx : Int, user : String)
    {
        CoroutineScope(Dispatchers.IO).launch {
            dbAlbum.LikedAlbumDAO().insertLikedAlbum(LikedAlbum(id = idx, userId = user))
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
}

