package demo.matchdetails

import demo.base.BasePanel
import demo.utils.*
import ag.sportradar.sdk.core.model.HandicapOddsOutcome
import ag.sportradar.sdk.core.model.OverUnderOddsOutcome
import ag.sportradar.sdk.core.model.ThreeWayOddsOutcome
import ag.sportradar.sdk.core.model.teammodels.*
import ag.sportradar.sdk.sports.model.tennis.TennisMatch
import android.graphics.Color

/**
 * Created by rokoblak on 1/10/18.
 */
abstract class MatchPanel : BasePanel<AnyMatchDetails>() {
    protected val match by lazy { trackable as AnyTeamMatch }
}

class OddsPanel : MatchPanel() {

    override val loadingParams = DetailsLoadingProperties.Odds

    override fun createFullItems(details: AnyMatchDetails): List<Renderable>? {
        val items = mutableListOf<Renderable>()

        details.odds?.forEach { odds ->
            val outcomes = odds.outcomes
            val oddsText = when (outcomes) {
                is ThreeWayOddsOutcome -> "Home: ${outcomes.home}, draw: ${outcomes.draw}, away: ${outcomes.away}"
                is OverUnderOddsOutcome -> "Total: ${outcomes.total}, over: ${outcomes.over}, under: ${outcomes.under}"
                is HandicapOddsOutcome -> "score: ${outcomes.score}, home: ${outcomes.home}, draw: ${outcomes.draw}, away: ${outcomes.away}"
                else -> ""
            }
            items.add(StyledRenderable("${odds.marketType} ${if (odds.active) "(active)" else "(inactive)"}", oddsText, true))
        }

        return items
    }

}

class TeamTablesPanel : MatchPanel() {

    override val loadingParams = DetailsLoadingProperties.TeamTables

    override fun createFullItems(details: AnyMatchDetails): List<Renderable>? {
        val items = mutableListOf<Renderable>()

        (details as? LineupsRankingMatchDetails<*, *, *>)?.let {
            it.team1LeagueTable?.let {
                items.add(StyledRenderable("${match.team1} league table", null, true))
                it.statistics?.forEach {
                    items.add(SimpleRenderable(it.toString()))
                }
            }
            it.team2LeagueTable?.let {
                items.add(StyledRenderable("${match.team2} league table", null, true))
                it.statistics?.forEach {
                    items.add(SimpleRenderable(it.toString()))
                }
            }
        }

        return items
    }
}

class TimelinePanel : MatchPanel() {

    override val loadingParams = DetailsLoadingProperties.Events

    override fun createFullItems(details: AnyMatchDetails): List<Renderable>? {
        return details.events?.map { it.toRenderable() }
    }

    override fun createDiffItems(details: AnyMatchDetails): List<Renderable>? {
        return details.events?.map { it.toRenderable(true) }
    }
}

class LineupsPanel : MatchPanel() {

    override val loadingParams = DetailsLoadingProperties.Lineups

    override fun createFullItems(details: AnyMatchDetails): List<Renderable>? {
        val lineups = (details as? LineupsRankingMatchDetails<*, *, *>)?.lineups ?: return null
        val data = mutableListOf<Renderable>()
        lineups.team1?.let { data.addAll(createLineupsItems(it, match.team1.name)) }
        lineups.team2?.let { data.addAll(createLineupsItems(it, match.team2.name)) }
        return data
    }

    private fun createLineupsItems(lineup: AnyTeamLineup, title: String): List<Renderable> {
        val data = mutableListOf<Renderable>()
        lineup.manager?.let {
            data.add(StyledRenderable("$title manager : ${it.fullname} (${it.nationality?.name})", null, true))
        }

        lineup.startinglineup.takeIf { it.isNotEmpty() }?.let {
            data.add(StyledRenderable("$title starting lineup:", null, true))
            it.forEach { player ->
                data.add(player.toRenderable(context!!))
            }
        }

        lineup.substitutes.takeIf { it.isNotEmpty() }?.let {
            data.add(StyledRenderable("$title substitutions:", null, true))
            it.forEach { player ->
                data.add(player.toRenderable(context!!))
            }
        }
        return data
    }
}

