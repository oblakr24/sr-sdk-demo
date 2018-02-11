package demo.teamdetails

import demo.base.*
import demo.notifications.FavouriteProvider
import demo.notifications.SubscribableProvider
import demo.notifications.SubscriptionsFragment
import demo.utils.PanelBuilder
import ag.sportradar.sdk.core.loadable.Callback
import ag.sportradar.sdk.core.loadable.OneTimeLoadable
import ag.sportradar.sdk.core.model.teammodels.AnyTeamDetails
import ag.sportradar.sdk.core.model.teammodels.AnyTeamType
import ag.sportradar.sdk.mdp.request.favourites.FavouriteTag
import ag.sportradar.sdk.sports.model.tennis.Tennis
import org.jetbrains.anko.startActivity

/**
 * Tasked with providing the details of a team
 */
class TeamDetailsActivity : BasePagerActivity(), SubscribableProvider, FavouriteProvider, PanelModelProvider<AnyTeamDetails> {

    lateinit var team: AnyTeamType

    override val panels by lazy {
        when (app.selectedSport) {
            Tennis -> listOf(
                        PanelBuilder({ LastNextMatchesPanel() }, "Last / next matches"),
                        PanelBuilder({ TeamInfoPanel() }, "Info"),
                        PanelBuilder({ SubscriptionsFragment() }, "Subscriptions")
                        )
            else -> listOf(
                        PanelBuilder({ LastNextMatchesPanel() }, "Last / next matches"),
                        PanelBuilder({ TeamInfoPanel() }, "Info"),
                        PanelBuilder({ TeamSquadPanel() }, "Squad"),
                        PanelBuilder({ SubscriptionsFragment() }, "Subscriptions")
                        )
        }
    }

    override val subscribable by lazy { team }

    override val favouriteTag = FavouriteTag.Team

    override val favouriteId by lazy { team.id }

    override fun getLoadable() = team as OneTimeLoadable<AnyTeamDetails>

    override fun loadData() {
        sdk.contesterController.getTeamById(modelId, object : Callback<AnyTeamType?> {
            override fun onSuccess(result: AnyTeamType?) {
                if (result != null) {
                    team = result
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
        fun newInstance(activity: BaseActivity, team: AnyTeamType) {
            activity.app.selectedSport = team.sport
            activity.startActivity<TeamDetailsActivity>(Extras.Title to "${team.name} details", Extras.ModelId to team.id)
        }
        fun newInstance(fragment: BaseFragment, team: AnyTeamType) {
            fragment.app.selectedSport = team.sport
            fragment.context?.startActivity<TeamDetailsActivity>(Extras.Title to "${team.name} details", Extras.ModelId to team.id)
        }
    }
}
