import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.AlbumData
import com.example.flo.ProfileData
import com.example.flo.R


class ProfileDataAlbumAdapter(private val itemList: MutableList<AlbumData>) : RecyclerView.Adapter<ProfileDataAlbumAdapter.ItemViewHolder>() {

    private var allSelected = false // 모든 항목 선택 상태

    // ViewHolder 정의
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.name)
        val descriptionTextView: TextView = itemView.findViewById(R.id.artist)
        val album: ImageView = itemView.findViewById(R.id.album)
        val container = itemView.findViewById<View>(R.id.container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_item_album_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.titleTextView.text = item.title
        holder.descriptionTextView.text = item.singer
        holder.album.setImageResource(item.coverImg)

        if (allSelected) {
            holder.container.setBackgroundColor(
                holder.itemView.context.getColor(R.color.purple_200) // 회색
            )
        } else {
            holder.container.setBackgroundColor(
                holder.itemView.context.getColor(R.color.white) // 기본 흰색
            )
        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun removeItem(position: Int) {
        itemList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemList.size)
    }

    fun selectAllItems() {
        allSelected = true
        notifyDataSetChanged()
    }

    fun deselectAllItems() {
        allSelected = false
        notifyDataSetChanged()
    }
}