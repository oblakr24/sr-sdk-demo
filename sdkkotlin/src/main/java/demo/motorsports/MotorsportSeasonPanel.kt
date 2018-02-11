package demo.motorsports

import demo.base.BasePanel
import demo.utils.*
import ag.sportradar.sdk.sports.model.motostport.AnyMotorsportSeasonDetails
import ag.sportradar.sdk.sports.model.motostport.formulaone.FormulaOneSeasonDetails

/**
 * Created by rokoblak on 1/14/18.
 */
abstract class MotorsportSeasonPanel : BasePanel<AnyMotorsportSeasonDetails>()

class MotorsportSeasonStagesPanel : MotorsportSeasonPanel() {

    override val loadingParams = DetailsLoadingProperties.F1Stages

    override fun createFullItems(details: AnyMotorsportSeasonDetails) =
            details.stages?.map { it.toStyledRenderable(context!!, app.dateFormat) } ?: emptyList()
}

class MotorsportSeasonCompetitorsPanel : MotorsportSeasonPanel() {

    override val loadingParams = DetailsLoadingProperties.F1Competitors

    override fun createFullItems(details: AnyMotorsportSeasonDetails) =
            details.drivers?.map { it.toStyledRenderable() } ?: emptyList()
}

class FormulaOneSeasonTeamsPanel : MotorsportSeasonPanel() {

    override val loadingParams = DetailsLoadingProperties.F1SeasonTeams

    override fun createFullItems(details: AnyMotorsportSeasonDetails): List<Renderable> {
        val teams = (details as? FormulaOneSeasonDetails)?.teams ?: return emptyList()
        return teams.map { it.toStyledRenderable() }
    }
}

class FormulaOneSeasonTeamStatsPanel : MotorsportSeasonPanel() {

    override val loadingParams = DetailsLoadingProperties.F1SeasonTeamStatistics

    override fun createFullItems(details: AnyMotorsportSeasonDetails): List<Renderable> {
        val teamsStats = (details as? FormulaOneSeasonDetails)?.teamStatistics ?: return emptyList()
        val items = mutableListOf<Renderable>()
        teamsStats.forEach { (team, stats) ->
            items.add(team.toStyledRenderable())
            stats.forEach {
                items.add(SimpleRenderable(it.toString()))
            }
        }
        return  items
    }
}

class MotorsportSeasonDriverStatsPanel : MotorsportSeasonPanel() {

    override val loadingParams = DetailsLoadingProperties.F1SeasonDriverStatistics

    override fun createFullItems(details: AnyMotorsportSeasonDetails): List<Renderable> {
        val items = mutableListOf<Renderable>()
        details.driverStatistics?.forEach { (driver, stats) ->
            items.add(driver.toStyledRenderable())
            stats.forEach {
                items.add(SimpleRenderable(it.toString()))
            }
        }
        return  items
    }
}