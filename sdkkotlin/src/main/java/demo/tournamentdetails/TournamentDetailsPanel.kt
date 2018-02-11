package demo.tournamentdetails

import demo.base.BasePanel
import demo.seasondetails.SeasonDetailsActivity
import demo.teamdetails.TeamDetailsActivity
import demo.utils.*
import ag.sportradar.sdk.core.model.teammodels.AnyTeamTournamentDetails
import android.graphics.Color

/**
 * Created by rokoblak on 1/12/18.
 */
abstract class TournamentDetailsPanel : BasePanel<AnyTeamTournamentDetails>()

class TournamentSeasonsPanel : TournamentDetailsPanel() {

    override val loadingParams = DetailsLoadingProperties.TournamentSeason

    override fun createFullItems(details: AnyTeamTournamentDetails): List<Renderable>? {
        val items = mutableListOf<Renderable>()

        details.activeSeason?.let { activeSeason ->
            items.add(object : StyledRenderable(activeSeason.name, "Active season", true, Color.LTGRAY) {
                override val clickListener: (() -> Unit)? = {
                    SeasonDetailsActivity.newInstance(app, activeSeason)
                }
            })
        }

        details.seasons?.let { seasons ->
            if (seasons.isNotEmpty()) items.add(StyledRenderable("All seasons", null, true, Color.LTGRAY))
            seasons.forEach { season ->
                items.add(object : SimpleRenderable(season.name) {
                    override val clickListener: (() -> Unit)? = {
                        SeasonDetailsActivity.newInstance(app, season)
                    }
                })
            }
        }
        return items
    }
}

class TournamentTeamsPanel : TournamentDetailsPanel() {

    override val loadingParams = DetailsLoadingProperties.TournamentTeams

    override fun createFullItems(details: AnyTeamTournamentDetails): List<Renderable>? {
        val items = mutableListOf<Renderable>()

        details.defendingChampion?.let {
            items.add(object : StyledRenderable(it.name, "Defending champion", true, Color.LTGRAY) {
                override val clickListener: (() -> Unit)? = {
                    TeamDetailsActivity.newInstance(this@TournamentTeamsPanel, it)
                }
            })
        }

        details.teams?.let { teams ->
            items.add(StyledRenderable("All teams", null, true, Color.LTGRAY))
            teams.forEach {
                items.add(object : SimpleRenderable(it.name) {
                    override val clickListener: (() -> Unit)? = {
                        TeamDetailsActivity.newInstance(this@TournamentTeamsPanel, it)
                    }
                })
            }
        }

        return items
    }
}
