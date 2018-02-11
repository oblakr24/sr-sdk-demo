package demo.base

import demo.sdkkotlin.R
import demo.utils.GenericPagerAdapter
import demo.utils.PanelBuilder
import android.support.v4.view.ViewPager
import android.view.ViewGroup
import org.jetbrains.anko.find

/**
 * A base activity with a pager with panels showing the model's details
 */
abstract class BasePagerActivity : BaseActivity() {

    override val contentResourceId = R.layout.activity_base_pager

    private val pager by lazy { find<ViewPager>(R.id.content_pager) }

    protected var modelId: Long = -1

    protected abstract val panels: List<PanelBuilder>

    override fun initUI(content: ViewGroup) {
        title = intent.getStringExtra(Extras.Title)
        if (hasInitialData) {
            showContent(false)
            initPager()
        } else {
            modelId = intent.getLongExtra(Extras.ModelId, -1)
        }
    }

    private fun initPager() {
        pager.adapter = GenericPagerAdapter(supportFragmentManager, panels)
        pager.currentItem = 0
    }

    override fun showContent(animate: Boolean) {
        if (!hasInitialData) {
            initPager()
        }
        super.showContent(false)
    }
}
