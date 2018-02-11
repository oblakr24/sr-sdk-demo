package demo.utils

import demo.sdkkotlin.R
import demo.base.BasicAdapter

/**
 * Created by rokoblak on 1/12/18.
 */
class GenericAdapter : BasicAdapter<GenericViewHolder, Renderable>( { parent, _ ->
    GenericViewHolder(parent.inflateChild(R.layout.holder_generic))
}) {

    init {
        onItemClicked = { renderable, _, _ ->
            renderable.clickListener?.invoke()
        }
    }
}