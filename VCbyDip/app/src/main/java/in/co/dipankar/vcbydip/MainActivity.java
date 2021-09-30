package in.co.dipankar.vcbydip;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Collections;

import static android.telecom.PhoneAccount.SCHEME_TEL;

public class MainActivity extends AppCompatActivity {

    private PhoneAccount mPhoneAccount;
    private Button mToggleAccountButton;
    private final String TAG = "DIPANKAR#VC_APP";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle aSavedInstanceState) {
        super.onCreate(aSavedInstanceState);
        setContentView(R.layout.activity_main);

        final PhoneAccountHandle handle = new PhoneAccountHandle(
                new ComponentName(this, CallConnectionService.class), "one");
        mPhoneAccount = PhoneAccount.builder(handle, "Test account")
                .setCapabilities(
                        PhoneAccount.CAPABILITY_CALL_PROVIDER
                                | PhoneAccount.CAPABILITY_CALL_SUBJECT
                ).setShortDescription("ShortDescription").setSupportedUriSchemes(Collections.singletonList("vc-by-dip")).build();


        mToggleAccountButton = (Button) findViewById(R.id.togglePhoneAccount);

        if (isPhoneAccountRegistered(handle)) {
            Log.i(TAG, "Phone account registered");
            mToggleAccountButton.setText("UnRegister Phone account");
        } else {
            mToggleAccountButton.setText("Register Phone account");
        }

        mToggleAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View aView) {
                TelecomManager tm = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
                if (isPhoneAccountRegistered(handle)) {
                    tm.unregisterPhoneAccount(mPhoneAccount.getAccountHandle());
                    mToggleAccountButton.setText("Register Phone account");
                } else {
                    tm.registerPhoneAccount(mPhoneAccount);
                    mToggleAccountButton.setText("UnRegister Phone account");
                    Intent intent = new Intent(TelecomManager.ACTION_CHANGE_PHONE_ACCOUNTS);
                    startActivity(intent);
                }
            }
        });

        // start call
        ((Button) findViewById(R.id.placeOutgoingCall)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "staring call");
               // Uri address = Uri.fromParts("vc-by-dip", "12345", null /* fragment */);
                Uri address = Uri.fromParts(SCHEME_TEL, "+15555551234", null);
                Bundle extras = new Bundle();
                extras.putString("videocall", "true");
                TelecomManager mTelecomManager =
                        (TelecomManager) (getSystemService(Context.TELECOM_SERVICE));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Log.i("DIPANKAR", "No permission given");
                    return;
                }
                mTelecomManager.placeCall(address, extras);
            }
        });

        // end call
        ((Button) findViewById(R.id.endCall)).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Ending call");
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                ((TelecomManager) (getSystemService(Context.TELECOM_SERVICE))).endCall();
            }
        });

        // mute button
        ((Button) findViewById(R.id.mute)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Toggleing mute");
                AudioManager mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                mAudioManager.setMicrophoneMute(!mAudioManager.isMicrophoneMute());
            }
        });
        
        // request permission
        requestPermissions(new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.ANSWER_PHONE_CALLS},10);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isPhoneAccountRegistered(PhoneAccountHandle aHandle) {
        TelecomManager tm = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
        return tm.getPhoneAccount(aHandle) != null;
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults){
    
    }
}
