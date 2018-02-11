package demo.matchlist

import demo.sdkkotlin.R
import demo.base.*
import demo.utils.*
import ag.sportradar.sdk.core.loadable.Callback
import ag.sportradar.sdk.core.loadable.ValueChangeCallback
import ag.sportradar.sdk.core.model.*
import ag.sportradar.sdk.core.model.teammodels.*
import ag.sportradar.sdk.sports.model.motostport.AnyMotorsportStage
import ag.sportradar.sdk.sports.model.motostport.formulaone.FormulaOne
import ag.sportradar.sdk.sports.model.motostport.formulaone.isOneOfMotosports
import ag.sportradar.sdk.sports.model.motostport.rally.Rally
import ag.sportradar.sdk.sports.model.tennis.Tennis
import ag.sportradar.sdk.sports.model.tennis.TennisMatch
import ag.sportradar.sdk.sports.model.tennis.TennisTournament
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.toast
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by rokoblak on 1/10/18.
 * Shows a list of matches for a given day
 */
class MatchListFragment : BaseFragment() {

    override val layoutResourceId = R.layout.layout_fragment_match_list

    private val title by lazy { layout.find<TextView>(R.id.match_list_title) }

    private val recycler by lazy { contentView.find<RecyclerView>(R.id.match_list_recycler).apply {
        layoutManager = LinearLayoutManager(this@MatchListFragment.context, LinearLayoutManager.VERTICAL, false)
    } }

    private val checkBox by lazy { layout.find<CheckBox>(R.id.checkbox_live) }

    private val cancelText by lazy { layout.find<TextView>(R.id.cancel) }

    private val searchText by lazy { layout.find<EditText>(R.id.search) }

    private var offset: Int = -1

    private var filterLive = false

    override val needsTracking = true

    private lateinit var sport: AnySportType

    private var timerTask: TimerTask? = null

    private val adapter by lazy { MatchListAdapter() }

