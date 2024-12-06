package com.example.flo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class discoverRecycleAdapter (
    private val items: List<String>,
    private var selectedPosition: Int,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<discoverRecycleAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.tabText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_indicator_tab, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val isSelected = position == selectedPosition
        holder.textView.text = items[position]

        // 배경색과 텍스트 색상 변경
        holder.textView.setBackgroundResource(
            if (isSelected) R.drawable.selected_tab_background else R.drawable.default_tab_background
        )

        holder.textView.setTextColor(
            if (isSelected)         ContextCompat.getColor(holder.itemView.context, R.color.white)
            else ContextCompat.getColor(holder.itemView.context, R.color.purple_200)

        )

        // 클릭 이벤트 처리
        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition) // 이전 선택된 탭 업데이트
            notifyItemChanged(position) // 현재 선택된 탭 업데이트
            onItemClick(position) // 콜백 호출
        }
    }

    override fun getItemCount(): Int = items.size
}