package com.goodwy.dialer.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.goodwy.dialer.activities.CallActivity
import com.goodwy.dialer.helpers.CallManager

class DynamicIslandReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_ANSWER = "com.miui.dynamicisland.ACTION_ANSWER"
        const val ACTION_DECLINE = "com.miui.dynamicisland.ACTION_DECLINE"
        const val ACTION_END = "com.miui.dynamicisland.ACTION_END"
        const val ACTION_OPEN_CALL = "com.miui.dynamicisland.ACTION_OPEN_CALL"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_ANSWER -> CallManager.accept()
            ACTION_DECLINE, ACTION_END -> CallManager.reject()
            ACTION_OPEN_CALL -> {
                val callIntent = CallActivity.getStartIntent(context)
                context.startActivity(callIntent)
            }
        }
    }
}
