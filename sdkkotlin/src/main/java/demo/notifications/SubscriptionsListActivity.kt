package demo.notifications

import demo.sdkkotlin.R
import demo.base.BaseActivity
import demo.matchdetails.MatchDetailsActivity
import demo.stagedetails.StageDetailsActivity
import demo.teamdetails.TeamDetailsActivity
import demo.tournamentdetails.TournamentDetailsActivity
import demo.utils.*
import ag.sportradar.sdk.android.notifications.NotificationEventType
import ag.sportradar.sdk.core.loadable.Callback
import ag.sportradar.sdk.core.loadable.CallbackHandler
import ag.sportradar.sdk.core.model.AnyContestType
import ag.sportradar.sdk.core.model.Sport
import ag.sportradar.sdk.core.model.SportRadarModel
import ag.sportradar.sdk.core.model.Stage
import ag.sportradar.sdk.core.model.subscribable.NotificationSubscribable
import ag.sportradar.sdk.core.model.subscribable.NotificationSubscription
import ag.sportradar.sdk.core.model.subscribable.NotificationTag
import ag.sportradar.sdk.core.model.teammodels.*
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.jetbrains.anko.find
import org.jetbrains.anko.toast

/**
 * Lists the subscriptions to various models and fetches these models for identification
 */
class SubscriptionsListActivity : BaseActivity() {

    override val contentResourceId = R.layout.activity_subscriptions_list

    private val recycler by lazy { find<RecyclerView>(R.id.recycler_subscriptions).apply {
        layoutManager = LinearLayoutManager(this@SubscriptionsListActivity, LinearLayoutManager.VERTICAL, false)
    } }

    private val adapter = GenericAdapter()

    override fun initUI(content: ViewGroup) {
        recycler.adapter = adapter
    }

    private var totalSubscriptionModels = 0

    private var subscriptionModelsFetched = 0

    private var subscriptionMapping: MutableMap<NotificationTag, MutableMap<SportRadarModel, List<NotificationEventType>>> = mutableMapOf()

    /**
     * Fetch the subscriptions and then the models corresponding to them
     */
    override fun loadData() {
        val handler = sdk.notificationsController.getAllSubscriptions(object : Callback<Map<NotificationTag, Map<NotificationSubscription, List<NotificationEventType>>>> {
            override fun onSuccess(result: Map<NotificationTag, Map<NotificationSubscription, List<NotificationEventType>>>?) {
                subscriptionModelsFetched = 0
                totalSubscriptionModels = result?.values?.sumBy { it.size } ?: 0
                subscriptionMapping.clear()

                if (result == null || result.isEmpty()) {
                    showEmpty("No subscriptions")
                } else {
                    result.forEach { (tag, mapping) ->
                        mapping.forEach { (sub, eventTypes) ->
                            fetchSubscriptionModel(tag, sub, eventTypes)
                        }
                    }
                }
            }
            override fun onFailure(t: Throwable) {
                t.printStackTrace()
                showEmpty("Failure: ${t.message}")
            }
        })
        handlers.add(handler)
    }

    /**
     * Create the subscriptions items
     */
    private fun createItemsIfReady() {
        subscriptionModelsFetched++
        if (subscriptionModelsFetched >= totalSubscriptionModels) {
            val data = mutableListOf<Renderable>()

            data.add(object : StyledRenderable("Remove all subscriptions", "Removes all the subscriptions", true) {
                override val clickListener: (() -> Unit)? = {
                    val removeAllHandler = sdk.notificationsController.removeAllSubscriptions(object : Callback<Boolean> {
                        override fun onSuccess(result: Boolean?) {
                            adapter.clear()
                            showLoading()
                            loadData()
                        }

                        override fun onFailure(t: Throwable) {
                            toast("Failure: ${t.message}")
                            t.printStackTrace()
                        }
                    })
                    handlers.add(removeAllHandler)
                }
            })

            subscriptionMapping.forEach {  (tag, mapping) ->
                data.add(StyledRenderable(tag.name, null,true, Color.LTGRAY))
                mapping.forEach { (model, eventTypes) ->
                    val context = this
                    data.add(object : StyledRenderable("$model (ID = ${model.id})", eventTypes.joinToString(", ") { it.friendlyName() }, true) {
                        override val clickListener = {
                            when (model) {
                                is AnyTeamMatch -> MatchDetailsActivity.newInstance(context, model)
                                is AnyTeamType -> TeamDetailsActivity.newInstance(context, model)
                                is AnyTeamStage -> StageDetailsActivity.newInstance(context, model)
                                is AnyTeamTournament -> TournamentDetailsActivity.newInstance(context, model)
                            }
                        }
                    })
                    data.add(createUnsubscribeItem("Unsubscribe", model as NotificationSubscribable, eventTypes.toSet()))
                }
            }
            if (data.isNotEmpty()) {
                adapter.setNewItems(data)
                showContent()
            } else {
                showEmpty("No subscriptions (this should not happen)")
            }
        }
    }

    /**
     * Creates an item that removes a model's subscription when clicked
     */
    private fun createUnsubscribeItem(title: String, model: NotificationSubscribable, eventTypes: Set<NotificationEventType>): Renderable {
        return object : StyledRenderable(title, null, true) {
            override val clickListener: (() -> Unit)? = {
                showLoading()
                val handler = sdk.notificationsController.unsubscribe(model, eventTypes, object : Callback<Boolean> {
                    override fun onSuccess(result: Boolean?) {
                        adapter.clear()
                        loadData()
                    }
                    override fun onFailure(t: Throwable) {
                        toast("Failure: ${t.message}")
                        t.printStackTrace()
                    }
                })

                handlers.add(handler)
            }
        }
    }

    /**
     * Fetch the model corresponding to a subscription
     */
    private fun fetchSubscriptionModel(tag: NotificationTag, subscription: NotificationSubscription, eventTypes: List<NotificationEventType>) {
        val sport = subscription.sport ?: return
        val mapping = subscriptionMapping.getOrPut(tag, { mutableMapOf() })

        val callback = object : Callback<SportRadarModel> {
            override fun onSuccess(result: SportRadarModel?) {
                result?.let { model ->
                    mapping.put(model, eventTypes)
                }
                createItemsIfReady()
            }

            override fun onFailure(t: Throwable) {
                createItemsIfReady()
                t.printStackTrace()
                toast("Failure: ${t.message}")
            }
        }

        val handler: CallbackHandler = when (tag) {
            NotificationTag.Match -> {
                sdk.contestsController.getById(subscription.id, callback as Callback<AnyContestType>)
            }
            NotificationTag.Team -> {
                sdk.contesterController.getContesterById(subscription.id, sport, Team::class.java, callback as Callback<AnyTeamType?>)
            }
            NotificationTag.Stage -> {
                sdk.competitionController.getStageById(subscription.id, sport as Sport<*, Stage, *, *, *>, callback as Callback<Stage?>)
            }
            NotificationTag.Tournament -> {
                sdk.competitionController.getTournamentById(subscription.id, subscription.sport as Sport<*, *, AnyTeamTournament, *, *>, callback as Callback<AnyTeamTournament?>)
            }
        }
        handlers.add(handler)
    }
}
