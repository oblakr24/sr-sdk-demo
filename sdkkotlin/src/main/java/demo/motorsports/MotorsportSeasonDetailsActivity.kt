package demo.motorsports

import demo.base.BasePagerActivity
import demo.base.Extras
import demo.base.PanelModelProvider
import demo.utils.PanelBuilder
import ag.sportradar.sdk.core.loadable.Callback
import ag.sportradar.sdk.core.loadable.OneTimeLoadable
import ag.sportradar.sdk.sports.model.motostport.AnyMotorsportSeason
import ag.sportradar.sdk.sports.model.motostport.AnyMotorsportSeasonDetails
import ag.sportradar.sdk.sports.model.motostport.AnyMotorsportStage
import ag.sportradar.sdk.sports.model.motostport.MotorsportController
import ag.sportradar.sdk.sports.model.motostport.formulaone.FormulaOne
import android.content.Context
import org.jetbrains.anko.startActivity

/**
 * Tasked with providing the details of a motorsport season
 */
class MotorsportSeasonDetailsActivity : BasePagerActivity(), PanelModelProvider<AnyMotorsportSeasonDetails> {

    lateinit var season: AnyMotorsportSeason

    private val controller by lazy { sdk.sportSpecificsController(app.selectedSport) as MotorsportController<AnyMotorsportSeason, AnyMotorsportStage> }

    override val panels by lazy {
        if (app.selectedSport == FormulaOne) {

            listOf(
                    PanelBuilder({ MotorsportSeasonStagesPanel() }, "Stages"),
                    PanelBuilder({ MotorsportSeasonCompetitorsPanel() }, "Competitors"),
                    PanelBuilder({ FormulaOneSeasonTeamsPanel() }, "Teams"),
                    PanelBuilder({ FormulaOneSeasonTeamStatsPanel() }, "Team statistics"),
                    PanelBuilder({ MotorsportSeasonDriverStatsPanel() }, "Driver statistics")
            )
        } else {
            listOf(
                    PanelBuilder({ MotorsportSeasonStagesPanel() }, "Stages"),
                    PanelBuilder({ MotorsportSeasonCompetitorsPanel() }, "Competitors"),
                    PanelBuilder({ MotorsportSeasonDriverStatsPanel() }, "Driver statistics")
            )
        }
    }

    override fun getLoadable() = season as OneTimeLoadable<AnyMotorsportSeasonDetails>

    override fun loadData() {
        val handler = controller.getSeasonById(modelId, object : Callback<AnyMotorsportSeason?> {
            override fun onSuccess(result: AnyMotorsportSeason?) {
                if (result == null) {
                    showEmpty()
                } else {
                    season = result
                    showContent()
                }
            }
            override fun onFailure(t: Throwable) {
                showEmpty("Failure: ${t.message}")
            }
        })

        handlers.add(handler)
    }

    companion object {
        fun newInstance(context: Context, season: AnyMotorsportSeason) {
            context.startActivity<MotorsportSeasonDetailsActivity>(Extras.Title to "${season.name} details", Extras.ModelId to season.id)
        }
    }
}
