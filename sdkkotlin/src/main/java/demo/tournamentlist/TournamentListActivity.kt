package demo.tournamentlist

import demo.sdkkotlin.R
import demo.base.BaseActivity
import demo.base.GenericChildItem
import demo.base.GenericGroupAdapter
import demo.base.GenericGroupItem
import demo.utils.*
import ag.sportradar.sdk.core.loadable.Callback
import ag.sportradar.sdk.core.model.Category
import ag.sportradar.sdk.core.model.Sport
import ag.sportradar.sdk.core.model.Stage
import ag.sportradar.sdk.core.model.teammodels.AnyTeamStage
import ag.sportradar.sdk.core.model.teammodels.AnyTeamTournament
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import org.jetbrains.anko.find

/**
 * Displays the tournament structure of the selected sport
 */
class TournamentListActivity : BaseActivity() {

    override val contentResourceId = R.layout.activity_tournament_list

    private val recycler by lazy { find<RecyclerView>(R.id.recycler_tournaments).apply {
        layoutManager = LinearLayoutManager(this@TournamentListActivity, LinearLayoutManager.VERTICAL, false)
    } }

    private val adapter = GenericGroupAdapter()

    private val cancelText by lazy { find<TextView>(R.id.cancel) }

    private val searchText by lazy { find<EditText>(R.id.search) }

    /**
     * The tournament filter text watcher
     */
    private val textWatcher: TextWatcher by lazy {
        object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(seq: CharSequence?, start: Int, before: Int, count: Int) {
                if (seq == null) {
                    adapter.groupPredicate = null
                    adapter.childPredicate = null
                    cancelText.visibility = View.GONE
                } else {
                    adapter.groupPredicate = { group, _ ->
                        group.renderable.title.matches(seq.toString())
                    }
                    adapter.childPredicate = { child, _ ->
                        (child as? GenericChildItem)?.renderable?.title?.matches(seq.toString()) ?: false
                    }
                    adapter.refresh(filterString = seq.toString())
                    cancelText.visibility = if (seq.isEmpty()) View.GONE else View.VISIBLE
                }
            }
        }
    }

    private fun String.matches(other: String) = toLowerCase().contains(other.toLowerCase())

    override fun initUI(content: ViewGroup) {
        title = "${app.selectedSport.name} tournaments"
        cancelText.setOnClickListener {
            searchText.setText("")
            hideKeyboard()
        }
    }

    /**
     * Loads the competition structure
     */
    override fun loadData() {
        val competitionsHandler = sdk.competitionController.loadCompetitionsForSport(app.selectedSport as Sport<*, Stage, AnyTeamTournament, *, *>, object : Callback<Map<Category, Map<AnyTeamTournament, List<Stage>>>?> {
            override fun onSuccess(result: Map<Category, Map<AnyTeamTournament, List<Stage>>>?) {
                val groups = result?.map { (cat, mapping) ->
                    GenericGroupItem(cat.toStyledRenderable()).apply {
                        mapping.forEach { (tournament, stages) ->
                            addChild(GenericChildItem(tournament.toStyledRenderable(this@TournamentListActivity)))
                            stages.forEach { stage ->
                                addChild(GenericChildItem((stage as AnyTeamStage).toRenderable(this@TournamentListActivity)))
                            }
                        }
                    }
                } ?: emptyList()

                adapter.setNewGroups(groups)
                searchText.addTextChangedListener(textWatcher)
                recycler.adapter = adapter
                showContent()
            }

            override fun onFailure(t: Throwable) {
                showEmpty("Failure: ${t.message}")
                t.printStackTrace()
            }
        })

        handlers.add(competitionsHandler)
    }
}
