package com.example.flo

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ProfilePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2 // 탭 개수
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProfileFragmentRecycle() // 첫 번째 탭에 표시할 Fragment
            1 -> DiscoverFragment()
            else -> throw IllegalStateException("Invalid position")
        }
    }
}
