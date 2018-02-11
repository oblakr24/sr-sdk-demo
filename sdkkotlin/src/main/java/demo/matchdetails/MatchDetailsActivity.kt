package demo.matchdetails

import demo.sdkkotlin.R
import demo.base.BasePagerActivity
import demo.base.Extras
import demo.base.PanelModelProvider
import demo.notifications.FavouriteProvider
import demo.notifications.SubscribableProvider
import demo.notifications.SubscriptionsFragment
import demo.teamdetails.TeamDetailsActivity
import demo.utils.DetailsLoadingProperties
import demo.utils.PanelBuilder
import ag.sportradar.sdk.core.loadable.Callback
import ag.sportradar.sdk.core.loadable.RepeatingLoadable
import ag.sportradar.sdk.core.loadable.ValueChangeCallback
import ag.sportradar.sdk.core.model.AnyContestType
import ag.sportradar.sdk.core.model.teammodels.AnyMatchDetails
import ag.sportradar.sdk.core.model.teammodels.AnyTeamMatch
import ag.sportradar.sdk.core.model.teammodels.AnyTeamType
import ag.sportradar.sdk.mdp.request.favourites.FavouriteTag
import ag.sportradar.sdk.sports.model.tennis.Tennis
import android.content.Context
import android.view.View
import demo.views.MatchViewHolder
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.util.*

/**
 * Tasked with providing the details of a match
 */
class MatchDetailsActivity : BasePagerActivity(), SubscribableProvider, FavouriteProvider, PanelModelProvider<AnyMatchDetails> {

    override val contentResourceId = R.layout.activity_match_details

    private val matchInfoBox by lazy { find<View>(R.id.match_box) }

    lateinit var match: AnyTeamMatch

    private var timerTask: TimerTask? = null

    override val subscribable by lazy { match }

    override val favouriteTag = FavouriteTag.Match

    override val favouriteId by lazy { match.id }

    override fun getTrackable() = match as RepeatingLoadable<AnyMatchDetails>

    private val holder by lazy { MatchViewHolder(matchInfoBox) }

    private val timer by lazy { Timer() }

    override val needsTracking = true

    /**
     * The panels for each of the main categories of match details
     */
    override val panels by lazy {
        when (app.selectedSport) {
            Tennis -> listOf(
                        PanelBuilder({ TimelinePanel() }, "Timeline"),
                        PanelBuilder({ MatchInfoPanel() }, "Info"),
                        PanelBuilder({ StatisticsPanel() }, "Statistics"),
                        PanelBuilder({ OddsPanel() }, "Odds"),
                        PanelBuilder({ SubscriptionsFragment() }, "Subscriptions")
            )
            else -> listOf(
                        PanelBuilder({ TimelinePanel() }, "Timeline"),
                        PanelBuilder({ TeamTablesPanel() }, "Team tables"),
                        PanelBuilder({ MatchInfoPanel() }, "Info"),
                        PanelBuilder({ StatisticsPanel() }, "Statistics"),
                        PanelBuilder({ LineupsPanel() }, "Lineups"),
                        PanelBuilder({ OddsPanel() }, "Odds"),
                        PanelBuilder({ SubscriptionsFragment() }, "Subscriptions")
            )
        }
    }

    override fun loadData() {
        if (hasInitialData) {
            // match already loaded, just the tracking needs to be restarted
            trackMatch()
            return
        }

        // fetch the match by its ID
        sdk.contestsController.getById(modelId, object : Callback<AnyContestType> {
            override fun onSuccess(result: AnyContestType?) {
                if (result == null) {
                    showEmpty("No match found for ID $modelId")
                    return
                }

                match = result as? AnyTeamMatch ?: TODO("Contest not instance of team match")

                holder.populate(match, { team: AnyTeamType ->
                    TeamDetailsActivity.newInstance(this@MatchDetailsActivity, team)
                })

                trackMatch()
                showContent()
            }

            override fun onFailure(t: Throwable) {
                showEmpty("Failure: ${t.message}")
                t.printStackTrace()
            }
        })
    }

    /**
     * Track the match by starting an updating times
     * and tracking its details (for live or upcoming matches only)
     */
    private fun trackMatch() {
        timerTask?.cancel()

        if (match.status?.isEnded == true) {
            return
        }

        timerTask = object: TimerTask() {
            override fun run() {
                runOnUiThread {
                    holder.populate(match)
                }
            }
        }
        timer.scheduleAtFixedRate(timerTask, 1000, 1000)

        // events tracking ensures we are getting the necessary updates merged into our match instance
        val handler = match.loadGenericDetails(DetailsLoadingProperties.Events, object : ValueChangeCallback<AnyMatchDetails> {
            override fun onInitialLoad(result: AnyMatchDetails?) { }
            override fun onValueChanged(diff: AnyMatchDetails) {
                holder.populate(match)
            }
            override fun onFailure(t: Throwable) {
                toast("Failure on tracking: ${t.message}")
                t.printStackTrace()
            }
        })

        handlers.add(handler)
    }

    override fun onPause() {
        super.onPause()
        timerTask?.cancel()
    }

    companion object {
        fun newInstance(context: Context, match: AnyTeamMatch) {
            val title = "${match.team1.name} vs ${match.team2.name}, ${match.startTime?.get(Calendar.DAY_OF_MONTH)}. ${(match.startTime?.get(Calendar.MONTH)?.plus(1))}. ${match.startTime?.get(Calendar.YEAR)}"
            context.startActivity<MatchDetailsActivity>(Extras.ModelId to match.id, Extras.Title to title)
        }
    }
}