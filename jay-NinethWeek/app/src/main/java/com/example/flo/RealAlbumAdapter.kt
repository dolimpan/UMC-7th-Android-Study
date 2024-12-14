package com.example.flo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class RealAlbumAdapter(private val itemList: List<SampleData>) :
    RecyclerView.Adapter<RealAlbumAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.item_title)
        val artistText: TextView = itemView.findViewById(R.id.item_description)
        val play :ImageView = itemView.findViewById(R.id.play)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_album_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val service = BackgroundService()
        val item = itemList[position]
        holder.titleText.text = item.title
        holder.artistText.text = item.artist
        holder.play.setOnClickListener {
            val fragmentManager = (holder.itemView.context as AppCompatActivity).supportFragmentManager
            val realAlbumFragment = RealAlbumFragment()
            Log.d("HomeFragment", "Song inserted: ${item.title}")

            val intent = Intent(holder.itemView.context, PlayerActivity::class.java).apply {
                putExtra("artist", item.artist)
                putExtra("song", item.title)
                putExtra("songid", item.id)
                putExtra("mp3",item.mp3)
                putExtra("cover", item.imageResId)
            }
            Log.d("RealalbumAdapter", "${item.artist} ${item.title} ${item.id} ${item.mp3} ")
            holder.itemView.context.startActivity(intent)

        }


    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
