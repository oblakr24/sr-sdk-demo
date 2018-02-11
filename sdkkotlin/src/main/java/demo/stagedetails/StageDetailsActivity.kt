package demo.stagedetails

import demo.base.BasePagerActivity
import demo.base.Extras
import demo.base.PanelModelProvider
import demo.notifications.FavouriteProvider
import demo.notifications.SubscribableProvider
import demo.notifications.SubscriptionsFragment
import demo.utils.PanelBuilder
import ag.sportradar.sdk.core.loadable.Callback
import ag.sportradar.sdk.core.loadable.RepeatingLoadable
import ag.sportradar.sdk.core.model.Sport
import ag.sportradar.sdk.core.model.Stage
import ag.sportradar.sdk.core.model.teammodels.AnyTeamStage
import ag.sportradar.sdk.core.model.teammodels.AnyTeamStageDetails
import ag.sportradar.sdk.mdp.request.favourites.FavouriteTag
import android.content.Context
import org.jetbrains.anko.startActivity

/**
 * Tasked with providing the details of a stage
 */
class StageDetailsActivity : BasePagerActivity(), SubscribableProvider, FavouriteProvider, PanelModelProvider<AnyTeamStageDetails> {

    override val favouriteId by lazy { stage.id }

    override val subscribable by lazy { stage }

    override val favouriteTag by lazy { FavouriteTag.Stage }

    lateinit var stage: AnyTeamStage

    override val panels by lazy {
        listOf(
                PanelBuilder({ StageCupRosterPanel() }, "Cup rosters"),
//                PanelBuilder({ StageFixturesPanel() }, "Fixtures"),
                PanelBuilder({ StageLiveRankingTablePanel() }, "Live ranking table"),
                PanelBuilder({ SubscriptionsFragment() }, "Subscriptions")
        )
    }

    override fun getTrackable() = stage as RepeatingLoadable<AnyTeamStageDetails>

    override fun loadData() {
        sdk.competitionController.getStageById(modelId, app.selectedSport as Sport<*, AnyTeamStage, *, *, *>, object : Callback<AnyTeamStage?> {
            override fun onSuccess(result: AnyTeamStage?) {
                if (result != null) {
                    stage = result
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
        fun newInstance(context: Context, stage: Stage) {
            context.startActivity<StageDetailsActivity>(Extras.ModelId to stage.id, Extras.Title to "${stage.name} details")
        }
    }
}