class StatisticsPanel : MatchPanel() {

    override val loadingParams = DetailsLoadingProperties.Statistics

    override fun createFullItems(details: AnyMatchDetails): List<Renderable>? {
        val stats = details.statistics
        val periodStats = details.periodStatistics

        if ((stats == null || stats.count() == 0) && (periodStats == null || periodStats.count() == 0)) return null

        val data = mutableListOf<Renderable>()

        data.add(StyledRenderable("Total statistics", null, true, Color.GRAY))
        data.add(StyledRenderable(match.team1.name, null, true, Color.LTGRAY))
        stats?.get(match.team1)?.forEach { stat ->
            data.add(stat.toRenderable())
        }
        data.add(StyledRenderable(match.team2.name, null, true, Color.LTGRAY))
        stats?.get(match.team2)?.forEach { stat ->
            data.add(stat.toRenderable())
        }

        if (periodStats != null && periodStats.count() != 0) {
            data.add(StyledRenderable("Period statistics", null, true, Color.GRAY))
            periodStats.entries.sortedBy { it.key }.forEach { (pIdx, stats) ->
                data.add(StyledRenderable("Period $pIdx", null, true, Color.WHITE))
                data.add(StyledRenderable("Team 1", null, true, Color.LTGRAY))
                stats[match.team1]?.forEach { stat ->
                    data.add(stat.toRenderable())
                }
                data.add(StyledRenderable("Team 2", null, true, Color.LTGRAY))
                stats[match.team2]?.forEach { stat ->
                    data.add(stat.toRenderable())
                }
            }
        }

        return data
    }

    override fun createDiffItems(details: AnyMatchDetails): List<Renderable>? {
        val items = mutableListOf<Renderable>()

        val stats = details.statistics
        stats?.get(match.team1)?.forEach { stat ->
            items.add(SimpleRenderable("UPDATED: T1 " + stat.toString()))
        }

        stats?.get(match.team2)?.forEach { stat ->
            items.add(SimpleRenderable("UPDATED: T2 " + stat.toString()))
        }

        val periodStats = details.periodStatistics
        if (periodStats != null && periodStats.count() != 0) {
            periodStats.entries.sortedBy { it.key }.forEach { (pIdx, stats) ->
                stats[match.team1]?.forEach { stat ->
                    items.add(SimpleRenderable("PS$pIdx UPDATED: T1 " + stat.toString()))
                }
                stats[match.team2]?.forEach { stat ->
                    items.add(SimpleRenderable("PS$pIdx UPDATED: T2 " + stat.toString()))
                }
            }
        }

        return items
    }
}

class MatchInfoPanel : MatchPanel() {

    override val loadingParams = DetailsLoadingProperties.MatchInfo

    override fun createFullItems(details: AnyMatchDetails): List<Renderable>? {

        val data = mutableListOf<Renderable>()

        match.score?.periodScores?.let { periodScores ->
            data.add(StyledRenderable("Period scores", null, true))
            periodScores.forEach { periodScore ->
                data.add(SimpleRenderable("${periodScore.type.name}: ${periodScore.score}"))
            }
        }

        data.add(StyledRenderable("Current period index: ${match.currentPeriodIdx}", null, false))

        (match as? TennisMatch)?.let { tennisMatch ->
            data.add(StyledRenderable("Court name: ${tennisMatch.courtName}", null, true))
        }

        data.add(StyledRenderable(details.venue?.name ?: "/", "Venue", true))
        data.add(StyledRenderable(details.attendance?.toString() ?: "/", "Attendance", true))

        (details as? RankingMatchDetails<*, *>)?.staff?.let { staff ->
            data.add(StyledRenderable(staff.referee?.toString() ?: "/", "Referee", true))
            data.add(StyledRenderable(staff.team1Manager?.toString() ?: "/", "${match.team1.name} manager", true))
            data.add(StyledRenderable(staff.team2Manager?.toString() ?: "/", "${match.team2.name} manager", true))
        }

        return data
    }
}
