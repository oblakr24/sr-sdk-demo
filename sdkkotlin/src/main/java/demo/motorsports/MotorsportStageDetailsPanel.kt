package demo.motorsports

import demo.base.BaseActivity
import demo.base.BasePanel
import demo.utils.*
import ag.sportradar.sdk.sports.model.motostport.AnyMotorsportStageDetails
import ag.sportradar.sdk.sports.model.motostport.formulaone.FormulaOneRaceStageDetails
import ag.sportradar.sdk.sports.model.motostport.rally.RallyRaceStageDetails

/**
 * Created by rokoblak on 1/15/18.
 */
abstract class MotorsportStageDetailsPanel : BasePanel<AnyMotorsportStageDetails>()

class FormulaOneStageRaceEventsPanel : MotorsportStageDetailsPanel() {

    override val loadingParams = DetailsLoadingProperties.F1StageRaceEvents

    override fun createFullItems(details: AnyMotorsportStageDetails): List<Renderable> {
        details as FormulaOneRaceStageDetails
        val items = mutableListOf<Renderable>()
        details.race?.toStyledRenderable(activity as BaseActivity, app.dateFormat)?.let { items.add(it) }
        items.add(StyledRenderable("Qualifications", null, true))
        details.qualifications?.map { it.toStyledRenderable(activity as BaseActivity, app.dateFormat) }?.let { items.addAll(it) }
        items.add(StyledRenderable("Practices", null, true))
        details.practices?.map { it.toStyledRenderable(activity as BaseActivity, app.dateFormat) }?.let { items.addAll(it) }
        return items
    }
}

class MotorsportDriversPanel : MotorsportStageDetailsPanel() {

    override val loadingParams = DetailsLoadingProperties.F1StageDrivers

    override fun createFullItems(details: AnyMotorsportStageDetails): List<Renderable> {
        return details.drivers?.map { it.toStyledRenderable() } ?: emptyList()
    }
}

class MotorsportDriverStatsPanel : MotorsportStageDetailsPanel() {

    override val loadingParams = DetailsLoadingProperties.F1StageDriversStats

    override fun createFullItems(details: AnyMotorsportStageDetails): List<Renderable> {
        val items = mutableListOf<Renderable>()
        details.driverStatistics?.forEach { (driver, stats) ->
            items.add(driver.toStyledRenderable())
            items.addAll(stats.map { SimpleRenderable(it.toString()) })
        }
        return items
    }
}

class FormulaOneTeamStatsPanel : MotorsportStageDetailsPanel() {

    override val loadingParams = DetailsLoadingProperties.F1StageTeamStats

    override fun createFullItems(details: AnyMotorsportStageDetails): List<Renderable> {
        details as FormulaOneRaceStageDetails
        val items = mutableListOf<Renderable>()
        details.teamStatistics?.forEach { (team, stats) ->
            items.add(team.toStyledRenderable())
            items.addAll(stats.map { SimpleRenderable(it.toString()) })
        }
        return items
    }
}

class RallyStageRaceEventsPanel : MotorsportStageDetailsPanel() {

    override val loadingParams = DetailsLoadingProperties.F1StageRaceEvents

    override fun createFullItems(details: AnyMotorsportStageDetails) = (details as RallyRaceStageDetails).races?.map { it.toStyledRenderable(activity as BaseActivity, app.dateFormat) } ?: emptyList()
}

class MotorsportStageTeamsPanel : MotorsportStageDetailsPanel() {

    override val loadingParams = DetailsLoadingProperties.F1StageTeams

    override fun createFullItems(details: AnyMotorsportStageDetails) = (details as FormulaOneRaceStageDetails).teams?.map { it.toStyledRenderable() } ?: emptyList()
}