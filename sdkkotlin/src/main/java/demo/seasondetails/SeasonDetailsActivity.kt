package demo.seasondetails

import demo.DemoApp
import demo.base.BasePagerActivity
import demo.base.Extras
import demo.base.PanelModelProvider
import demo.utils.PanelBuilder
import ag.sportradar.sdk.core.loadable.OneTimeLoadable
import ag.sportradar.sdk.core.model.teammodels.AnyTeamSeason
import ag.sportradar.sdk.core.model.teammodels.AnyTeamSeasonDetails
import org.jetbrains.anko.startActivity

/**
 * Tasked with providing the details of a season
 */
class SeasonDetailsActivity : BasePagerActivity(), PanelModelProvider<AnyTeamSeasonDetails> {

    override var hasInitialData = true

    private val season by lazy { app.consumeSelectedSeason() }

    override val panels by lazy {
        listOf(
                PanelBuilder({ SeasonFixturesPanel() }, "Season fixtures"),
                PanelBuilder({ SeasonLiveTablePanel() }, "Season livetable"),
                PanelBuilder({ SeasonRankingTablePanel() }, "Season ranking table"),
                PanelBuilder({ SeasonPlayerStatisticsPanel() }, "Player statistics"),
                PanelBuilder({ SeasonGoalsStatisticsPanel() }, "Goals statistics"),
                PanelBuilder({ SeasonCardsStatisticsPanel() }, "Cards statistics"),
                PanelBuilder({ SeasonAssistsStatisticsPanel() }, "Assists statistics")
        )
    }

    override fun getLoadable() = season as OneTimeLoadable<AnyTeamSeasonDetails>

    override fun loadData() {}

    companion object {
        fun newInstance(application: DemoApp, season: AnyTeamSeason) {
            application.setSelectedSeason(season)
            application.startActivity<SeasonDetailsActivity>(Extras.Title to "${season.name} details")
        }
    }
}
