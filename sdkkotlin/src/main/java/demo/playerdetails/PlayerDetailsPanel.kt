package demo.playerdetails

import demo.base.BasePanel
import demo.utils.*
import ag.sportradar.sdk.core.model.teammodels.AnyTeamPlayerDetails
import android.graphics.Color

/**
 * Created by rokoblak on 1/12/18.
 */
abstract class PlayerDetailsPanel : BasePanel<AnyTeamPlayerDetails>()

class PlayerStatsPanel : PlayerDetailsPanel() {

    override val loadingParams = DetailsLoadingProperties.PlayerStats

    override fun createFullItems(details: AnyTeamPlayerDetails): List<Renderable>? {
        val items = mutableListOf<Renderable>()

        details.totalStats?.let {
            items.add(StyledRenderable("Total stats", null, true, Color.LTGRAY))
            it.forEach { stat ->
                items.add(SimpleRenderable(stat.toString()))
            }
        }

        details.teamStatistics?.let {
            items.add(StyledRenderable("Team stats", null, true, Color.LTGRAY))
            it.forEach { (team, teamStatMapping) ->
                items.add(StyledRenderable("${team.name} statistics", null, true))

                teamStatMapping.forEach { (season, stats) ->
                    items.add(season.toStyledRenderable(app))

                    stats.forEach {
                        items.add(SimpleRenderable(it.toString()))
                    }
                }
            }
        }

        return items
    }
}

class PlayerSeasonsPanel : PlayerDetailsPanel() {

    override val loadingParams = DetailsLoadingProperties.PlayerSeasons

    override fun createFullItems(details: AnyTeamPlayerDetails): List<Renderable>? {
        return details.seasons?.map { it.toRenderable(app) }
    }
}

class PlayerTeamsPanel : PlayerDetailsPanel() {

    override val loadingParams = DetailsLoadingProperties.Teams

    override fun createFullItems(details: AnyTeamPlayerDetails): List<Renderable>? {
        return details.teams?.map { it.toRenderable(this) }
    }
}

class PlayerTournamentsPanel : PlayerDetailsPanel() {

    override val loadingParams = DetailsLoadingProperties.Tournaments

    override fun createFullItems(details: AnyTeamPlayerDetails): List<Renderable>? {
        return details.tournaments?.map { it.toRenderable(context!!) }
    }
}

class PlayerRolesPanel : PlayerDetailsPanel() {

    override val loadingParams = DetailsLoadingProperties.Roles

    override fun createFullItems(details: AnyTeamPlayerDetails): List<Renderable>? {
        return details.roles?.map { it.toRenderable(app.dateFormat) }
    }
}