package demo.playerdetails

import demo.base.BasePagerActivity
import demo.base.Extras
import demo.base.PanelModelProvider
import demo.utils.PanelBuilder
import ag.sportradar.sdk.core.loadable.Callback
import ag.sportradar.sdk.core.loadable.OneTimeLoadable
import ag.sportradar.sdk.core.model.Sport
import ag.sportradar.sdk.core.model.teammodels.AnyTeamPlayer
import ag.sportradar.sdk.core.model.teammodels.AnyTeamPlayerDetails
import android.content.Context
import org.jetbrains.anko.startActivity

/**
 * Tasked with providing the details of a player
 */
class PlayerDetailsActivity : BasePagerActivity(), PanelModelProvider<AnyTeamPlayerDetails> {

    lateinit var player: AnyTeamPlayer

    override val panels by lazy {
        listOf(
                PanelBuilder({ PlayerStatsPanel() }, "Statistics"),
                PanelBuilder({ PlayerSeasonsPanel() }, "Seasons"),
                PanelBuilder({ PlayerTeamsPanel() }, "Teams"),
                PanelBuilder({ PlayerTournamentsPanel() }, "Tournaments"),
                PanelBuilder({ PlayerRolesPanel() }, "Roles")
        )
    }

    override fun getLoadable() = player as OneTimeLoadable<AnyTeamPlayerDetails>

    override fun loadData() {
        sdk.contesterController.getPlayerById(modelId, app.selectedSport as Sport<*, *, *, AnyTeamPlayer, *>, object : Callback<AnyTeamPlayer?> {
            override fun onSuccess(result: AnyTeamPlayer?) {
                if (result == null) {
                    showEmpty()
                    return
                }
                player = result
                showContent(false)
            }

            override fun onFailure(t: Throwable) {
                t.printStackTrace()
                showEmpty()
            }
        })
    }

    companion object {
        fun newInstance(context: Context, player: AnyTeamPlayer) {
            context.startActivity<PlayerDetailsActivity>(Extras.Title to "${player.name} details", Extras.ModelId to player.id)
        }
    }
}
