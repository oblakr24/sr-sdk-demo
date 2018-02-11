package demo.base

import demo.sdkkotlin.R
import demo.DemoApp
import ag.sportradar.sdk.core.loadable.CallbackHandler
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import demo.views.StateControlView
import org.jetbrains.anko.find

/**
 * Created by rokoblak on 1/10/18.
 * A base fragment class
 */
abstract class BaseFragment : Fragment(), StateControlView {

    val app: DemoApp by lazy { activity?.application as DemoApp }

    protected val sdk by lazy { app.sdk }

    abstract val layoutResourceId: Int

    protected lateinit var layout: View
    override val contentView by lazy { layout.find<ViewGroup>(R.id.layout_content) }
    override val loadingView by lazy { layout.find<ViewGroup>(R.id.layout_loading) }
    override val emptyView by lazy { layout.find<ViewGroup>(R.id.layout_empty) }
    override val emptyMessage by lazy { emptyView.find<TextView>(R.id.text_empty) }

    override val handlers = mutableSetOf<CallbackHandler>()

    protected open val reloadOnPause: Boolean = false

    protected open val needsTracking = false

    override var hasInitialData = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutResourceId, container, false)
        layout = view
        initUI(contentView)
        return view
    }

    abstract fun initUI(content: ViewGroup)

    abstract fun loadData()

    override fun onResume() {
        super.onResume()
        if (reloadOnPause || !hasInitialData) {
            showLoading()
            loadData()
        } else if (hasInitialData && !needsTracking) {
            showContent(false)
        } else if (needsTracking) {
            loadData()
        }
    }

    override fun onPause() {
        super.onPause()
        handlers.forEach { it.stop() }
    }
}