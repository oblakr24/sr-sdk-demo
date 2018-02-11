package demo.matchlist

import demo.sdkkotlin.R
import demo.base.*
import demo.matchdetails.MatchDetailsActivity
import demo.motorsports.MotorsportStageDetailsActivity
import demo.utils.safeFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import demo.views.MatchView
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

class MatchListAdapter : GroupItemAdapter<GenericGroupItem, ChildItem, GroupHolder<GenericGroupItem>, ChildHolder<ChildItem>>() {

    override fun onCreateChildHolder(parent: ViewGroup, viewType: Int): ChildHolder<ChildItem> {
        return when (viewType) {
            ChildItemType.Match.id -> MatchChildHolder(parent.inflateChild(R.layout.holder_match))
            ChildItemType.MotorsportStage.id -> MotorsportStageChildHolder(parent.inflateChild(R.layout.holder_motorsport_stage))
            else -> GenericChildHolder(parent.inflateChild(R.layout.holder_generic))
        } as ChildHolder<ChildItem>
    }

    override fun onCreateGroupHolder(parent: ViewGroup, viewType: Int): GroupHolder<GenericGroupItem> =
            GenericGroupHolder(parent.inflateChild(R.layout.holder_generic))

    class MotorsportStageChildHolder(val view: View) : ChildHolder<MotorsportStageChildItem>(view) {

        private val nameText by lazy { view.find<TextView>(R.id.txt_name) }
        private val statusText by lazy { view.find<TextView>(R.id.txt_status) }
        private val timeText by lazy { view.find<TextView>(R.id.txt_time) }

        private val infoText by lazy { view.find<TextView>(R.id.text_info) }

        private val dateFormat by lazy { SimpleDateFormat("HH:mm", Locale.US) }


        override fun populateChild(data: MotorsportStageChildItem) {
            val stage = data.stage

            nameText.text = stage.name
            statusText.text = stage.status?.name
            timeText.text = stage.time?.getCountingString()
            infoText.text = "${dateFormat.safeFormat(stage.startDate?.time)} - ${dateFormat.safeFormat(stage.endDate?.time)}"
        }

        override val clickListener: ((data: MotorsportStageChildItem, pos: Int) -> Unit)? = { data, _ ->
            MotorsportStageDetailsActivity.newInstance(view.context, data.stage)
        }
    }

    class MatchChildHolder(override val view: View) : ChildHolder<MatchChildItem>(view), MatchView {

        override val team1 by lazy { view.find<TextView>(R.id.txt_team_1) }
        override val team2 by lazy { view.find<TextView>(R.id.txt_team_2) }

        override val score by lazy { view.find<TextView>(R.id.txt_score) }
        override val statusText by lazy { view.find<TextView>(R.id.txt_status) }
        override val time by lazy { view.find<TextView>(R.id.txt_time) }
        override val liveText by lazy { view.find<TextView>(R.id.txt_live) }

        override val infoText by lazy { view.find<TextView>(R.id.text_info) }

        override val dateFormat by lazy { SimpleDateFormat("HH:mm", Locale.US) }

        override fun populateChild(data: MatchChildItem) {
            populateMatch(data.match)
        }

        override val clickListener: ((data: MatchChildItem, pos: Int) -> Unit)? = { data, _ ->
            MatchDetailsActivity.newInstance(view.context, data.match)
        }
    }

    private fun ViewGroup.inflateChild(resourceId: Int, attach: Boolean = false): View {
        return LayoutInflater.from(context).inflate(resourceId, this, attach)
    }
}
