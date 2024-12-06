package com.example.flo

import ProfileDataAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragmentRecycle : Fragment() {
    private lateinit var dbSong: AppDatabase
    private val items = mutableListOf<ProfileData>() // 데이터 리스트
    private var selected = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.store_fragment, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val clickAll = view.findViewById<TextView>(R.id.selectall_tv)
        val delete = view.findViewById<TextView>(R.id.remove_tv)
        delete.visibility = View.GONE
        dbSong = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "song_database"
        ).build()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ProfileDataAdapter(items)

        // 초기 데이터 로드
        fetchLikedSongsAndUpdateUI(recyclerView, items)
        clickAll.setOnClickListener {
            val adapter = recyclerView.adapter as? ProfileDataAdapter
            if(!selected)
            {
                delete.visibility = View.VISIBLE // 숨기기
                adapter?.selectAllItems() // 모든 항목 선택
                selected = true;

            }
            else
            {
                delete.visibility = View.GONE
                adapter?.deselectAllItems() // 모든 항목 선택
                selected = false;
            }
        }
        delete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                dbSong.SongTableDAO().resetLikedSongs()
                fetchLikedSongsAndUpdateUI(recyclerView, items)
            }
            delete.visibility = View.GONE
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView)
        if (recyclerView != null) {
            fetchLikedSongsAndUpdateUI(recyclerView, items) // 데이터 업데이트
        }
    }

    private fun fetchLikedSongsAndUpdateUI(recyclerView: RecyclerView, items: MutableList<ProfileData>) {
        CoroutineScope(Dispatchers.IO).launch {
            val likedSongs = dbSong.SongTableDAO().getLikedSongs()

            // liked 상태의 데이터를 ProfileData로 매핑
            val likedItems = likedSongs.map { song ->
                ProfileData(song.title, song.singer, song.coverImg)
            }

            // MainThread에서 RecyclerView 업데이트
            withContext(Dispatchers.Main) {
                items.clear()
                items.addAll(likedItems)
                recyclerView.adapter?.notifyDataSetChanged() // 데이터 변경 알림
            }
        }
    }
}
