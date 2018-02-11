package demo.views

import ag.sportradar.sdk.core.loadable.CallbackHandler
import android.view.View
import android.widget.TextView
import demo.utils.crossFadeFrom

/**
 * A base loading/content/empty view with some transition animations
 */
interface StateControlView {

    val contentView: View
    val loadingView: View
    val emptyView: View
    val emptyMessage: TextView

    var hasInitialData: Boolean

    /**
     * The SDK callback handlers -
     * the children should add theirs here so that they get stopped
     * at the right time
     */
    val handlers: MutableSet<CallbackHandler>

    private fun getPreviouslyVisible(): View {
        return when {
            emptyView.visibility == View.VISIBLE -> emptyView
            loadingView.visibility == View.VISIBLE -> loadingView
            else -> contentView
        }
    }

    fun showLoading() {
        handlers.forEach { it.stop() }
        contentView.visibility = View.GONE
        loadingView.visibility = View.VISIBLE
        emptyView.visibility = View.GONE
    }

    fun showContent(animate: Boolean = true) {
        hasInitialData = true
        if (animate) {
            contentView.crossFadeFrom(getPreviouslyVisible(), 400L)
        } else {
            contentView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            loadingView.visibility = View.GONE
        }
    }

    fun showEmpty(message: String = "No data", animate: Boolean = true) {
        if (animate) {
            emptyView.crossFadeFrom(getPreviouslyVisible(), 400L)
        } else {
            contentView.visibility = View.GONE
            loadingView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        }
        emptyMessage.text = message
    }
}