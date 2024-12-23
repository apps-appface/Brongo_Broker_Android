package in.brongo.brongo_broker.services;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.applozic.mobicomkit.Applozic;
import com.applozic.mobicomkit.api.account.register.RegisterUserClientService;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import in.brongo.brongo_broker.util.AppConstants;

/**
 * Created by Rohit Kumar on 7/14/2017.
 */

public class MyInstanceIDListenerService extends FirebaseInstanceIdService {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    @Override
    public void onTokenRefresh() {
        pref = getSharedPreferences(AppConstants.PREF_NAME,0);
        editor = pref.edit();
        editor.putBoolean(AppConstants.DEVICE_TOKEN_UPDATED,false).commit();
        System.out.println("InstanceIDListenerService called");
        String tkn = FirebaseInstanceId.getInstance().getToken();
        Applozic.getInstance(this).setDeviceRegistrationId(tkn);
        Log.i("Deice token",tkn);
        if (MobiComUserPreference.getInstance(this).isRegistered()) {
            try {
                new RegisterUserClientService(this).updatePushNotificationId(tkn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Fetch updated Instance ID token and notify of changes
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
