package demo.seasondetails

import demo.base.BasePanel
import demo.utils.*
import ag.sportradar.sdk.core.model.teammodels.AnyTeamSeasonDetails
import ag.sportradar.sdk.core.model.teammodels.GoalsCardsAssistsSeasonDetails
import ag.sportradar.sdk.core.model.teammodels.RankingSeasonDetails
import ag.sportradar.sdk.core.model.teammodels.statistics.AssistBasedSeasonStatistics
import ag.sportradar.sdk.core.model.teammodels.statistics.CardBasedSeasonStatistics
import ag.sportradar.sdk.core.model.teammodels.statistics.GoalBasedSeasonStatistics
import ag.sportradar.sdk.core.model.teammodels.statistics.PlayerTeamSeasonStats
import android.graphics.Color

/**
 * Created by rokoblak on 1/12/18.
 */
abstract class SeasonDetailsPanel : BasePanel<AnyTeamSeasonDetails>()

class SeasonFixturesPanel : SeasonDetailsPanel() {

    override val loadingParams = DetailsLoadingProperties.SeasonFixtures

    override fun createFullItems(details: AnyTeamSeasonDetails): List<Renderable>? {
        val items = mutableListOf<Renderable>()
        details.fixture?.let { fixture ->
            fixture.groups.forEach { group ->
                items.add(StyledRenderable(group.name, "${app.dateFormat.safeFormat(group.startDate.time)} - ${app.dateFormat.safeFormat(group.endDate?.time)}", true, Color.LTGRAY))
                fixture.getContestsForGroup(group)?.forEach { match ->
                    items.add(match.toRenderable(context!!))
                }
            }
        }
        return items
    }
}

class SeasonLiveTablePanel : SeasonDetailsPanel() {

    override val loadingParams = DetailsLoadingProperties.SeasonLiveTable

    override fun createFullItems(details: AnyTeamSeasonDetails): List<Renderable>? {
        val items = mutableListOf<Renderable>()

        (details as? RankingSeasonDetails<*, *>)?.let { details ->

            if (details.liveRankingTables != null && details.liveRankingTables!!.isNotEmpty()) items.add(StyledRenderable("Live ranking tables", null, true, Color.LTGRAY))

            details.liveRankingTables?.forEach { rankingTable ->

                items.add(StyledRenderable(rankingTable.name, "Group name: ${rankingTable.groupName ?: "/"}", true, Color.LTGRAY))
                items.add(StyledRenderable(rankingTable.competition?.name ?: "", "Competition", true, Color.LTGRAY))

                rankingTable.tableItems.forEach {

                    items.add(StyledRenderable("${it.team.name} ranking table", null, true, Color.LTGRAY))

                    it.promotionRelegation?.let {
                        items.add(StyledRenderable("${it.name}, position: ${it.position}", "Promotion relegation", true, Color.LTGRAY))
                    }

                    items.add(StyledRenderable("${it.team.name} statistics", null, true))
                    it.statistics?.forEach {
                        items.add(SimpleRenderable(it.toString()))
                    }
                }
            }
        }

        return items
    }
}

class SeasonRankingTablePanel : SeasonDetailsPanel() {

    override val loadingParams = DetailsLoadingProperties.SeasonRankingTable

    override fun createFullItems(details: AnyTeamSeasonDetails): List<Renderable>? {
        val items = mutableListOf<Renderable>()

        (details as? RankingSeasonDetails<*, *>)?.let { details ->

            if (details.rankingTables != null && details.rankingTables!!.isNotEmpty()) items.add(StyledRenderable("Ranking tables", null, true, Color.LTGRAY))

            details.rankingTables?.forEach { rankingTable ->

                items.add(StyledRenderable(rankingTable.name, "Group name: ${rankingTable.groupName ?: "/"}", true, Color.LTGRAY))
                items.add(StyledRenderable(rankingTable.competition?.name ?: "", "Competition", true, Color.LTGRAY))

                rankingTable.tableItems.forEach {

                    items.add(StyledRenderable("${it.team.name} ranking table", null, true, Color.LTGRAY))

                    it.promotionRelegation?.let {
                        items.add(StyledRenderable("${it.name}, position: ${it.position}", "Promotion relegation", true, Color.LTGRAY))
                    }

                    items.add(StyledRenderable("${it.team.name} statistics", null, true))
                    it.statistics?.forEach {
                        items.add(SimpleRenderable(it.toString()))
                    }
                }
            }
        }

        return items
    }
}

