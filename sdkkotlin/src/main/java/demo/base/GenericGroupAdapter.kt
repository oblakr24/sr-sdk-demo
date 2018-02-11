package demo.base

import demo.sdkkotlin.R
import demo.utils.GenericTitleDescView
import demo.utils.Renderable
import ag.sportradar.sdk.core.model.teammodels.AnyTeamMatch
import ag.sportradar.sdk.sports.model.motostport.AnyMotorsportStage
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.jetbrains.anko.find

class GenericGroupAdapter : GroupItemAdapter<GenericGroupItem, ChildItem, GroupHolder<GenericGroupItem>, ChildHolder<ChildItem>>() {

    override fun onCreateChildHolder(parent: ViewGroup, viewType: Int): GenericChildHolder {
        return GenericChildHolder(parent.inflateChild(R.layout.holder_generic))
    }

    override fun onCreateGroupHolder(parent: ViewGroup, viewType: Int): GenericGroupHolder =
            GenericGroupHolder(parent.inflateChild(R.layout.holder_generic))


    private fun ViewGroup.inflateChild(resourceId: Int, attach: Boolean = false): View {
        return LayoutInflater.from(context).inflate(resourceId, this, attach)
    }
}

class GenericGroupHolder(val layout: View) : GroupHolder<GenericGroupItem>(layout), GenericTitleDescView {

    override val background: View by lazy { itemView }
    override val titleText by lazy { layout.find<TextView>(R.id.text_generic_title) }
    override val descriptionText by lazy { layout.find<TextView>(R.id.text_generic_description) }
    override val infoIcon by lazy { layout.find<ImageView>(R.id.icon_info) }

    override fun populateGroup(data: GenericGroupItem, groupPosition: Int) {
        populate(data.renderable)
    }

    override val clickListener: ((data: GenericGroupItem, pos: Int) -> Unit)? = { data, _ ->
        data.renderable.clickListener?.invoke()
    }
}

class GenericChildHolder(val view: View) : ChildHolder<GenericChildItem>(view), GenericTitleDescView {

    override val background: View by lazy { itemView }
    override val titleText by lazy { view.find<TextView>(R.id.text_generic_title) }
    override val descriptionText by lazy { view.find<TextView>(R.id.text_generic_description) }
    override val infoIcon by lazy { view.find<ImageView>(R.id.icon_info) }

    override fun populateChild(data: GenericChildItem) {
        populate(data.renderable)
    }

    override val clickListener: ((data: GenericChildItem, pos: Int) -> Unit)? = { data, _ ->
        data.renderable.clickListener?.invoke()
    }
}

/**
 * Child item type enums - favouriteId should always be above 1
 */
enum class ChildItemType(val id: Int) {
    Generic(1),
    Match(2),
    MotorsportStage(3)
}

/**
 * Group item type enums - favouriteId should always be above 1
 */
enum class GroupItemType(val id: Int) {
    Generic(1)
}

class GenericGroupItem(val renderable: Renderable) : GroupItem<ChildItem>(GroupItemType.Generic.id)
class GenericChildItem(val renderable: Renderable) : ChildItem(ChildItemType.Generic.id)
class MatchChildItem(val match: AnyTeamMatch) : ChildItem(ChildItemType.Match.id)
class MotorsportStageChildItem(val stage: AnyMotorsportStage) : ChildItem(ChildItemType.MotorsportStage.id)
