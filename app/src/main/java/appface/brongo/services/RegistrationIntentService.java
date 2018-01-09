package appface.brongo.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.applozic.mobicomkit.Applozic;
import com.applozic.mobicomkit.api.account.register.RegisterUserClientService;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import appface.brongo.R;
import appface.brongo.activity.LoginActivity;
import appface.brongo.activity.MainActivity;
import appface.brongo.activity.PushAlertActivity;
import appface.brongo.model.DeviceDetailsModel;
import appface.brongo.util.AppConstants;
import appface.brongo.util.RetrofitAPIs;
import appface.brongo.util.RetrofitBuilders;
import appface.brongo.util.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Rohit Kumar on 7/14/2017.
 */

public class RegistrationIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    private static final String TAG = "RegIntentService";
    private Context context = RegistrationIntentService.this;
    private SharedPreferences pref;
    SharedPreferences.Editor editor;
    private PackageInfo pInfo;
    private String appVersion;
    private String os_version;
    private String newToken = "";
    private String finalToken = "";
    private int key =0;
    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent( Intent intent) {
        if(intent != null){
            key = intent.getIntExtra("key",0);
        }
        String oldToken;
       pref = getSharedPreferences(AppConstants.PREF_NAME,0);
        editor = pref.edit();
        // Make a call to Instance API
        InstanceID instanceID = InstanceID.getInstance(this);
        String senderId = getResources().getString(R.string.sender_id);
        try {
            // request token that will be used by the server to send push notifications
            newToken = FirebaseInstanceId.getInstance().getToken();
           // newToken = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            oldToken = pref.getString(AppConstants.DEVICE_TOKEN, "");
            boolean tokenSame = newToken.equals(oldToken);
            if (tokenSame) {
                finalToken = oldToken;
            } else {
                finalToken = newToken;
                editor.putString(AppConstants.DEVICE_TOKEN, newToken);
                editor.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(Utils.isNetworkAvailable(getApplicationContext())) {
            sendDeviceDetails();
        }else{
            Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_SHORT).show();
        }
        /*if (MobiComUserPreference.getInstance(this).isRegistered()) {
            try {
                new RegisterUserClientService(this).updatePushNotificationId(pref.getString(AppConstants.DEVICE_TOKEN,""));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }


    private void sendDeviceDetails(){
        Call<ResponseBody> call = null;
        DeviceDetailsModel deviceDetailsModel = new DeviceDetailsModel();
        deviceDetailsModel.setMobileNo(pref.getString(AppConstants.MOBILE_NUMBER,""));
        deviceDetailsModel.setDeviceId(pref.getString(AppConstants.DEVICE_ID,""));
        deviceDetailsModel.setPlatform("android");
        deviceDetailsModel.setUserId(pref.getString(AppConstants.USER_ID,""));
        deviceDetailsModel.setAppVersion(pref.getString(AppConstants.APP_VERSION,""));
        deviceDetailsModel.setDeviceToken(pref.getString(AppConstants.DEVICE_TOKEN,""));
        deviceDetailsModel.setModelName(pref.getString(AppConstants.MODEL_NAME,""));
        deviceDetailsModel.setOsVersion(pref.getString(AppConstants.OS_VERSION,""));
        RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
        call = retrofitAPIs.storeDevice(deviceDetailsModel);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response != null){
                    String responseString = null;
                    if(response.isSuccessful()){
                        try {
                            responseString = response.body().string();
                            JSONObject jsonObject = new JSONObject(responseString);
                            int statusCode = jsonObject.optInt("statusCode");
                            String message = jsonObject.optString("message");
                            if(statusCode == 200 && message.equalsIgnoreCase("Device Information Successfully Uploaded")){
                                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                                if(key == 100) {
                                    editor.putBoolean(AppConstants.DEVICE_TOKEN_UPDATED, true);
                                    editor.commit();
                                }
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        try {
                            responseString = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(responseString);
                            int statusCode = jsonObject.optInt("statusCode");
                            String message = jsonObject.optString("message");
                            if (statusCode == 417) {
                                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                            }
                        }  catch (IOException e) {
                            e.printStackTrace();
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }
}
