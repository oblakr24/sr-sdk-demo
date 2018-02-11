package demo.favourites

import demo.sdkkotlin.R
import demo.base.BaseActivity
import demo.utils.GenericAdapter
import demo.utils.Renderable
import demo.utils.SimpleRenderable
import demo.utils.StyledRenderable
import ag.sportradar.sdk.core.loadable.Callback
import ag.sportradar.sdk.mdp.request.favourites.FavouriteTag
import ag.sportradar.sdk.mdp.request.favourites.FavouritesMappingResponse
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.jetbrains.anko.find

/**
 * Lists the user's favourites
 */
class FavouritesActivity : BaseActivity() {

    override val contentResourceId = R.layout.activity_favourites

    private val recycler by lazy { find<RecyclerView>(R.id.recycler_favourites).apply {
        layoutManager = LinearLayoutManager(this@FavouritesActivity, LinearLayoutManager.VERTICAL, false)
    } }

    private val adapter = GenericAdapter()

    override fun initUI(content: ViewGroup) {
        recycler.adapter = adapter
    }

    override fun loadData() {
        val handler = sdk.favouritesController.getFavouriteIds(object : Callback<FavouritesMappingResponse> {
            override fun onSuccess(result: FavouritesMappingResponse?) {
                adapter.items.addAll(createItems(result?.favorites))
                if (adapter.items.isEmpty()) {
                    showEmpty("No favourites")
                } else {
                    showContent()
                }
            }

            override fun onFailure(t: Throwable) {
                t.printStackTrace()
                showEmpty(t.message ?: "Error")
            }
        })

        handlers.add(handler)
    }

    /**
     * Creates the adapter items for each of the favourite tags
     */
    private fun createItems(favouritesMapping: Map<FavouriteTag, List<Long>>?): List<Renderable> {
        val data = mutableListOf<Renderable>()
        favouritesMapping?.forEach { (tag, ids) ->
            data.add(StyledRenderable(tag.name.toLowerCase().capitalize(), null, true, Color.LTGRAY))
            data.addAll(ids.map { SimpleRenderable(it.toString()) })
        }
        return data
    }
}
