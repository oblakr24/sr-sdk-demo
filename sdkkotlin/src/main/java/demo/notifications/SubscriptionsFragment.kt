package demo.notifications

import demo.sdkkotlin.R
import demo.base.BaseFragment
import demo.utils.inflateChild
import ag.sportradar.sdk.android.notifications.NotificationEventType
import ag.sportradar.sdk.core.loadable.Callback
import ag.sportradar.sdk.core.model.subscribable.NotificationSubscribable
import ag.sportradar.sdk.http.response.EmptyResponse
import ag.sportradar.sdk.mdp.request.favourites.FavouriteTag
import ag.sportradar.sdk.mdp.request.favourites.FavouritesMappingResponse
import ag.sportradar.sdk.mdp.request.favourites.FavouritesResponse
import android.view.View
import android.view.ViewGroup
import android.widget.*
import demo.views.NotificationIcon
import org.jetbrains.anko.dip
import org.jetbrains.anko.find
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.space
import org.jetbrains.anko.support.v4.toast

/**
 * Created by rokoblak on 1/11/18.
 * Shows the existing and possible subscriptions for a model, as well as a favourite toggle
 */
class SubscriptionsFragment : BaseFragment() {

    override val layoutResourceId = R.layout.layout_fragment_subscriptions

    private val subscribedNotifications = mutableMapOf<NotificationEventType, Boolean>()

    private val icons = mutableListOf<NotificationIcon>()

    private val favouritedText by lazy { layout.find<TextView>(R.id.text_favourited) }

    private val favouritedProgress by lazy { layout.find<ProgressBar>(R.id.favourite_progress) }

    private var isFavourited = false

    private val subscribable: NotificationSubscribable by lazy { (activity as SubscribableProvider).subscribable }

    private val favouriteId by lazy { (activity as FavouriteProvider).favouriteId }

    private val favouriteTag by lazy { (activity as FavouriteProvider).favouriteTag }

    override fun initUI(content: ViewGroup) { }

    /**
     * Favourites or un-favourites a model
     */
    private fun toggleFavourite(favourite: Boolean) {
        val handler = if (favourite) {
            sdk.favouritesController.addFavouriteId(favouriteId, favouriteTag, object : Callback<FavouritesResponse> {
                override fun onSuccess(result: FavouritesResponse?) {
                    isFavourited = result?.favouriteIds?.contains(subscribable.id) ?: false
                    updateFavouritedText()
                }

                override fun onFailure(t: Throwable) {
                    t.printStackTrace()
                    updateFavouritedText(t.message)
                }
            })
        } else {
            sdk.favouritesController.removeFavouriteId(favouriteId, favouriteTag, object : Callback<EmptyResponse> {
                override fun onSuccess(result: EmptyResponse?) {
                    isFavourited = false
                    updateFavouritedText()
                }

                override fun onFailure(t: Throwable) {
                    t.printStackTrace()
                    updateFavouritedText(t.message)
                }
            })
        }
        handlers.add(handler)
    }

    private fun updateFavouritedText(message: String? = "Favourited: $isFavourited") {
        favouritedText.visibility = View.VISIBLE
        favouritedProgress.visibility = View.GONE
        favouritedText.text = message
    }

    override fun loadData() {
        showContent()

        // fetch the favourite IDs
        val favHandler = sdk.favouritesController.getFavouriteIds(object : Callback<FavouritesMappingResponse> {
            override fun onSuccess(result: FavouritesMappingResponse?) {
                isFavourited = result?.favorites?.get(favouriteTag)?.contains(subscribable.id) ?: false
                favouritedText.text = "Favourited: $isFavourited"
                favouritedText.visibility = View.VISIBLE
                favouritedProgress.visibility = View.GONE
                favouritedText.setOnClickListener {
                    favouritedText.visibility = View.GONE
                    favouritedProgress.visibility = View.VISIBLE
                    toggleFavourite(!isFavourited)
                }
            }

            override fun onFailure(t: Throwable) {
                t.printStackTrace()
            }
        })
        handlers.add(favHandler)

        val validEventTypes = sdk.notificationsController.getEventTypesForSport(app.selectedSport)

        // fetch the subscription types for a model
        val handler = sdk.notificationsController.getSubscriptionTypes(subscribable, object : Callback<List<NotificationEventType>> {
            override fun onSuccess(result: List<NotificationEventType>?) {
                result?.map { it to true }?.let { subscribedNotifications.putAll(it) }
                // update the current state
                icons.forEach { it.updateState(subscribedNotifications) }  // update the views
            }

            override fun onFailure(t: Throwable) {
                showEmpty()
                t.printStackTrace()
            }
        })

        handlers.add(handler)

        // setup the icons
        for (eventType in validEventTypes) {
            contentView.addView(contentView.inflateChild(R.layout.item_notifications).apply {
                val titleText = find<TextView>(R.id.title_notification)
                val icon = find<ImageView>(R.id.icon_notification)
                val progressBar = find<ProgressBar>(R.id.notification_progress)

                titleText.text = eventType.friendlyName()

                icons.add(NotificationIcon(icon, progressBar, eventType, false, {
                    showLoading()
                    updateSubscription(eventType, isSubscribed, { updateState(subscribedNotifications) })
                }))
            })

            contentView.apply {
                space().apply {
                    layoutParams.width = matchParent
                    layoutParams.height = dip(10)
                }
            }
        }
    }

    /**
     * Add or remove a subscription
     */
    private fun updateSubscription(eventType: NotificationEventType, unsubscribe: Boolean, onFinish: ((success: Boolean) -> Unit)) {
        val callback = object : Callback<Boolean> {
            override fun onSuccess(result: Boolean?) {
                subscribedNotifications[eventType] = !unsubscribe /* update to the new state */
                toast("Subscription ${if (unsubscribe) "removed" else "added"} successfully")
                onFinish(true)
            }

            override fun onFailure(t: Throwable) {
                toast("Subscription failed")
                t.printStackTrace()
                onFinish(false)
            }
        }

        val handler = if (unsubscribe) {
            sdk.notificationsController.unsubscribe(subscribable, setOf(eventType), callback)
        } else {
            sdk.notificationsController.subscribe(subscribable, eventType, 0L, callback)
        }

        handlers.add(handler)
    }
}

/**
 * Implemented by activities providing a notification subscribable
 */
interface SubscribableProvider {
    val subscribable: NotificationSubscribable
}

/**
 * Implemented by activities providing a model that can be favourited
 */
interface FavouriteProvider {
    val favouriteTag: FavouriteTag
    val favouriteId: Long
}