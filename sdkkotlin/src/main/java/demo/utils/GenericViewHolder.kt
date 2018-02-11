package demo.utils

import demo.sdkkotlin.R
import demo.base.BaseViewHolder
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import org.jetbrains.anko.find

/**
 * Created by rokoblak on 1/10/18.
 */

/**
 * A base model for use in adapters,
 * meant for other models to map to
 */
interface Renderable {
    val title: String
    val description: String?
    val clickListener: (() -> Unit)?
    val style: RenderableStyle
}

open class RenderableStyle(val titleBold: Boolean, val bgColor: Int)

object DefaultRenderableStyle : RenderableStyle(false, Color.WHITE)

open class SimpleRenderable(override val title: String, override val description: String? = null, override val clickListener: (() -> Unit)? = null) : Renderable {
    override val style: RenderableStyle = DefaultRenderableStyle
}

open class StyledRenderable(override val title: String, override val description: String? = null, private val titleBold: Boolean, private val bgColor: Int = Color.WHITE, override val clickListener: (() -> Unit)? = null) : Renderable {
    override val style = RenderableStyle(titleBold, bgColor)
}

class GenericViewHolder(view: View) : BaseViewHolder<Renderable>(view), GenericTitleDescView {

    override val background: View by lazy { itemView }
    override val titleText by lazy { view.find<TextView>(R.id.text_generic_title) }
    override val descriptionText by lazy { view.find<TextView>(R.id.text_generic_description) }
    override val infoIcon by lazy { view.find<ImageView>(R.id.icon_info) }

    override fun populateView(data: Renderable, position: Int) {
        populate(data)
    }
}

/**
 * Base view for use in adapters providing a title, description and an info icon
 */
interface GenericTitleDescView {

    val background: View
    val titleText: TextView
    val descriptionText: TextView
    val infoIcon: ImageView

    fun populate(data: Renderable) {
        titleText.text = data.title
        if (data.description != null) {
            descriptionText.text = data.description
            descriptionText.visibility = View.VISIBLE
        } else {
            descriptionText.visibility = View.GONE
        }

        val style = data.style
        titleText.typeface = if (style.titleBold) {
            Typeface.DEFAULT_BOLD
        } else {
            Typeface.DEFAULT
        }

        infoIcon.visibility = if (data.clickListener != null) {
            View.VISIBLE
        } else {
            View.GONE
        }

        background.setBackgroundColor(style.bgColor)
    }
}