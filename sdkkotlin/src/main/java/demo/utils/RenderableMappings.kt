package demo.utils

import demo.sdkkotlin.R
import demo.DemoApp
import demo.base.BaseActivity
import demo.base.BaseFragment
import demo.motorsports.MotorsportSeasonDetailsActivity
import demo.motorsports.MotorsportStageDetailsActivity
import demo.matchdetails.MatchDetailsActivity
import demo.motorsports.MotorsportRaceDetailsActivity
import demo.playerdetails.PlayerDetailsActivity
import demo.seasondetails.SeasonDetailsActivity
import demo.stagedetails.StageDetailsActivity
import demo.teamdetails.TeamDetailsActivity
import demo.tournamentdetails.TournamentDetailsActivity
import ag.sportradar.sdk.core.model.Category
import ag.sportradar.sdk.core.model.Event
import ag.sportradar.sdk.core.model.teammodels.*
import ag.sportradar.sdk.core.model.teammodels.statistics.MatchStatistics
import ag.sportradar.sdk.sports.model.*
import ag.sportradar.sdk.sports.model.motostport.AnyMotorsportRace
import ag.sportradar.sdk.sports.model.motostport.AnyMotorsportSeason
import ag.sportradar.sdk.sports.model.motostport.AnyMotorsportStage
import ag.sportradar.sdk.sports.model.motostport.MotorsportDriver
import ag.sportradar.sdk.sports.model.motostport.formulaone.*
import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import java.text.SimpleDateFormat

/**
 * Created by rokoblak on 1/12/18.
 * A mapping of used models to the renderables so that they can be displayed with a generic adapter
 */

fun MatchStatistics.toRenderable() = SimpleRenderable("${toString()} ($statType)")

fun Event<*>.toRenderable(updated: Boolean = false): Renderable {
    val updatedMark = if (updated) "UPDATED: " else ""
    val minutes = contestTime?.minutes?.takeIf { it >= 0 }?.let { "$it\" - " } ?: ""
    return if (this is PlayerEvent<*>) {
        StyledRenderable("$updatedMark$minutes $name (${contester?.name ?: "/"})", "player: ${player?.name ?: "/"}", true)
    } else {
        SimpleRenderable("$updatedMark$minutes $name (${contester?.name ?: "/"})")
    }
}

fun FormulaOneRace.toStyledRenderable(activity: BaseActivity, dateFormat: SimpleDateFormat) = object : StyledRenderable(name, "Circuit: ${circuit?.name}, winner: ${(winner?.name ?: "/")}, status: ${(status?.name ?: "/")}", true) {
    override val clickListener = {
        MotorsportRaceDetailsActivity.newInstance(activity, this@toStyledRenderable)
    }
}

fun AnyMotorsportRace.toStyledRenderable(activity: BaseActivity, dateFormat: SimpleDateFormat) = (this as? FormulaOneRace)?.toStyledRenderable(activity, dateFormat) ?: object : StyledRenderable(name, "Winner: ${(winner?.name ?: "/")}", true) {
    override val clickListener = {
        MotorsportRaceDetailsActivity.newInstance(activity, this@toStyledRenderable)
    }
}

fun AnyMotorsportStage.toStyledRenderable(context: Context, dateFormat: SimpleDateFormat) = object : StyledRenderable(name,
        "${((this as? FormulaOneRaceStage)?.circuit?.name)?.let { "$it, " } ?: ""}${dateFormat.safeFormat(startDate?.time)} - ${dateFormat.safeFormat(endDate?.time)}", true) {
    override val clickListener = {
        MotorsportStageDetailsActivity.newInstance(context, this@toStyledRenderable)
    }
}

fun FormulaOneTeam.toStyledRenderable() = StyledRenderable("$name ${country?.name}", "(pos: ${currentPosition?.position}, pts: ${currentPosition?.points})", true)

fun MotorsportDriver.toStyledRenderable(titleBold: Boolean = true) = (this as? FormulaOneDriver)?.toStyledRenderable(titleBold) ?: StyledRenderable("$name (${nationality?.name})", null, titleBold)

