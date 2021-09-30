package `in`.co.dipankar.companion

import `in`.co.dipankar.companion.databinding.ActivityMainBinding
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
// adb shell am start -n in.co.dipankar.companion/.MainActivity
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkDefaultDialer()
        Model.log.observe(this, Observer { data -> 
            binding.status.setText("${data}\n-----\n${binding.status.text}")
        })
        //actions
        binding.btnEndCall.setOnClickListener {
            Model.action.postValue(Pair("end_call",""))
        }
        binding.btnAudioMute.setOnClickListener {
            Model.action.postValue(Pair("audio_mute",""))
        }
        binding.btnAccept.setOnClickListener {
            Model.action.postValue(Pair("accept",""))
        }
        binding.btnReject.setOnClickListener {
            Model.action.postValue(Pair("rejcet",""))
        }
    }
    
    fun toast(msg: String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    val REQUEST_CODE_SET_DEFAULT_DIALER=200

    private fun checkDefaultDialer() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            toast("Permison check fails")
            return
        }

        val telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
        val isAlreadyDefaultDialer = packageName == telecomManager.defaultDialerPackage
        if (isAlreadyDefaultDialer) {
            toast("Already a dialer")
            startXService();
            return
        }
        toast("asking for dialer permission")
        val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
            .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
        startActivityForResult(intent, REQUEST_CODE_SET_DEFAULT_DIALER)
    }

    private fun startXService() {
        startService(Intent(this, CallService::class.java))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_SET_DEFAULT_DIALER && REQUEST_CODE_SET_DEFAULT_DIALER in grantResults) {
            Toast.makeText(this, "got dialer permission", Toast.LENGTH_SHORT).show()
            startXService();
        } else {
            Toast.makeText(this, "permission denaied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkSetDefaultDialerResult(resultCode: Int) {
        val message = when (resultCode) {
            RESULT_OK -> "User accepted request to become default dialer"
            RESULT_CANCELED -> "User declined request to become default dialer"
            else            -> "Unexpected result code $resultCode"
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    }
}
