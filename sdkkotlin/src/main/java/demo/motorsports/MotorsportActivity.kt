package demo.motorsports

import demo.base.BasePagerActivity
import demo.utils.PanelBuilder
import android.view.ViewGroup

/**
 * Motorsport landing screen
 */
class MotorsportActivity : BasePagerActivity() {

    override val panels by lazy {
        listOf(
                PanelBuilder({ MotorsportSeasonsPanel() }, "Seasons"),
                PanelBuilder({ MotorsportLastSeasonStagePanel() }, "Last season stages")
        )
    }

    override fun initUI(content: ViewGroup) {
        super.initUI(content)
        title = app.selectedSport.name
    }

    override fun loadData() {
        showContent()
    }
}
