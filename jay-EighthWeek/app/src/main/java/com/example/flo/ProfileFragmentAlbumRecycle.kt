package com.example.flo

import ProfileDataAdapter
import ProfileDataAlbumAdapter
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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

class ProfileFragmentAlbumRecycle : Fragment() {
    private lateinit var dbAlbum: AppDatabase
    private lateinit var dbAlbums : AppDatabase
    private val items = mutableListOf<AlbumData>() // 데이터 리스트
    private var selected = false
    private lateinit var extractedUserId : String
    private lateinit var sharedPref : SharedPreferences
    private lateinit var token : String



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.store_fragment, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val clickAll = view.findViewById<TextView>(R.id.selectall_tv)
        val delete = view.findViewById<TextView>(R.id.remove_tv)
        sharedPref = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        token = sharedPref.getString("jwt_token", null).toString()
        extractedUserId = token?.let { JwtUtils.getUserIdFromToken(it) }.toString()

        delete.visibility = View.GONE
        dbAlbum = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "liked_album"
        ).build()

        dbAlbums = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "album_database"
        ).build()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ProfileDataAlbumAdapter(items)

        // 초기 데이터 로드
        fetchLikedSongsAndUpdateUI(recyclerView, items)
        clickAll.setOnClickListener {
            val adapter = recyclerView.adapter as? ProfileDataAlbumAdapter
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
            delete.visibility = View.GONE
        }
        return view
    }


    override fun onResume() {
        super.onResume()
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView)

        sharedPref = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        token = sharedPref.getString("jwt_token", null).toString()
        extractedUserId = token?.let { JwtUtils.getUserIdFromToken(it) }.toString()

        if (recyclerView != null) {
            fetchLikedSongsAndUpdateUI(recyclerView, items) // 데이터 업데이트
        }
    }

    private fun fetchLikedSongsAndUpdateUI(recyclerView: RecyclerView, items: MutableList<AlbumData>) {
        CoroutineScope(Dispatchers.IO).launch {
            val indexes = dbAlbum.LikedAlbumDAO().getIdsByUserId(extractedUserId.toString())
            Log.e("a", "$indexes")
            val likedItems = indexes.mapNotNull { id ->
                val albumData = dbAlbums.AlbumTableDAO().getAlbumDataById(id)
                Log.d("Debug", "AlbumData for ID $id: $albumData") // 여기서 null인지 확인
                albumData
            }
            val data = dbAlbum.AlbumTableDAO().getAllAlbums()

            withContext(Dispatchers.Main) {
                Log.d("Debug", "Liked items in MainThread: $likedItems")
                Log.d("Debug", "Items before update: $items")
                items.clear()
                items.addAll(likedItems)

                Log.d("Debug", "Items after update: $items")
                recyclerView.adapter?.notifyDataSetChanged() // 데이터 변경 알림

            }
        }
    }
}
