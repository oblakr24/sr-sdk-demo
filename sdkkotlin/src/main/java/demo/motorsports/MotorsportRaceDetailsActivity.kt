package demo.motorsports

import demo.base.BaseActivity
import demo.base.BasePagerActivity
import demo.base.BasePanel
import demo.base.Extras
import demo.utils.*
import ag.sportradar.sdk.core.model.ModelDetails
import ag.sportradar.sdk.sports.model.motostport.AnyMotorsportRace
import ag.sportradar.sdk.sports.model.motostport.formulaone.FormulaOneRace
import android.view.ViewGroup
import org.jetbrains.anko.startActivity

/**
 * Tasked with providing the details of a motorsport race
 */
class MotorsportRaceDetailsActivity : BasePagerActivity() {

    val race by lazy { app.consumeSelectedRace() }

    override var hasInitialData = true

    override val panels: List<PanelBuilder> by lazy {
        listOf(
                PanelBuilder({ MotorsportRaceInfoPanel() }, "Info"),
                PanelBuilder({ MotorsportRaceStatsPanel() }, "Stats"),
                PanelBuilder({ MotorsportRaceScoresPanel() }, "Scores")
        )
    }

    override fun loadData() {
        showContent(false)
    }

    companion object {
        fun newInstance(activity: BaseActivity, race: AnyMotorsportRace) {
            activity.app.setSelectedRace(race)
            activity.startActivity<MotorsportRaceDetailsActivity>(Extras.Title to race.name)
        }
    }
}

abstract class MotorsportRaceDetailsPanel : BasePanel<ModelDetails>() {

    override var hasInitialData = true

    override fun initUI(content: ViewGroup) {
        super.initUI(content)
        val items = createItems((activity as MotorsportRaceDetailsActivity).race)
        if (items.isEmpty()) {
            showEmpty()
        } else {
            adapter.setNewItems(items)
            showContent(false)
        }
    }

    override fun loadData() {}

    protected abstract fun createItems(race: AnyMotorsportRace): List<Renderable>
}

class MotorsportRaceInfoPanel : MotorsportRaceDetailsPanel() {

    override fun createItems(race: AnyMotorsportRace): List<Renderable> {
        val items = mutableListOf<Renderable>()

        race.status?.let { status ->
            items.add(StyledRenderable(status.toString(), "Status", true))
        }

        (race as? FormulaOneRace)?.let { f1Race ->
            f1Race.circuit?.let {
                items.add(StyledRenderable(it.name, "Circuit", true))
            }

            f1Race.laps?.let {
                items.add(StyledRenderable(it.name, it.statisticsValue.toString(), true))
            }

            f1Race.lapsCompleted?.let {
                items.add(StyledRenderable(it.name, it.statisticsValue.toString(), true))
            }

            f1Race.weatherInfo?.let { weatherInfo ->
                items.add(StyledRenderable("Weather info", null, true))
                weatherInfo.weather?.let {
                    items.add(SimpleRenderable("Weather: $it"))
                }
                weatherInfo.airTemperature?.let {
                    items.add(SimpleRenderable("Air temperature: $it"))
                }
                weatherInfo.trackTemperature?.let {
                    items.add(SimpleRenderable("Track temperature: $it"))
                }
                weatherInfo.humidity?.let {
                    items.add(SimpleRenderable("Humidity: $it"))
                }
            }
        }

        race.startTime?.time?.let { startDate ->
            items.add(StyledRenderable(app.dateFormatLong.format(startDate), "Start date", true))
        }

        race.competitors.takeIf { it.isNotEmpty() }?.let { drivers ->
            items.add(StyledRenderable("Drivers", null, true))
            items.addAll(drivers.map { it.toStyledRenderable(false) })
        }

        return items
    }
}

class MotorsportRaceStatsPanel : MotorsportRaceDetailsPanel() {

    override fun createItems(race: AnyMotorsportRace): List<Renderable> {
        val items = mutableListOf<Renderable>()
        race.driverStatistics?.forEach { (driver, stats) ->
            items.add(driver.toStyledRenderable())
            stats.forEach { stat ->
                items.add(SimpleRenderable(stat.toString()))
            }
        }
        return items
    }
}

class MotorsportRaceScoresPanel : MotorsportRaceDetailsPanel() {

    override fun createItems(race: AnyMotorsportRace): List<Renderable> {
        val items = mutableListOf<Renderable>()
        race.raceScores?.let { score ->
            score.winner?.let { winner ->
                items.add(StyledRenderable(winner.name, "Winner", true))
            }

            score.results?.forEach { (driver, pos) ->
                items.add(SimpleRenderable("${driver.name} - position: ${pos.position}, points: ${(pos.points ?: "/")}"))
            }
        }
        return items
    }
}
