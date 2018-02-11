package demo.base

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Filter

/**
 * Created by rokoblak on 11/1/16.
 * A base adapter storing items that are either groups or their children,
 * where groups can be expanded or collapsed.
 * Also supports filtering of groups and their children.
 */

abstract class GroupHolder<in TDataG>(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun populateGroup(data: TDataG, groupPosition: Int)
    open val clickListener: ((data: TDataG, pos: Int) -> Unit)? = null
}

abstract class ChildHolder<TDataC>(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun populateChild(data: TDataC)
    open val clickListener: ((data: TDataC, pos: Int) -> Unit)? = null
}

open class GroupItem<TDataC : ChildItem>(groupItemType: Int) : DataItem(groupItemType) {
    val childItems: MutableList<TDataC> = mutableListOf()
    var expanded: Boolean = false
    var alwaysShow: Boolean = false

    override val isTopLevelitem = true

    fun addChild(child: TDataC) {
        childItems.add(child)
    }

    fun getVisibleChildren() = childItems.filter { it.isShown }
}

open class ChildItem(childViewType: Int, val nonStandalone: Boolean = false, val ignoreFiltering: Boolean = false) : DataItem(childViewType) {
    override val isTopLevelitem = false
}

abstract class DataItem(val itemType: Int) {
    var isShown = true /* modified by the filter */

    abstract val isTopLevelitem: Boolean
}

