package demo.stagedetails

import demo.base.BasePanel
import demo.utils.*
import ag.sportradar.sdk.core.model.teammodels.AnyTeamStageDetails
import android.graphics.Color

/**
 * Created by rokoblak on 1/12/18.
 */
abstract class StageDetailsPanel : BasePanel<AnyTeamStageDetails>()

class StageCupRosterPanel : StageDetailsPanel() {

    override val loadingParams = DetailsLoadingProperties.StageCupRosters

    override fun createFullItems(details: AnyTeamStageDetails): List<Renderable>? {

        val items = mutableListOf<Renderable>()

        details.cupRosters?.forEach { cupRoster ->

            items.add(StyledRenderable("${cupRoster.name} (${if (cupRoster.active) "active" else "inactive"})", null, true, Color.LTGRAY))

            cupRoster.cupRounds.forEach { cupRound ->

                items.add(StyledRenderable(cupRound.shortName, "Cup round", true, Color.LTGRAY))

                cupRound.blocks.forEach { cupBlock ->

                    items.add(StyledRenderable(cupBlock.winner?.name ?: "/", "Cup block winner", true))

                    cupBlock.matches.forEach {
                        items.add(it.toRenderable(context!!))
                    }
                }
            }
        }
        return items
    }
}

class StageLiveRankingTablePanel : StageDetailsPanel() {

    override val loadingParams = DetailsLoadingProperties.StageLiveRankingTable

    override fun createFullItems(details: AnyTeamStageDetails): List<Renderable>? {
        val items = mutableListOf<Renderable>()

        details.liveRankingTable?.let {

            it.tableItems.forEach {
                val team = it.team

                items.add(StyledRenderable("${team.name} live ranking table", null, true, Color.LTGRAY))

                it.promotionRelegation?.let {
                    items.add(StyledRenderable("${it.name}, position: ${it.position}", "Promotion relegation", true, Color.LTGRAY))
                }

                items.add(StyledRenderable("${team.name} statistics", null, true))
                it.statistics?.forEach {
                    items.add(SimpleRenderable(it.toString()))
                }
            }
        }

        return items
    }
}