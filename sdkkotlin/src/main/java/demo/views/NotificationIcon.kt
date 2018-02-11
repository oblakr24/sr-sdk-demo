package demo.views

import demo.sdkkotlin.R
import ag.sportradar.sdk.android.notifications.NotificationEventType
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import demo.utils.fadeIn

class NotificationIcon(val iconView: ImageView, val progressBar: ProgressBar, val eventType: NotificationEventType, var isSubscribed: Boolean, val clickHandler: NotificationIcon.() -> Unit) {

    init {
        iconView.setOnClickListener { clickHandler() }
    }

    fun updateState(subscribedNotifications: Map<NotificationEventType, Boolean>) {
        progressBar.visibility = View.GONE
        iconView.fadeIn(500L)
        isSubscribed = subscribedNotifications[eventType] ?: false
        if (isSubscribed) {
            iconView.setImageResource(R.drawable.ico_notification_on)
        } else {
            iconView.setImageResource(R.drawable.ico_notification_off)
        }
    }

    fun showLoading() {
        progressBar.visibility = View.VISIBLE
        iconView.visibility = View.GONE
    }
}