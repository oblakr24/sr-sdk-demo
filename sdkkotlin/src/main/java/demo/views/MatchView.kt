package demo.views

import demo.sdkkotlin.R
import ag.sportradar.sdk.core.model.teammodels.AnyStagedMatch
import ag.sportradar.sdk.core.model.teammodels.AnyTeamMatch
import ag.sportradar.sdk.core.model.teammodels.AnyTeamType
import ag.sportradar.sdk.sports.model.basketball.Basketball
import ag.sportradar.sdk.sports.model.icehockey.IceHockey
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import demo.base.BaseViewHolder
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*


/**
 * Shows the basic information about a match
 */
interface MatchView {

    val view: View
    val team1: TextView
    val team2: TextView

    val score: TextView
    val statusText: TextView
    val time: TextView
    val liveText: TextView

    val infoText: TextView

    val dateFormat: SimpleDateFormat

    fun populateMatch(match: AnyTeamMatch) {
        team1.text = match.team1.name
        team2.text = match.team2.name

        val scoreStr = match.score?.toString()
        if (scoreStr != null) {
            score.text = scoreStr
            score.visibility = View.VISIBLE
        } else {
            score.visibility = View.GONE
        }

        val isSportWithTimeStartStop = when (match.sport) {
            IceHockey, Basketball -> true
            else -> false
        }

        // crests
        team1.loadCrest(match.team1, true)
        team2.loadCrest(match.team2, false)

        if (match.live) {
            liveText.visibility = View.VISIBLE
            statusText.backgroundDrawable?.setColorFilter(ContextCompat.getColor(view.context, R.color.colorLive), PorterDuff.Mode.MULTIPLY)
            statusText.visibility = View.VISIBLE
            time.text = if (match.time?.isRunning == true || !isSportWithTimeStartStop) "${match.time?.getCountingString()}" else "${match.time?.getCountingString()} (not running)"
            time.visibility = View.VISIBLE
        } else {
            liveText.visibility = View.GONE
            statusText.backgroundDrawable?.setColorFilter(null)
            if (match.status?.isNotStarted == true) {
                time.text = "Starts at ${match.startTime?.time?.let { dateFormat.format(it) } ?: "/"}"
                statusText.visibility = View.GONE
                time.visibility = View.VISIBLE
            } else {
                time.visibility = View.GONE
                statusText.visibility = View.VISIBLE
            }
        }
        statusText.text = match.status?.name

        val stageText = (match as? AnyStagedMatch)?.stage?.name?.let { "$it | " } ?: ""
        val tournamentText = match.tournament.name
        val categoryText = match.category.name

        infoText.text = "$stageText$tournamentText | $categoryText | ${match.sport.name}"
    }

    private fun TextView.loadCrest(team: AnyTeamType, isLeft: Boolean) {
        Glide.with(this)
                .load("https://ls.betradar.com/ls/crest/medium/${team.id}.png")
                .apply(RequestOptions().error(R.drawable.ico_crest_placeholder))
                .transition(withCrossFade())
                .into(createDrawableTarget(isLeft))
    }

    private fun TextView.createDrawableTarget(isLeft: Boolean): SimpleTarget<Drawable> {
        return object : SimpleTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable?, transition: Transition<in Drawable>?) {
                if (resource?.intrinsicWidth ?: 0 > 1 && resource?.intrinsicHeight ?: 0 > 1) {
                    if (isLeft) {
                        this@createDrawableTarget.setCompoundDrawablesWithIntrinsicBounds(resource, null, null, null)
                    } else {
                        this@createDrawableTarget.setCompoundDrawablesWithIntrinsicBounds(null, null, resource, null)
                    }
                } else {
                    if (isLeft) {
                        this@createDrawableTarget.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ico_crest_placeholder, 0, 0, 0)
                    } else {
                        this@createDrawableTarget.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ico_crest_placeholder, 0)
                    }
                }
            }
        }
    }
}

class MatchViewHolder(view: View) : BaseViewHolder<AnyTeamMatch>(view), MatchView {
    override val view by lazy { itemView }
    override val team1 by lazy { view.find<TextView>(R.id.txt_team_1) }
    override val team2 by lazy { view.find<TextView>(R.id.txt_team_2) }

    override val score by lazy { view.find<TextView>(R.id.txt_score) }
    override val statusText by lazy { view.find<TextView>(R.id.txt_status) }
    override val time by lazy { view.find<TextView>(R.id.txt_time) }
    override val liveText by lazy { view.find<TextView>(R.id.txt_live) }

    override val infoText by lazy { view.find<TextView>(R.id.text_info) }

    override val dateFormat by lazy { SimpleDateFormat("HH:mm", Locale.US) }

    override fun populateView(match: AnyTeamMatch, position: Int) {
        populateMatch(match)
    }

    fun populate(match: AnyTeamMatch, teamClickListener: ((AnyTeamType) -> Unit)? = null) {
        populateMatch(match)
        teamClickListener?.let {
            team1.setOnClickListener { it(match.team1) }
            team2.setOnClickListener { it(match.team2) }
        }
    }
}