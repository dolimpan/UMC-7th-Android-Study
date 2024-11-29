package com.example.flo.ui.theme

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.flo.Page1
import com.example.flo.Page2

class HomeFragmentAdapter2(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val fragmentList = listOf(
        Page1(),
        Page2()
    )

    override fun getItemCount() = fragmentList.size

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}