package demo.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by rokoblak on 11/28/16.
 */

class NotificationBroadcastReceiver : BroadcastReceiver() {
    private val PREFS_NAME = "GcmMessageHandlerNotifications"

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("notificationId", 0)
//        val mySPrefs = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE)
//        val editor = mySPrefs.edit()
//        editor.remove(notificationId.toString())
//        editor.apply()
    }
}