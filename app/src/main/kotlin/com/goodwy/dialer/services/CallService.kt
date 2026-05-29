package com.goodwy.dialer.services

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.telecom.Call
import android.telecom.CallAudioState
import android.telecom.InCallService
import com.goodwy.commons.extensions.baseConfig
import com.goodwy.commons.extensions.canUseFullScreenIntent
import com.goodwy.commons.extensions.hasPermission
import com.goodwy.commons.helpers.PERMISSION_POST_NOTIFICATIONS
import com.goodwy.dialer.activities.CallActivity
import com.goodwy.dialer.extensions.config
import com.goodwy.dialer.extensions.getStateCompat
import com.goodwy.dialer.extensions.isOutgoing
import com.goodwy.dialer.extensions.keyguardManager
import com.goodwy.dialer.extensions.powerManager
import com.goodwy.dialer.helpers.*
import com.goodwy.dialer.models.Events
import org.greenrobot.eventbus.EventBus

class CallService : InCallService() {
    private val context = this
    private val callNotificationManager by lazy { CallNotificationManager(this) }
    private val broadcastHandler = Handler(Looper.getMainLooper())
    private var broadcastRunnable: Runnable? = null

    private val callListener = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            super.onStateChanged(call, state)

            callNotificationManager.setupNotification()

            if (state == Call.STATE_DISCONNECTED || state == Call.STATE_DISCONNECTING) {
                callNotificationManager.cancelNotification()
            }

            try {
                if (baseConfig.flashForAlerts) MyCameraImpl.newInstance(context).stopSOS()
            } catch (_: Exception) { }

            sendCallUpdateBroadcast(call)

            if (state == Call.STATE_ACTIVE) {
                startBroadcastTimer()
            } else if (state == Call.STATE_DISCONNECTED || state == Call.STATE_DISCONNECTING) {
                stopBroadcastTimer()
            }
        }
    }

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        CallManager.onCallAdded(call)
        CallManager.inCallService = this
        call.registerCallback(callListener)
        sendCallUpdateBroadcast(call)
        startBroadcastTimer()

        // Incoming/Outgoing (locked): high priority (FSI)
        // Incoming (unlocked): if user opted in, low priority ➜ manual activity start, otherwise high priority (FSI)
        // Outgoing (unlocked): low priority ➜ manual activity start
        val isOutgoing = call.isOutgoing()
        val isIncoming = !isOutgoing
        val isDeviceLocked = !powerManager.isInteractive //|| keyguardManager.isDeviceLocked
        val lowPriority = when {
            isDeviceLocked -> false // High priority on locked screen
            isIncoming && !isDeviceLocked -> config.showIncomingCallsFullScreen
            else -> true
        }

        if (
            lowPriority
            || !hasPermission(PERMISSION_POST_NOTIFICATIONS)
            || !canUseFullScreenIntent()
        ) {
            try {
                val needSelectSIM = isOutgoing && call.details.accountHandle == null
                startActivity(CallActivity.getStartIntent(this, needSelectSIM = needSelectSIM))
            } catch (e: Exception) {
                // seems like startActivity can throw AndroidRuntimeException and
                // ActivityNotFoundException, not yet sure when and why, lets show a notification
//                callNotificationManager.setupNotification()
                context.baseConfig.lastError = "CallService: $e"
            }
        }
        callNotificationManager.setupNotification(lowPriority)
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        sendCallUpdateBroadcast(call)
        call.unregisterCallback(callListener)
        callNotificationManager.cancelNotification()
        val wasPrimaryCall = call == CallManager.getPrimaryCall()
        CallManager.onCallRemoved(call)
        EventBus.getDefault().post(Events.RefreshCallLog)
        if (CallManager.getPhoneState() == NoCall) {
            CallManager.inCallService = null
            stopBroadcastTimer()
//            callNotificationManager.cancelNotification()
        } else {
            callNotificationManager.setupNotification()
            if (wasPrimaryCall) {
                startActivity(CallActivity.getStartIntent(this))
            }
        }

        try {
            if (baseConfig.flashForAlerts) MyCameraImpl.newInstance(this).stopSOS()
        } catch (_: Exception) { }
    }

    override fun onCallAudioStateChanged(audioState: CallAudioState?) {
        super.onCallAudioStateChanged(audioState)
        if (audioState != null) {
            CallManager.onAudioStateChanged(audioState)
        }
    }

    override fun onSilenceRinger() {
        super.onSilenceRinger()

        try {
            if (baseConfig.flashForAlerts) MyCameraImpl.newInstance(this).stopSOS()
        } catch (_: Exception) { }
    }

    override fun onDestroy() {
        super.onDestroy()
        callNotificationManager.cancelNotification()
        stopBroadcastTimer()

        try {
            if (baseConfig.flashForAlerts) MyCameraImpl.newInstance(this).stopSOS()
        } catch (_: Exception) { }
    }

    private fun startBroadcastTimer() {
        if (broadcastRunnable != null) return

        broadcastRunnable = object : Runnable {
            override fun run() {
                val primaryCall = CallManager.getPrimaryCall()
                if (primaryCall != null && (primaryCall.getStateCompat() == Call.STATE_ACTIVE || primaryCall.getStateCompat() == Call.STATE_DIALING || primaryCall.getStateCompat() == Call.STATE_CONNECTING)) {
                    sendCallUpdateBroadcast(primaryCall)
                    broadcastHandler.postDelayed(this, 1000)
                } else {
                    stopBroadcastTimer()
                }
            }
        }
        broadcastHandler.post(broadcastRunnable!!)
    }

    private fun stopBroadcastTimer() {
        broadcastRunnable?.let { broadcastHandler.removeCallbacks(it) }
        broadcastRunnable = null
    }

    private fun sendCallUpdateBroadcast(call: Call) {
        val state = when (call.getStateCompat()) {
            Call.STATE_RINGING -> "RINGING"
            Call.STATE_ACTIVE, Call.STATE_DIALING, Call.STATE_CONNECTING, Call.STATE_HOLDING -> "ONGOING"
            Call.STATE_DISCONNECTED, Call.STATE_DISCONNECTING -> "DISCONNECTED"
            else -> "ONGOING"
        }

        val number = call.details.handle?.schemeSpecificPart ?: ""

        getCallContact(this, call) { contact ->
            val intent = Intent("com.miui.dynamicisland.CALL_UPDATE")
            intent.setPackage("com.miui.dynamicisland")
            intent.putExtra("state", state)
            intent.putExtra("number", number)
            intent.putExtra("name", contact.name)
            val duration = if (call.getStateCompat() == Call.STATE_ACTIVE) {
                System.currentTimeMillis() - call.details.connectTimeMillis
            } else {
                0L
            }
            intent.putExtra("duration", duration)
            sendBroadcast(intent)
        }
    }
}

