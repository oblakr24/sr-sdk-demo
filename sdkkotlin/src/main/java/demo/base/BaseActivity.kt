package demo.base

import demo.sdkkotlin.R
import demo.DemoApp
import ag.sportradar.sdk.core.loadable.CallbackHandler
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.TextView
import demo.views.StateControlView
import org.jetbrains.anko.find

/**
 * Created by rokoblak on 1/10/18.
 */

abstract class BaseActivity : AppCompatActivity(), StateControlView {

    val app: DemoApp by lazy { application as DemoApp }

    protected val sdk by lazy { app.sdk }

    abstract val contentResourceId: Int

    override val contentView by lazy { find<ViewGroup>(R.id.layout_content) }
    override val loadingView by lazy { find<ViewGroup>(R.id.layout_loading) }
    override val emptyView by lazy { find<ViewGroup>(R.id.layout_empty) }
    override val emptyMessage by lazy { emptyView.find<TextView>(R.id.text_empty) }

    override val handlers = mutableSetOf<CallbackHandler>()

    protected open val reloadOnPause = false

    override var hasInitialData = false

    protected open val needsTracking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentResourceId)
        initUI(contentView)
    }

    abstract fun initUI(content: ViewGroup)

    abstract fun loadData()

    override fun onPause() {
        super.onPause()
        handlers.forEach { it.stop() }
    }

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
}