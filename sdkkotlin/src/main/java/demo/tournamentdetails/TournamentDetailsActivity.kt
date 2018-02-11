package demo.tournamentdetails

import demo.base.BasePagerActivity
import demo.base.Extras
import demo.base.PanelModelProvider
import demo.notifications.FavouriteProvider
import demo.notifications.SubscribableProvider
import demo.notifications.SubscriptionsFragment
import demo.utils.PanelBuilder
import ag.sportradar.sdk.core.loadable.Callback
import ag.sportradar.sdk.core.loadable.OneTimeLoadable
import ag.sportradar.sdk.core.model.Sport
import ag.sportradar.sdk.core.model.teammodels.AnyTeamTournament
import ag.sportradar.sdk.core.model.teammodels.AnyTeamTournamentDetails
import ag.sportradar.sdk.mdp.request.favourites.FavouriteTag
import android.content.Context

import org.jetbrains.anko.startActivity

/**
 * Tasked with providing the details of a tournament
 */
class TournamentDetailsActivity : BasePagerActivity(), SubscribableProvider, FavouriteProvider, PanelModelProvider<AnyTeamTournamentDetails> {

    lateinit var tournament: AnyTeamTournament

    override val subscribable by lazy { tournament }

    override val favouriteTag = FavouriteTag.Tournament

    override val favouriteId by lazy { tournament.id }

    override val panels by lazy {
        listOf(
                PanelBuilder({ TournamentSeasonsPanel() }, "Seasons"),
                PanelBuilder({ TournamentTeamsPanel() }, "Teams"),
                PanelBuilder({ SubscriptionsFragment() }, "Subscriptions")
        )
    }

    override fun getLoadable() = tournament as OneTimeLoadable<AnyTeamTournamentDetails>

    override fun loadData() {
        sdk.competitionController.getTournamentById(modelId, app.selectedSport as Sport<*, *, AnyTeamTournament, *, *>, object : Callback<AnyTeamTournament?> {
            override fun onSuccess(result: AnyTeamTournament?) {
                if (result != null) {
                    tournament = result
                    showContent()
                } else {
                    showEmpty()
                }
            }

            override fun onFailure(t: Throwable) {
                t.printStackTrace()
                showEmpty()
            }
        })
    }

    companion object {
        fun newInstance(context: Context, tournament: AnyTeamTournament) {
            context.startActivity<TournamentDetailsActivity>(Extras.ModelId to tournament.id, Extras.Title to "${tournament.name} details")
        }
    }
}
