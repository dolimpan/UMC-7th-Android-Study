package com.example.flo
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DiscoverFragment : Fragment() {
    private val tabItems = listOf("차트", "영상", "장르", "상황", "분위기")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val view = inflater.inflate(R.layout.discover_fragment, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.indicatorRecyclerView)

        val adapter = discoverRecycleAdapter(tabItems, 0) { position ->
            // 탭 클릭 시 처리
            Log.d("IndicatorFragment", "Selected Tab: ${tabItems[position]}")
            // ViewPager2 연동 시 position 전달
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter

        return view

    }
}
