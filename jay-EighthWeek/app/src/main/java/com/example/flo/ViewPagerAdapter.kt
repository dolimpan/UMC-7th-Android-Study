import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.flo.DiscoverFragment
import com.example.flo.HomeFragment
import com.example.flo.ProfileFragment
import com.example.flo.SearchFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val fragmentList = listOf(
        HomeFragment(),
        DiscoverFragment(),
        SearchFragment(),
        ProfileFragment()
    )

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> DiscoverFragment()
            2 -> SearchFragment()
            3 -> ProfileFragment()
            else -> HomeFragment()
        }
    }
}