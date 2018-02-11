package demo.utils

import demo.base.BaseFragment
import demo.base.Extras
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import org.jetbrains.anko.bundleOf

/**
 * Created by rokoblak on 1/12/18.
 */
class GenericPagerAdapter(fm: FragmentManager, private val panelBuilders: List<PanelBuilder>) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        val builder = panelBuilders[position]
        val panelArgs = builder.extras.toMutableList().apply {
            add(Extras.Title to builder.title)
        }.toTypedArray()
        return builder.fragmentInitializer().apply { arguments = bundleOf(*panelArgs) }
    }

    override fun getCount() = panelBuilders.size
}

class PanelBuilder(val fragmentInitializer: () -> BaseFragment, val title: String, vararg val extras: Pair<String, Any?>)