abstract class GroupItemAdapter<TGroup : GroupItem<out TChild>, TChild : ChildItem,
        out TGroupHolder : GroupHolder<TGroup>, out TChildHolder : ChildHolder<TChild>> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //
    // region Members
    //

    private var footerResourceId = 0 // the resource favouriteId of a footer view

    private val groupItems: MutableList<TGroup> = ArrayList()

    /* called when the filtering finishes */
    private var filterString = ""
    private val filter: BaseFilter<TGroup, TChild> = BaseFilter(groupItems) {
        updateGroups(filterString.isNotEmpty())
        visibleItemsCallback?.invoke(displayedItems.size)
    }

    private val displayedItems: MutableList<DataItem> = ArrayList()

    var groupPredicate: ((data: TGroup, charSequence: CharSequence) -> Boolean)? = null
        set(value) {
            filter.groupPredicate = value
        }

    var childPredicate: ((data: TChild, charSequence: CharSequence) -> Boolean)? = null
        set(value) {
            filter.childPredicate = value
        }

    var visibleItemsCallback: ((Int) -> Unit)? = null

    /* refresh the visible item without filtering */
    fun refresh(skipFiltering: Boolean = false, filterString: String = "") {
        if (skipFiltering) {
            groupItems.forEach {
                // reset the visibilities
                it.isShown = true
                it.childItems.forEach { it.isShown = true }
            }
            updateGroups()
            visibleItemsCallback?.invoke(displayedItems.size)
        } else {
            this.filterString = filterString
            filter(null)
        }
    }

    fun filter(charSequence: CharSequence?) {
        if (groupItems.count() == 0) return
        filter.filter(charSequence)
    }

    fun updateGroups(expand: Boolean? = null) {
        updateVisibleItems(groupItems, expand)
    }

    fun clear() {
        groupItems.clear()
        displayedItems.clear()
        notifyDataSetChanged()
    }

    //
    // endregion
    //

    //
    // region Overrides
    //

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        /* nothing to bind on the footer - ignore */
        if (position == displayedItems.count()) return

        val item = displayedItems[position]

        if (item is GroupItem<*>) {
            val groupHolder = holder as TGroupHolder
            val groupItem = item as TGroup
            val groupPosition = displayedItems.filter { it is GroupItem<*> }.indexOf(groupItem)

            groupHolder.populateGroup(groupItem, groupPosition)

            groupHolder.itemView.setOnClickListener {
                if (groupItem.expanded) {
                    collapseGroup(groupItem)
                } else {
                    expandGroup(groupItem)
                }

                /* trigger the custom click listeners */
                groupHolder.clickListener?.invoke(groupItem, position)
            }

        } else if (item is ChildItem) {
            val childHolder = holder as TChildHolder
            val childItem = item as TChild
            childHolder.populateChild(item)

            /* trigger the custom click listeners */
            if (childHolder.clickListener != null) {
                childHolder.itemView.setOnClickListener {
                    childHolder.clickListener?.invoke(childItem, position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType < 0) {
            onCreateGroupHolder(parent, -viewType)
        } else {
            onCreateChildHolder(parent, viewType)
        }
    }

    //
    // endregion
    //

    //
    // region Private
    //

    private fun hasFooter(): Boolean = footerResourceId != 0

    //
    // endregion
    //

    //
    // region Abstract
    //

    abstract fun onCreateGroupHolder(parent: ViewGroup, viewType: Int): TGroupHolder

    abstract fun onCreateChildHolder(parent: ViewGroup, viewType: Int): ChildHolder<out TChild> // not constrained to TChildHolder by default to enable different TChild types

    //
    // endregion Abstract
    //

    override fun getItemCount() = displayedItems.count() + if (hasFooter()) 1 else 0

    override fun getItemViewType(position: Int): Int {
        val item = displayedItems[position]
        if (item.itemType < 1) {
            throw RuntimeException("Item type for $item should be above 1")
        }
        // negative for top level items
        return if (item.isTopLevelitem) {
            -item.itemType
        } else {
            item.itemType
        }
    }

    //
    // region Item spans
    //

    /* override to return custom span for child items with the given number of columns */
    open fun getChildSpan(viewType: Int, columns: Int): Int = columns

    /* override to return custom span for group items with the given number of columns */
    open fun getGroupSpan(viewType: Int, columns: Int): Int = columns

    fun getItemSpan(position: Int, columns: Int): Int {
        if (position == displayedItems.count()) return columns // footer
        val item = displayedItems[position]
        return if (item is GroupItem<*>) {
            getGroupSpan(item.itemType, columns)
        } else {
            getChildSpan(item.itemType, columns)
        }
    }

    //
    // endregion
    //

    fun setNewGroups(groups: List<TGroup>, expand: Boolean? = null) {
        groupItems.clear()
        groupItems.addAll(groups)
        updateVisibleItems(groups, expand)
    }

    // gets a copy
    fun getGroups() = groupItems.toList()

    fun getExpandedGroups(): List<TGroup> = groupItems.filter { it.expanded }

    private fun updateVisibleItems(groups: List<TGroup>, forceExpand: Boolean? = null) {
        displayedItems.clear()
        /* over the visible groups */
        for (group in groups.filter { it.isShown }) {
            displayedItems.add(group)
            when {
                forceExpand ?: group.expanded -> {
                    group.expanded = true
                    displayedItems.addAll(group.getVisibleChildren())
                }
                else -> group.expanded = false
            }
        }
        notifyDataSetChanged()
    }

    class BaseFilter<TGroup : GroupItem<out TChild>, TChild : ChildItem>(val allGroups: List<TGroup>, val onFilteringFinishedDelegate: (() -> Unit)) : Filter() {

        var groupPredicate: ((TGroup, charSequence: CharSequence) -> Boolean)? = null
        var childPredicate: ((TChild, charSequence: CharSequence) -> Boolean)? = null

        /* iterates through the standalone items next of the given index and returns true if at least one is visible */
        private fun hasVisibleStandaloneNeighbor(idx: Int, items: List<TChild>): Boolean {
            for (i in idx + 1 until items.count()) {
                if (!items[i].isShown) continue
                return !items[i].nonStandalone
            }
            return false
        }

        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            /* go over the group items and call the corresponding filtering function */
            for (group in allGroups) {
                /* only consider the children who get to be filtered */
                val childItems = group.childItems.filter { !it.ignoreFiltering }

                /* only keep the matches among the children */
                childItems.forEach { it.isShown = childPredicate?.invoke(it, charSequence ?: "") ?: true }
                /* a non-standalone item cannot remain visible if does not have a visible standalone neighbor */
                childItems.zip(0..childItems.count())
                        .filter { it.first.nonStandalone }
                        .map { it.first.isShown = hasVisibleStandaloneNeighbor(it.second, childItems) }

                /* whether any of the children are visible after applying their predicate */
                val anyChildrenVisible = childItems.any { it.isShown }

                /* check whether the group itself matches ONLY if the group predicate is set, otherwise consider it matching */
                var groupMatch = anyChildrenVisible
                if (groupPredicate != null) {
                    /* the group needs to match the predicate and there have to be visible children */
                    groupMatch = group.alwaysShow || (anyChildrenVisible || groupPredicate?.invoke(group, charSequence ?: "") ?: false)
                } else {
                    /* by default, the group always matches the predicate */
                }
                if (groupMatch && !anyChildrenVisible) {
                    childItems.forEach { it.isShown = true }
                }
                group.isShown = groupMatch
            }
            return FilterResults()
        }

        override fun publishResults(constraint: CharSequence?, filterResults: FilterResults?) {
            /* ignore the result and inform the adapter via the delegate */
            onFilteringFinishedDelegate()
        }
    }

    //
    // region Expand / Collapse functionality
    //

    private fun expandGroup(group: TGroup) {
        if (group.expanded) return

        val displayedPos = displayedItems.indexOf(group)
        /* only the filtered items */
        val visibleChildren = group.getVisibleChildren()

        displayedItems.addAll(displayedPos + 1, visibleChildren)

        /* notify about the newly inserted children and about the changed group item */
        notifyItemRangeInserted(displayedPos + 1, visibleChildren.count())
        notifyItemChanged(displayedPos)

        group.expanded = true
    }

    private fun collapseGroup(group: TGroup) {
        if (!group.expanded) return

        val displayedPos = displayedItems.indexOf(group)

        /* count only the filtered items */
        val visibleChildCount = group.getVisibleChildren().size
        displayedItems.subList(displayedPos + 1, displayedPos + 1 + visibleChildCount).clear()

        /* notify about the removed children and about the changed group item */
        notifyItemRangeRemoved(displayedPos + 1, visibleChildCount)
        notifyItemChanged(displayedPos)

        group.expanded = false
    }

    //
    // endregion
    //
}