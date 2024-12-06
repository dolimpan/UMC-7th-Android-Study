import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.ProfileData
import com.example.flo.R


class ProfileDataAdapter(private val itemList: MutableList<ProfileData>) : RecyclerView.Adapter<ProfileDataAdapter.ItemViewHolder>() {

    private var allSelected = false // 모든 항목 선택 상태

    // ViewHolder 정의
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.item_title)
        val descriptionTextView: TextView = itemView.findViewById(R.id.item_description)
        val album: ImageView = itemView.findViewById(R.id.album)
        val delete : ImageView = itemView.findViewById(R.id.delete)
        val container = itemView.findViewById<View>(R.id.container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.titleTextView.text = item.title
        holder.descriptionTextView.text = item.description
        holder.album.setImageResource(item.album)
        holder.delete.setOnClickListener {
            removeItem(position)
        }

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