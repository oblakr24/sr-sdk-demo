package demo.motorsports

import demo.base.BasePagerActivity
import demo.base.Extras
import demo.base.PanelModelProvider
import demo.utils.PanelBuilder
import ag.sportradar.sdk.core.loadable.Callback
import ag.sportradar.sdk.core.loadable.RepeatingLoadable
import ag.sportradar.sdk.sports.model.motostport.AnyMotorsportSeason
import ag.sportradar.sdk.sports.model.motostport.AnyMotorsportStage
import ag.sportradar.sdk.sports.model.motostport.AnyMotorsportStageDetails
import ag.sportradar.sdk.sports.model.motostport.MotorsportController
import ag.sportradar.sdk.sports.model.motostport.formulaone.FormulaOne
import ag.sportradar.sdk.sports.model.motostport.rally.Rally
import android.content.Context
import android.view.ViewGroup
import org.jetbrains.anko.startActivity

/**
 * Tasked with providing the details of a motorsport stage
 */
class MotorsportStageDetailsActivity : BasePagerActivity(), PanelModelProvider<AnyMotorsportStageDetails> {

    private var stageId: Long = -1

    lateinit var stage: AnyMotorsportStage

    private val controller by lazy { sdk.sportSpecificsController(app.selectedSport) as MotorsportController<AnyMotorsportSeason, AnyMotorsportStage> }

    override val panels by lazy {
        when (app.selectedSport) {
            FormulaOne -> listOf(
                    PanelBuilder({ FormulaOneStageRaceEventsPanel() }, "Practices, qualifications and race"),
                    PanelBuilder({ MotorsportStageTeamsPanel() }, "Teams"),
                    PanelBuilder({ MotorsportDriversPanel() }, "Drivers"),
                    PanelBuilder({ MotorsportDriverStatsPanel() }, "Driver stats"),
                    PanelBuilder({ FormulaOneTeamStatsPanel() }, "Team stats"))
            Rally -> listOf(
                    PanelBuilder({ RallyStageRaceEventsPanel() }, "Races"),
                    PanelBuilder({ MotorsportDriversPanel() }, "Drivers"),
                    PanelBuilder({ MotorsportDriverStatsPanel() }, "Driver stats"))
            else -> emptyList()
        }
    }

    override fun getTrackable() = stage as RepeatingLoadable<AnyMotorsportStageDetails>

    override fun initUI(content: ViewGroup) {
        super.initUI(content)
        stageId = intent.getLongExtra(Extras.StageId, -1)
    }

    override fun loadData() {
        val handler = controller.getStageById(stageId, object : Callback<AnyMotorsportStage?> {
            override fun onSuccess(result: AnyMotorsportStage?) {
                if (result == null) {
                    showEmpty()
                } else {
                    stage = result
                    showContent()
                }
            }
            override fun onFailure(t: Throwable) {
                t.printStackTrace()
                showEmpty("Failure: ${t.message}")
            }
        })
        handlers.add(handler)
    }

    companion object {
        fun newInstance(context: Context, stage: AnyMotorsportStage) {
            context.startActivity<MotorsportStageDetailsActivity>(Extras.Title to "${stage.name} details", Extras.StageId to stage.id)
        }
    }
}
