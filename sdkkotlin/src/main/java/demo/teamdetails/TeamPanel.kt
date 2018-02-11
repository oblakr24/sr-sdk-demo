package demo.teamdetails

import demo.base.BasePanel
import demo.utils.*
import ag.sportradar.sdk.core.model.teammodels.*

/**
 * Created by rokoblak on 1/11/18.
 */
abstract class TeamPanel : BasePanel<AnyTeamDetails>()

class LastNextMatchesPanel : TeamPanel() {

    override val loadingParams = DetailsLoadingProperties.TeamLastNextMatches

    override fun createFullItems(details: AnyTeamDetails): List<Renderable>? {
        val data = mutableListOf<Renderable>()
        details.lastMatches?.let { matches ->
            data.add(StyledRenderable("Last matches: ", null, true))
            matches.forEach {
                data.add((it as AnyTeamMatch).toRenderable(context!!))
            }
        }
        details.nextMatches?.let { matches ->
            data.add(StyledRenderable("Next matches: ", null, true))
            matches.forEach {
                data.add((it as AnyTeamMatch).toRenderable(context!!))
            }
        }
        return data
    }
}

class TeamSquadPanel : TeamPanel() {

    override val loadingParams = DetailsLoadingProperties.TeamSquad

    override fun createFullItems(details: AnyTeamDetails): List<Renderable>? {
        val data = mutableListOf<Renderable>()

        (details as? SquadTeamDetails<*, *, *, *>)?.let { details ->
            val squad = details.squad
            val roles = details.roles
            squad?.forEach { player ->
                val lastRole = roles?.get(player)?.first()?.name ?: "/"
                data.add(player.toStyledRenderable(context!!, lastRole))
            }
        }

        return data
    }
}

class TeamInfoPanel : TeamPanel() {

    override val loadingParams = DetailsLoadingProperties.TeamInfo

    override fun createFullItems(details: AnyTeamDetails): List<Renderable>? {
        val data = mutableListOf<Renderable>()

        (details as? SquadTeamDetails<*, *, *, *>)?.homeVenue?.let { stadium ->
            data.add(StyledRenderable(stadium.name, "Home stadium", true))
        }

        details.manager?.let {
            data.add(StyledRenderable(it.fullname, "Manager", true))
        }

        details.competitions?.let {
            data.add(StyledRenderable("Stages / Tournaments", null, true))
            it.forEach {
                (it as? AnyTeamStage)?.toRenderable(context!!)?.let { data.add(it) }
                (it as? AnyTeamTournament)?.toRenderable(context!!)?.let { data.add(it) }
            }
        }

        return data
    }
}