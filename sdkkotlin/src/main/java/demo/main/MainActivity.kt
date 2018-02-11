package demo.main

import demo.sdkkotlin.R
import demo.base.BaseActivity
import demo.favourites.FavouritesActivity
import demo.motorsports.MotorsportActivity
import demo.matchlist.MatchListPagerActivity
import demo.notifications.SubscriptionsListActivity
import demo.sportslist.SportsListActivity
import demo.tournamentlist.TournamentListActivity
import ag.sportradar.sdk.core.loadable.Callback
import ag.sportradar.sdk.core.model.AnySportType
import ag.sportradar.sdk.sports.model.motostport.formulaone.FormulaOne
import ag.sportradar.sdk.sports.model.motostport.rally.Rally
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import android.widget.TextView

/**
 * The landing activity providing navigation to specific activities and a sport-selection spinner
 */
class MainActivity : BaseActivity() {

    private val gridView by lazy { find<GridView>(R.id.main_grid) }

    private val sportsSpinner by lazy { find<Spinner>(R.id.sports_spinner) }

    override val contentResourceId = R.layout.activity_main

    /**
     * The navigation tiles
     */
    private val tiles by lazy {
        listOf(
                MainScreenElement("Match list", {
                    startActivity<MatchListPagerActivity>()
                }),
                MainScreenElement("Tournament list", {
                    startActivity<TournamentListActivity>()
                }),
                MainScreenElement("Subscriptions", {
                    startActivity<SubscriptionsListActivity>()
                }),
                MainScreenElement("Favourites", {
                    startActivity<FavouritesActivity>()
                }),
                MainScreenElement("Formula One", {
                    app.selectedSport = FormulaOne
                    startActivity<MotorsportActivity>()
                }),
                MainScreenElement("Rally", {
                    app.selectedSport = Rally
                    startActivity<MotorsportActivity>()
                }),
                MainScreenElement("Sports list", {
                    startActivity<SportsListActivity>()
                }),
                MainScreenElement("Pesapallo", {
                    // TODO: WIP
                }),
                    MainScreenElement("Tennis", {
                    // TODO: WIP
                }))
    }

    private val spinnerAdapter by lazy {
        object : ArrayAdapter<AnySportType>(this, android.R.layout.simple_spinner_dropdown_item, app.allSports) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = super.getView(position, convertView, parent) as TextView
                view.text = app.allSports[position].name
                return view
            }
        }
    }

    override fun initUI(content: ViewGroup) {
        sportsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedView: View?, position: Int, id: Long) {
                app.selectedSport = app.allSports[position]
                app.sportSelected = true
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        gridView.adapter = MainScreenGridAdapter(getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater, tiles)
    }

    override fun loadData() {
        sdk.init(object : Callback<Map<String, String>?> {
            override fun onFailure(t: Throwable) {}
            override fun onSuccess(result: Map<String, String>?) {}
        }, object : Callback<String> {
            override fun onSuccess(result: String?) {
                sdk.competitionController.loadSports(object : Callback<List<AnySportType>> {
                    override fun onSuccess(result: List<AnySportType>?) {
                        app.allSports.clear()
                        result?.let { app.allSports.addAll(it) }

                        sportsSpinner.adapter = spinnerAdapter

                        if (app.sportSelected) {
                            sportsSpinner.setSelection(app.allSports.indexOf(app.selectedSport))
                        }

                        showContent()
                    }

                    override fun onFailure(t: Throwable) {
                        t.printStackTrace()
                        showEmpty("Could not fetch sports: ${t.message}")
                    }
                })
            }
            override fun onFailure(t: Throwable) {
                t.printStackTrace()
                showEmpty("SDK start failed")
            }
        })
    }
}

/**
 * Represents a navigation tile
 */
class MainScreenElement(val title: String, val onClick: () -> Unit)

class MainScreenGridAdapter(private val inflater: LayoutInflater, val items: List<MainScreenElement>) : BaseAdapter() {

    override fun getView(pos: Int, convertView: View?, parent: ViewGroup?): View {
        val tile = convertView ?: inflater.inflate(R.layout.view_grid, null)
        tile.find<TextView>(R.id.grid_text).text = items[pos].title
        tile.setOnClickListener { items[pos].onClick() }
        return tile
    }

    override fun getItem(pos: Int) = items[pos]

    override fun getItemId(pos: Int) = items[pos].title.hashCode().toLong()

    override fun getCount() = items.size
}