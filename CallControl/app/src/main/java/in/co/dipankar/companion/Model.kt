package `in`.co.dipankar.companion

import android.telecom.Call
import androidx.lifecycle.MutableLiveData

object Model {
    val result = MutableLiveData<String>()
    val action = MutableLiveData<Pair<String, String>>() // pair of command and payload
    val log = MutableLiveData<String>()
    val activeCall = MutableLiveData<Call?>()
}
