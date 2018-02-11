package demo

import ag.sportradar.sdk.android.AndroidConfig
import ag.sportradar.sdk.android.SRSDK
import ag.sportradar.sdk.core.model.AnySportType
import ag.sportradar.sdk.core.model.Season
import ag.sportradar.sdk.sports.model.motostport.AnyMotorsportRace
import ag.sportradar.sdk.sports.model.soccer.Soccer
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import java.text.SimpleDateFormat
import java.util.*

class DemoApp : Application() {

    val sdk: SRSDK by lazy { SRSDK(AndroidConfig("957efda931e34cf0874cc120b7dff06b", "en", TimeZone.getDefault(), ""), this) }

    val dateFormat by lazy { SimpleDateFormat("dd. MM. yyyy", Locale.US) }
    val dateFormatLong by lazy { SimpleDateFormat("hh:mm, dd. MM. yyyy", Locale.US) }

    var selectedSport: AnySportType = Soccer
    var sportSelected = false

    val allSports = mutableListOf<AnySportType>()

    //
    // region temporary data storage
    //

    /**
     * These models are not serializable, parcelable or can be trivially fetched,
     * which is the reason for these setters and getters. Every consumer activity/fragment
     * resets the reference to null upon consuming to prevent memory leaks (provided the object is consumed).
     */

    private var selectedSeason: Season? = null

    fun setSelectedSeason(season: Season) {
        selectedSeason = season
    }

    fun consumeSelectedSeason(): Season {
        val temp = selectedSeason ?: TODO("Selected season not set")
        selectedSeason = null
        return temp
    }

    private var selectedRace: AnyMotorsportRace? = null

    fun setSelectedRace(race: AnyMotorsportRace) {
        selectedRace = race
    }

    fun consumeSelectedRace(): AnyMotorsportRace {
        val temp = selectedRace ?: TODO("Selected race not set")
        selectedRace = null
        return temp
    }

    //
    // endregion
    //

    @SuppressLint("ResourceType")
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
