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
import android.telecom.ConnectionService;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;

import static android.telecom.PhoneAccount.SCHEME_TEL;

public class MainActivity extends AppCompatActivity {

    private PhoneAccount mPhoneAccount;
    private Button mToggleAccountButton;
    private final String TAG = "DIPANKAR#VC_APP";
    private PhoneAccountHandle handle;
    static CallConnection callConnection;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void register() {
        TelecomManager manager = (TelecomManager) getSystemService(TELECOM_SERVICE);
        handle = new PhoneAccountHandle(
                new ComponentName(getPackageName(),
                        CallConnectionService.class.getName()), "NEW ACC");
        PhoneAccount.Builder builder = PhoneAccount.builder(handle, "NEW ACC");
        builder.setCapabilities(PhoneAccount.CAPABILITY_SELF_MANAGED);
        mPhoneAccount = builder.setShortDescription("NEE ACC").build();
        manager.registerPhoneAccount(mPhoneAccount);
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle aSavedInstanceState) {
        super.onCreate(aSavedInstanceState);
        setContentView(R.layout.activity_main);
        
        createAccount();

        // start call
        ((Button) findViewById(R.id.placeOutgoingCall)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "staring call");
               createCall();
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
        
        // send custom event
        ((Button) findViewById(R.id.sendCustomEvnet)).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N_MR1)
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Sending Custom Evnet mute");
                Log.i(TAG, "count s of connection "+CallConnectionService._this.getAllConnections().size());
                if(callConnection != null) {
                    Log.i(TAG, "EVNET SENT");
                    callConnection.sendConnectionEvent("RAISE_HAND", new Bundle());
                } else{
                    Log.i(TAG, "Not able to sent event");
                }
            }
        });
        
        // start service
        startService(new Intent(this, CallConnectionService.class));
        
        // request permission
        requestPermissions(new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.ANSWER_PHONE_CALLS, },10);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isPhoneAccountRegistered(PhoneAccountHandle aHandle) {
        TelecomManager tm = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
        return tm.getPhoneAccount(aHandle) != null;
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults){
    
    }
    private TelecomManager tm;
    private PhoneAccountHandle accountHandle;
    @RequiresApi(api = Build.VERSION_CODES.O)
    void createAccount() {
        tm = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
        if (tm == null) {
            throw new RuntimeException("cannot obtain telecom system service");
        }

        ComponentName connectionServiceName = new ComponentName(getApplicationContext(), CallConnectionService.class);
        PhoneAccountHandle accountHandle = new PhoneAccountHandle(connectionServiceName, "PHONE_ACCOUNT_LABEL");
        try {
            PhoneAccount phoneAccount = tm.getPhoneAccount(accountHandle);
            if (phoneAccount == null) {
                PhoneAccount.Builder builder = PhoneAccount.builder(accountHandle, "PHONE_ACCOUNT_LABEL");
                builder.setCapabilities(PhoneAccount.CAPABILITY_SELF_MANAGED);
                phoneAccount = builder.build();
                tm.registerPhoneAccount(phoneAccount);
            }
            this.accountHandle = phoneAccount.getAccountHandle();

            if (tm.getPhoneAccount(accountHandle) == null) {
                throw new RuntimeException("cannot create account");
            }

        } catch (SecurityException e) {
            throw new RuntimeException("cannot create account", e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void createCall() {
        try {
            Bundle extras = new Bundle();
            extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, accountHandle);
            Uri uri = Uri.fromParts(PhoneAccount.SCHEME_SIP, "test_call", null);
            tm.placeCall(uri, extras);
        }
        catch (SecurityException e) {
            throw new RuntimeException("cannot place call", e);
        }
    }

    public static void setConnection(@NotNull CallConnection c) {
        callConnection =c ;
    }
}