abstract class SeasonStatisticsPanel : SeasonDetailsPanel() {

    protected fun getStatsItems(teamSeasonStatsList: List<PlayerTeamSeasonStats<*, *, *>>): List<Renderable> {
        val items = mutableListOf<Renderable>()

        teamSeasonStatsList.forEach { teamSeasonStats ->
            items.add(StyledRenderable(teamSeasonStats.player.name, null, true))
            items.add(StyledRenderable("Total stats", null, true))

            teamSeasonStats.totalStats?.forEach { stat ->
                items.add(SimpleRenderable(stat.toString()))
            }

            items.add(StyledRenderable("Team stats", null, true))

            teamSeasonStats.teamStats?.forEach { (team, stats) ->
                items.add(team.toRenderable(this))
                items.addAll(stats.map { SimpleRenderable(it.toString()) })
            }
        }

        return items
    }
}

class SeasonPlayerStatisticsPanel : SeasonDetailsPanel() {

    override val loadingParams = DetailsLoadingProperties.SeasonPlayerStatistics

    override fun createFullItems(details: AnyTeamSeasonDetails): List<Renderable>? {
        val items = mutableListOf<Renderable>()

        (details as? GoalsCardsAssistsSeasonDetails<*, *, *, *>)?.let { details ->
            details.playerStats?.let { statsMapping ->
                if (statsMapping.isNotEmpty()) items.add(StyledRenderable("Player statistics", null, true, Color.LTGRAY))

                statsMapping.forEach { (player, stats) ->
                    items.add(StyledRenderable(player.name, null, true))
                    items.addAll(stats.map { SimpleRenderable(it.toString()) })
                }
            }
        }

        return items
    }

}

class SeasonGoalsStatisticsPanel : SeasonStatisticsPanel() {

    override val loadingParams = DetailsLoadingProperties.SeasonGoalsStatistics

    override fun createFullItems(details: AnyTeamSeasonDetails): List<Renderable>? {
        val items = mutableListOf<Renderable>()

        (details as? GoalBasedSeasonStatistics<*, *, *>)?.let { details ->
            details.goals?.let { playerTeamSeasonStatsList ->
                if (playerTeamSeasonStatsList.isNotEmpty())  items.add(StyledRenderable("Goals statistics", null, true, Color.LTGRAY))
                items.addAll(getStatsItems(playerTeamSeasonStatsList))
            }

            details.ownGoals?.let { playerTeamSeasonStatsList ->
                if (playerTeamSeasonStatsList.isNotEmpty()) items.add(StyledRenderable("Own goals statistics", null, true, Color.LTGRAY))
                items.addAll(getStatsItems(playerTeamSeasonStatsList))
            }
        }

        return items
    }
}

class SeasonCardsStatisticsPanel : SeasonStatisticsPanel() {

    override val loadingParams = DetailsLoadingProperties.SeasonCardsStatistics

    override fun createFullItems(details: AnyTeamSeasonDetails): List<Renderable>? {
        val items = mutableListOf<Renderable>()

        (details as? CardBasedSeasonStatistics<*, *, *>)?.let { details ->
            details.cards?.let { playerTeamSeasonStatsList ->
                if (playerTeamSeasonStatsList.isNotEmpty()) items.add(StyledRenderable("Cards statistics", null, true, Color.LTGRAY))
                items.addAll(getStatsItems(playerTeamSeasonStatsList))
            }
        }

        return items
    }
}

class SeasonAssistsStatisticsPanel : SeasonStatisticsPanel() {

    override val loadingParams = DetailsLoadingProperties.SeasonAssistsStatistics

    override fun createFullItems(details: AnyTeamSeasonDetails): List<Renderable>? {
        val items = mutableListOf<Renderable>()

        (details as? AssistBasedSeasonStatistics<*, *, *>)?.let { details ->
            details.assists?.let { playerTeamSeasonStatsList ->
                if (playerTeamSeasonStatsList.isNotEmpty()) items.add(StyledRenderable("Assists statistics", null, true, Color.LTGRAY))
                items.addAll(getStatsItems(playerTeamSeasonStatsList))
            }
        }

        return items
    }
}