fun FormulaOneDriver.toStyledRenderable(titleBold: Boolean = true) = StyledRenderable("$name (${nationality?.name})", "${team?.name} (${team?.country?.name})", true)

fun AnyMotorsportSeason.toStyledRenderable(context: Context) = (this as? FormulaOneSeason)?.toStyledRenderable(context) ?: object : StyledRenderable("$name (${status?.name})", "Winner: ${winner?.name ?: "/"}", true) {
    override val clickListener = {
        MotorsportSeasonDetailsActivity.newInstance(context, this@toStyledRenderable)
    }
}

fun FormulaOneSeason.toStyledRenderable(context: Context) = object : StyledRenderable("$name (${status?.name})", "Winner: ${winner?.name ?: "/"} (${winner?.team?.name ?: "/"})", true) {
    override val clickListener = {
        MotorsportSeasonDetailsActivity.newInstance(context, this@toStyledRenderable)
    }
}

fun TeamPlayerRole<*>.toRenderable(dateFormat: SimpleDateFormat) = StyledRenderable(name, "${team.name} (${dateFormat.safeFormat(startDate?.time)} - ${dateFormat.safeFormat(endDate?.time)})", true)

fun AnyTeamPlayer.toRenderable(context: Context) = object : SimpleRenderable("$name (${nationality?.name})") {
    override val clickListener = {
        PlayerDetailsActivity.newInstance(context, this@toRenderable)
    }
}

fun AnyTeamMatch.toRenderable(context: Context) = object : SimpleRenderable(toString()) {
    override val clickListener = {
        MatchDetailsActivity.newInstance(context, this@toRenderable)
    }
}

fun AnyTeamPlayer.toStyledRenderable(context: Context, lastRole: String) = object : StyledRenderable("${fullname.takeIf { it.isNotBlank() } ?: name} (${nationality?.name ?: "/"})", lastRole, true) {
    override val clickListener = {
        PlayerDetailsActivity.newInstance(context, this@toStyledRenderable)
    }
}

fun AnyTeamStage.toStyledRenderable(context: Context) = object : StyledRenderable(name, "Stage", true, ContextCompat.getColor(context, R.color.colorGrayVeryLight)) {
    override val clickListener = {
        StageDetailsActivity.newInstance(context, this@toStyledRenderable)
    }
}

fun AnyTeamStage.toRenderable(context: Context) = object : SimpleRenderable(name) {
    override val clickListener = {
        StageDetailsActivity.newInstance(context, this@toRenderable)
    }
}

fun AnyTeamTournament.toRenderable(context: Context) = object : SimpleRenderable(name) {
    override val clickListener = {
        TournamentDetailsActivity.newInstance(context, this@toRenderable)
    }
}

fun AnyTeamTournament.toStyledRenderable(context: Context, desc: String? = "Tournament") = object : StyledRenderable(name, desc, true, ContextCompat.getColor(context, R.color.colorGrayLight)) {
    override val clickListener = {
        TournamentDetailsActivity.newInstance(context, this@toStyledRenderable)
    }
}

fun AnyTeamType.toRenderable(activity: BaseActivity) = object : SimpleRenderable(name) {
    override val clickListener = {
        TeamDetailsActivity.newInstance(activity, this@toRenderable)
    }
}

fun AnyTeamType.toRenderable(fragment: BaseFragment) = object : SimpleRenderable(name) {
    override val clickListener = {
        TeamDetailsActivity.newInstance(fragment, this@toRenderable)
    }
}

fun AnyTeamSeason.toRenderable(application: DemoApp) = object : SimpleRenderable(name) {
    override val clickListener = {
        SeasonDetailsActivity.newInstance(application, this@toRenderable)
    }
}

fun AnyTeamSeason.toStyledRenderable(application: DemoApp) = object : StyledRenderable(name, "Season", true) {
    override val clickListener = {
        SeasonDetailsActivity.newInstance(application, this@toStyledRenderable)
    }
}

fun Category.toStyledRenderable(): StyledRenderable {
    var displayName = name
    if (country != null && country?.name != name) {
        displayName += " ${country?.name}"
    }
    return StyledRenderable(displayName, "Category", true, Color.parseColor("#84b3ff"))
}