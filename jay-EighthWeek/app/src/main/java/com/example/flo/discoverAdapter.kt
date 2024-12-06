package com.example.flo

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class discoverAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3 // 탭 개수
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SearchFragment() // 첫 번째 탭에 표시할 Fragment
            1 -> DiscoverFragment()
            2 -> SearchFragment()
            else -> throw IllegalStateException("Invalid position")
        }
    }
}
