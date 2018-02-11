package demo.base

import demo.sdkkotlin.R
import demo.utils.GenericAdapter
import demo.utils.Renderable
import ag.sportradar.sdk.core.loadable.*
import ag.sportradar.sdk.core.model.DetailsParams
import ag.sportradar.sdk.core.model.ModelDetails
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.TextView
import org.jetbrains.anko.find

/**
 * Created by rokoblak on 1/12/18.
 * A base panel with loading/tracking functionality
 * The child should specify the type of the loaded details, the model for which to load them
 * as well as the methods for creating adapter items from the details.
 */
abstract class BasePanel<D: ModelDetails> : BaseFragment() {

    override val layoutResourceId = R.layout.layout_panel_generic

    private val modelProvider by lazy { (activity as? PanelModelProvider<D>) }

    protected val loadable: OneTimeLoadable<D>? by lazy { modelProvider?.getLoadable() }

    protected val trackable: RepeatingLoadable<D>? by lazy { modelProvider?.getTrackable() }

    private val title by lazy { layout.find<TextView>(R.id.text_panel_title) }

    // if true, updated items (received in onValueChanged) replace the current adapter's items
    protected open val diffItemsOverrideInitials: Boolean = false

    // if true, updated items (received in onValueChanged) don't get handled
    protected open val skipDiffItems: Boolean = false

    private val recycler by lazy { layout.find<RecyclerView>(R.id.base_panel_recycler).apply {
        layoutManager = LinearLayoutManager(this@BasePanel.context, LinearLayoutManager.VERTICAL, false)
    } }

    /**
     * Load the loadable or trackable's details
     */
    override fun loadData() {
        // loadable models
        val loadableHandler = loadable?.loadDetails(loadingParams!!, object : Callback<D> {
            override fun onSuccess(result: D?) {
                if (result != null) {
                    val data = createFullItems(result)
                    if (data == null || data.isEmpty()) {
                        showEmpty()
                    } else {
                        adapter.setNewItems(data)
                        showContent()
                    }
                } else {
                    showEmpty()
                }
            }

            override fun onFailure(t: Throwable) {
                t.printStackTrace()
                showEmpty("Failure: ${t.message}")
            }
        })

        loadableHandler?.let { handlers.add(it) }

        // trackable models
        val trackableHandler = trackable?.loadDetails(loadingParams!!, object : ValueChangeCallback<D> {
            override fun onInitialLoad(result: D?) {
                if (result != null) {
                    val data = createFullItems(result)
                    if (data == null || data.isEmpty()) {
                        showEmpty()
                    } else {
                        adapter.setNewItems(data)
                        showContent()
                    }
                } else {
                    showEmpty()
                }
            }

            override fun onValueChanged(diff: D) {
                if (skipDiffItems) return
                val data = createDiffItems(diff)
                if (diffItemsOverrideInitials && (data == null || data.isEmpty())) {
                    showEmpty()
                } else if (diffItemsOverrideInitials && data != null && data.isNotEmpty()) {
                    adapter.setNewItems(data)
                    showContent()
                } else if (!diffItemsOverrideInitials) {
                    data?.let { adapter.setNewItems(adapter.items + it) }
                }
            }

            override fun onFailure(t: Throwable) {
                t.printStackTrace()
                showEmpty("Failure: ${t.message}")

            }
        })
        trackableHandler?.let { handlers.add(it) }
    }

    /**
     * Create the full details' items
     * (the initial load's items in case of trackables)
     */
    open fun createFullItems(details: D): List<Renderable>? = null

    /**
     * Create the items of trackables' diff results
     */
    open fun createDiffItems(details: D): List<Renderable>? = null

    /**
     * The parameters defining which details to load
     */
    open val loadingParams: DetailsParams<D>? = null

    protected val adapter = GenericAdapter()

    override fun initUI(content: ViewGroup) {
        title.text = arguments?.getString(Extras.Title)
        recycler.adapter = adapter
    }
}

/**
 * Implemented by parent activities providing either loadable or trackable models
 */
interface PanelModelProvider<D: ModelDetails> {
    fun getLoadable(): OneTimeLoadable<D>? = null
    fun getTrackable(): RepeatingLoadable<D>? = null
}