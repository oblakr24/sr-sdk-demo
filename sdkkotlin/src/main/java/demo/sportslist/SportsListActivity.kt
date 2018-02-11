package demo.sportslist

import demo.sdkkotlin.R
import demo.base.BaseActivity
import demo.utils.GenericAdapter
import demo.utils.SimpleRenderable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.jetbrains.anko.find

/**
 * Lists the supported sports
 */
class SportsListActivity : BaseActivity() {

    override val contentResourceId = R.layout.activity_sports_list

    private val recycler by lazy { find<RecyclerView>(R.id.recycler_sports).apply {
        layoutManager = LinearLayoutManager(this@SportsListActivity, LinearLayoutManager.VERTICAL, false)
    } }

    private val adapter = GenericAdapter()

    override fun initUI(content: ViewGroup) {
        title = "All supported sports"
    }

    override fun loadData() {
        adapter.setNewItems(app.allSports.map { SimpleRenderable(it.name) })
        recycler.adapter = adapter
        showContent()
    }
}
