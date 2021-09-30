package `in`.co.dipankar.companion


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telecom.Call
import android.telecom.CallAudioState
import android.telecom.InCallService
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer

@RequiresApi(Build.VERSION_CODES.M)
class CallService : InCallService() {
    companion object {
        private const val TAG = "DIPANKAR"
    }

    var isMute = false
    var mCall: Call? = null

    fun logxx(msg: String) {
        Log.d(TAG, msg)
        Model.log.postValue(msg)
    }

    val observer = Observer<Pair<String, String>> { command ->
        logxx("action called ${command.first}");
        when (command.first) {
            "audio_mute" -> {
                isMute = !isMute;
                setMuted(isMute)
                logxx("Seting mute state ${isMute}")
            }
            "end_call" -> {
                logxx("Disconnting...")
                mCall?.disconnect()
            }
            "accept" -> {
                logxx("Answering...")
                mCall?.answer(0)
            }
            "reject" -> {
                logxx("Rejecting...")
                mCall?.reject(0)
            }
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logxx("onStartCommand")
        logxx("We have total ${calls.size} calls")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        logxx("oncreate")
        if(calls.size > 0 ){
            mCall = calls.get(0)
        }
        Model.action.observeForever(observer)
    }

    override fun onDestroy() {
        Model.action.removeObserver(observer)
        logxx("onDestroy")
        super.onDestroy()
    }

    override fun onCallAudioStateChanged(audioState: CallAudioState?) {
        logxx("onCallAudioStateChanged: is Auto mute? ${audioState?.isMuted()}")
        super.onCallAudioStateChanged(audioState)
    }

    override fun onConnectionEvent(call: Call?, event: String?, extras: Bundle?) {
        logxx("onConnectionEvent")
        super.onConnectionEvent(call, event, extras)
    }

    override fun onBringToForeground(showDialpad: Boolean) {
        logxx("onBringToForeground")
        super.onBringToForeground(showDialpad)
    }

    override fun onCanAddCallChanged(canAddCall: Boolean) {
        logxx("onCanAddCallChanged")
        super.onCanAddCallChanged(canAddCall)
    }

    override fun onSilenceRinger() {
        logxx("onSilenceRinger")
        super.onSilenceRinger()
    }

    override fun onCallAdded(call: Call) {
        logxx("oncalladded")
        if (Model.activeCall.value != null) {
            logxx("Call comes when we already have a call. Ignoring")
        }
        mCall = call;
        logxx(call.details?.extras.toString())
        logxx(call.details.toString())
        Model.activeCall.postValue(call)
        call.registerCallback(callCallback);
    }

    override fun onCallRemoved(call: Call) {
        logxx("onCallRemoved")
        Model.activeCall.postValue(null)
        call.unregisterCallback(callCallback)
    }

    private val callCallback = @RequiresApi(Build.VERSION_CODES.M)
    object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {

            var logstr = when (call.state) {
                0 -> "STATE_NEW"
                1 -> "STATE_DIALING"
                2 -> "STATE_RINGING"
                3 -> "STATE_HOLDING"
                4 -> "STATE_ACTIVE"
                7 -> "STATE_DISCONNECTED"
                8 -> "STATE_SELECT_PHONE_ACCOUNT"
                9 -> "STATE_CONNECTING"
                10 -> "STATE_DISCONNECTING"
                11 -> "STATE_PULLING_CALL"
                else -> "UNKNOWN"
            }
            logxx(logstr)
        }
    }

}
