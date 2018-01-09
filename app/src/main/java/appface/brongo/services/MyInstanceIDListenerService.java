package appface.brongo.services;

import android.content.Intent;

import com.applozic.mobicomkit.Applozic;
import com.applozic.mobicomkit.api.account.register.RegisterUserClientService;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.google.android.gms.iid.InstanceIDListenerService;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Rohit Kumar on 7/14/2017.
 */

public class MyInstanceIDListenerService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        System.out.println("InstanceIDListenerService called");
        String tkn = FirebaseInstanceId.getInstance().getToken();
        Applozic.getInstance(this).setDeviceRegistrationId(tkn);
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