    /**
     * The match filter text watcher
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
                        (child as? MatchChildItem)?.match?.toString()?.matches(seq.toString()) ?: false
                    }
                    adapter.refresh(filterString = seq.toString())
                    cancelText.visibility = if (seq.isEmpty()) View.GONE else View.VISIBLE
                }
            }
        }
    }

    private fun String.matches(other: String) = toLowerCase().contains(other.toLowerCase())

    override fun initUI(content: ViewGroup) {

        offset = arguments?.getInt(Extras.Offset) ?: 0
        sport = app.selectedSport

        val today = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, offset)
        }
        val format = SimpleDateFormat("EEE, d. MMM, yyyy", Locale.US)
        title.text = format.format(today.time)

        recycler.adapter = adapter

        checkBox.setOnCheckedChangeListener { _, value ->
            checkBox.text = if (value) "Filter live: ON" else "Filter live: OFF"
            filterLive = value
            adapter.clear()
            hasInitialData = false
            showLoading()
            loadData()
        }

        checkBox.visibility = if (offset == 0) {
            View.VISIBLE
        } else {
            View.GONE
        }

        cancelText.setOnClickListener {
            searchText.setText("")
            hideKeyboard()
        }
    }

    /**
     * Load the match list
     */
    override fun loadData() {
        if (offset != 0) {
            // load the matches
            val sportsArray: Array<AnySportType> = arrayOf(sport)
            val handler = app.sdk.contestsController.loadContests(offset, sportsArray, object : Callback<List<AnyContestType>> {
                override fun onSuccess(result: List<AnyContestType>?) {
                    if (hasInitialData) return  // do not reset the matches if they've already been loaded
                    val all = result ?: emptyList()
                    if (all.isNotEmpty()) {
                        adapter.setNewGroups(createMatchGroups(all as List<AnyTeamMatch>), true)
                        showContent()
                        searchText.addTextChangedListener(textWatcher)
                    } else {
                        showEmpty("No matches")
                    }
                }

                override fun onFailure(t: Throwable) {
                    t.printStackTrace()
                    showEmpty("Failure: ${t.message}")
                }
            })
            handlers.add(handler)
        } else {
            // load and track the matches
            val handler = app.sdk.contestsController.trackTodayContests(arrayOf(sport), object: ValueChangeCallback<Map<AnyContestType, List<Event<*>>>> {
                override fun onInitialLoad(result: Map<AnyContestType, List<Event<*>>>?) {
                    if (hasInitialData) return  // do not reset the matches if they've already been loaded
                    val allContests = result?.keys?.toList()?.filter { !filterLive || it.live } ?: emptyList()
                    if (allContests.isNotEmpty()) {
                        adapter.setNewGroups(createMatchGroups(allContests as List<AnyTeamMatch>), true)
                        showContent()
                        searchText.addTextChangedListener(textWatcher)
                    } else {
                        showEmpty("No matches")
                    }
                }

                override fun onValueChanged(diff: Map<AnyContestType, List<Event<*>>>) {
                    // TODO: handle motorsports
                    if (app.selectedSport.isOneOfMotosports()) return

                    val existingMatches = adapter.getGroups().flatMap { it.childItems.mapNotNull { (it as? MatchChildItem)?.match } }

                    val existingMatchesMapping = existingMatches.map { it.id to it }.toMap()
                    val diffMatches = diff.keys.map { it.id to it as AnyTeamMatch }.toMap()

                    val updatedMatches = existingMatches.map {
                        diffMatches[it.id] ?: it
                    }

                    val newMatches = diffMatches.values.filter {
                        existingMatchesMapping[it.id] == null
                    }

                    if (newMatches.isNotEmpty()) {
                        toast("Received ${newMatches.size} new matches (${updatedMatches.size} updated)")
//                        newMatches.forEach { toast("new: $it") }
                        updatedMatches.forEach { toast("updated: $it") }
                        val combined = (updatedMatches + newMatches).filter { !filterLive || it.live }
//                        adapter.setNewGroups(createMatchGroups(combined), true)
                    }
                }

                override fun onFailure(t: Throwable) {
                    t.printStackTrace()
                    showEmpty("Failure: ${t.message}")
                }
            })
            handlers.add(handler)

            // start the update timer
            timerTask?.cancel()
            timerTask = object: TimerTask() {
                override fun run() {
                    activity?.runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            Timer().scheduleAtFixedRate(timerTask, 1000, 1000)
        }
    }

    private fun getSortedMatches(matches: List<AnyContestType>) = matches.sortedBy { it.startTime?.time?.time }

    /**
     * Groups the matches based on their competitions
     */
    private fun createMatchGroups(matches: List<AnyContestType>): List<GenericGroupItem> {
        return when (app.selectedSport) {
            FormulaOne, Rally -> {
                // nothing to group by, just list
                val season = (matches as List<AnyMotorsportStage>).first().competition
                listOf(GenericGroupItem(season.toStyledRenderable(context!!)).apply {
                    matches.forEach { stage ->
                        addChild(MotorsportStageChildItem(stage))
                    }
                })
            }
            Tennis -> {
                val grouped: Map<Category, Map<TennisTournament, List<TennisMatch>>> = (matches as List<TennisMatch>).groupBy { it.category }
                        .mapValues { (_, categoryMatches) ->
                    categoryMatches.groupBy { it.tournament }.toSortedMap(compareBy { it.name })
                }.toSortedMap(compareBy { it.name })

                grouped.map { (category, tournamentMapping) ->
                    GenericGroupItem(category.toStyledRenderable()).apply {
                        tournamentMapping.forEach { (tournament, tournamentMatches) ->
                            addChild(GenericChildItem(tournament.toStyledRenderable(context!!)))
                            getSortedMatches(tournamentMatches).forEach {
                                addChild(MatchChildItem(it as TennisMatch))
                            }
                        }
                    }
                }
            }
            else -> {
                val grouped: Map<Category, Map<AnyTeamTournament, Map<AnyTeamStage, List<AnyStagedMatch>>>> = (matches as List<AnyStagedMatch>).groupBy { it.category }.mapValues { (_, categoryMatches) ->
                    categoryMatches.groupBy { it.tournament }.mapValues { (_, tournamentMatches) ->
                        tournamentMatches.groupBy { it.stage }.toSortedMap(compareBy { it.name })
                    }.toSortedMap(compareBy { it.name })
                }.toSortedMap(compareBy { it.name })

                grouped.map { (category, tournamentMapping) ->
                    GenericGroupItem(category.toStyledRenderable()).apply {
                        tournamentMapping.forEach { (tournament, groupedByStages) ->
                            addChild(GenericChildItem(tournament.toStyledRenderable(context!!)))
                            groupedByStages.forEach { (stage, matches) ->
                                addChild(GenericChildItem(stage.toStyledRenderable(context!!)))
                                getSortedMatches(matches).forEach {
                                    addChild(MatchChildItem(it as AnyTeamMatch))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        timerTask?.cancel()
    }
}