package demo.motorsports

import demo.base.BasePanel
import demo.utils.toStyledRenderable
import ag.sportradar.sdk.core.loadable.Callback
import ag.sportradar.sdk.core.model.ModelDetails
import ag.sportradar.sdk.sports.model.motostport.AnyMotorsportSeason
import ag.sportradar.sdk.sports.model.motostport.AnyMotorsportStage
import ag.sportradar.sdk.sports.model.motostport.MotorsportController

/**
 * Created by rokoblak on 1/14/18.
 */

abstract class MotorsportPanel : BasePanel<ModelDetails>() {
    protected val controller by lazy { sdk.sportSpecificsController(app.selectedSport) as MotorsportController<AnyMotorsportSeason, AnyMotorsportStage> }
}

class MotorsportSeasonsPanel : MotorsportPanel() {

    override fun loadData() {

        val handler = controller.loadSeasons(object : Callback<List<AnyMotorsportSeason>?> {
            override fun onSuccess(result: List<AnyMotorsportSeason>?) {
                if (result == null || result.isEmpty()) {
                    showEmpty()
                } else {
                    val items = result.map { it.toStyledRenderable(context!!) }
                    adapter.setNewItems(items)
                    showContent()
                }
            }
            override fun onFailure(t: Throwable) {
                t.printStackTrace()
                showEmpty()
            }
        })

        handlers.add(handler)
    }
}

class MotorsportLastSeasonStagePanel : MotorsportPanel() {

    override fun loadData() {
        val handler = controller.loadLastSeasonStages(object : Callback<List<AnyMotorsportStage>?> {
            override fun onSuccess(result: List<AnyMotorsportStage>?) {
                if (result == null || result.isEmpty()) {
                    showEmpty()
                } else {
                    val items = result.map { it.toStyledRenderable(context!!, app.dateFormat) }
                    adapter.setNewItems(items)
                    showContent()
                }
            }

            override fun onFailure(t: Throwable) {
                t.printStackTrace()
                showEmpty()
            }
        })

        handlers.add(handler)
    }
}