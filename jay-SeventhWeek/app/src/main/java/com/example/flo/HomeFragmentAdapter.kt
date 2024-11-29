package com.example.flo
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat

class HomeFragmentAdapter(private val itemList: List<SampleData>, private val onAlbumClick: (Any?) -> Unit):
    RecyclerView.Adapter<HomeFragmentAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.album)
        val artist: TextView = itemView.findViewById(R.id.album_artist)
        val title: TextView = itemView.findViewById(R.id.album_title)
        val album : ImageView = itemView.findViewById(R.id.album)
        val play : ImageView = itemView.findViewById(R.id.play)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.album_list_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.imageView.setImageResource(item.imageResId)
        holder.artist.text = item.artist
        holder.title.text = item.title

        holder.album.setOnClickListener {
            Log.d("HomeFragmentAdapter", "Album clicked: ${item.title}")
            onAlbumClick(item.title)
        }

        holder.play.setOnClickListener {
            val context = holder.itemView.context
            val serviceIntent = Intent(context, BackgroundService::class.java).apply {
                putExtra("song", item.title)
                putExtra("artist", item.artist)
            }
                ContextCompat.startForegroundService(context, serviceIntent)
                holder.play.setImageResource(R.drawable.btn_miniplay_pause)
            }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}

data class SampleData(
    val title: String,
    val artist: String,
    val imageResId: Int,
    val id:Int,
    val mp3:Int
)
