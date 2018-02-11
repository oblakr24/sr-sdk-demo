package demo.matchlist

import demo.sdkkotlin.R
import demo.base.BaseActivity
import demo.base.Extras
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.ViewGroup
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.find

/**
 * An activity holding a pager for match lists for different days
 */
class MatchListPagerActivity : BaseActivity() {

    override val contentResourceId = R.layout.activity_match_list_pager

    private val pager by lazy { find<ViewPager>(R.id.match_list_pager) }

    override fun initUI(content: ViewGroup) {
        title = "  ${app.selectedSport.name} match list"

        pager.adapter = MatchListPagerAdapter(supportFragmentManager)
        pager.currentItem = 7
    }

    override fun loadData() {
        showContent(false)
    }

    class MatchListPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return MatchListFragment().apply { arguments = bundleOf(Extras.Offset to position - 7) }
        }

        // plus/minus a week's worth of pages - SDK limitation
        override fun getCount() = 15
    }
}
