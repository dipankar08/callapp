package `in`.co.dipankar.vcbydip

import android.os.Build
import android.provider.CallLog.Calls.PRESENTATION_ALLOWED
import android.telecom.*
import android.util.Log
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.M)
class CallConnectionService : ConnectionService() {
    val TAG = "DIPANKAR#VC"
    override fun onCreate() {
        Log.i(TAG, "service started")
        _this = this
        super.onCreate()
    }

    override fun onDestroy() {
        Log.i(TAG, "service ended")
        super.onDestroy()
    }
    override fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        Log.i(TAG, "onCreateOutgoingConnection")
        val c =  CallConnection(this)
        MainActivity.setConnection(c);
        return c;
    }

    override fun onCreateOutgoingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ) {
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request)
        Log.i(TAG, "create outgoing call failed")
    }

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        Log.i(TAG, "onCreateIncomingConnection")
        val c =  CallConnection(this)
        MainActivity.setConnection(c);
        return c;
    }

    override fun onCreateIncomingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ) {
        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request)
        Log.i(TAG , "create outgoing call failed ")
    }

    companion object {
        lateinit var _this: ConnectionService;
    }
}
