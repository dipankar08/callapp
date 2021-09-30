package `in`.co.dipankar.vcbydip

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telecom.CallAudioState
import android.telecom.Connection
import android.util.Log
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.M)
class CallConnection(ctx: Context) : Connection() {
    var ctx:Context = ctx
    val TAG = "CallConnection"

    override fun onShowIncomingCallUi() {
        Log.i(TAG, "onShowIncomingCallUi")
    }

    override fun onCallAudioStateChanged(state: CallAudioState?) {
        Log.i(TAG, "onCallAudioStateChanged")
    }

    override fun onAnswer() {
        Log.i(TAG, "onAnswer")
    }

    override fun onDisconnect() {
        Log.i(TAG, "onDisconnect")
    }

    override fun onHold() {
        Log.i(TAG, "onHold")
    }

    override fun onUnhold() {
        Log.i(TAG, "onUnhold")
    }

    override fun onReject() {
        Log.i(TAG, "onReject")
    }
}
