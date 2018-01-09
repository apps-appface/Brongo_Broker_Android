package appface.brongo.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import appface.brongo.activity.MainActivity;
import appface.brongo.model.DeviceDetailsModel;
import appface.brongo.model.TokenModel;
import appface.brongo.util.AppConstants;
import appface.brongo.util.RetrofitAPIs;
import appface.brongo.util.RetrofitBuilders;
import appface.brongo.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

/**
 * Created by Rohit Kumar on 7/10/2017.
 */

public class TokenServices extends Service{
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        doToast();
        return super.onStartCommand(intent, flags, startId);
    }
    public void doToast()
    {
        final Handler handler= new Handler();
        handler.postDelayed(new Runnable(){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                    generate_Token1();
                    handler.postDelayed(this, 1000 * 60 * 28);
            }
        }, 0);

    }
    private void generate_Token1() {
        final String deviceId = Utils.getDeviceId(getApplicationContext());
        final SharedPreferences pref = getApplicationContext().getSharedPreferences(AppConstants.PREF_NAME,0);
        final SharedPreferences.Editor editor = pref.edit();
        RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
        DeviceDetailsModel.TokenGeneration tokenGeneration1 = new DeviceDetailsModel.TokenGeneration();
        tokenGeneration1.setPlatform("android");
        tokenGeneration1.setDeviceId(deviceId);
        tokenGeneration1.setMobileNo(pref.getString(AppConstants.MOBILE_NUMBER,""));
        Call<TokenModel> call = retrofitAPIs.generateToken(tokenGeneration1);
        call.enqueue(new Callback<TokenModel>() {
            @Override
            public void onResponse(Call<TokenModel> call, Response<TokenModel> response) {
                if (response != null && response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.isSuccessful()) {
                            try {
                                TokenModel tokenModel1 = response.body();
                                int statusCode = tokenModel1.getStatusCode();
                                String message = tokenModel1.getMessage();
                                if (statusCode == 200 && message.equalsIgnoreCase("")) {
                                    makeText(getApplicationContext(), "Token is generated", LENGTH_SHORT).show();
                                    String mobileNo = tokenModel1.getData().get(0).getMobileNo();
                                    String tokenAccess = tokenModel1.getData().get(0).getAccessToken();
                                    editor.putString(AppConstants.DEVICE_ID, deviceId);
                                    editor.putString(AppConstants.TOKEN_ACCESS, tokenAccess);
                                    editor.commit();
                                    if(!pref.getBoolean(AppConstants.LOGIN_STATUS,false)){
                                        Intent intent = new Intent (getBaseContext(), MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                } else { makeText(getApplicationContext(), "Token Access is denied", LENGTH_SHORT).show();
                                }

                            } catch (Exception e) {

                            }
                        } else {
                            String responseString = null;
                            try {
                                responseString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                int statusCode = jsonObject.optInt("statusCode");
                                String message = jsonObject.optString("message");
                                if (statusCode == 417) {
                                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                                }else if(statusCode == 503){
                                    Toast.makeText(getApplicationContext(),"some problem occured",Toast.LENGTH_SHORT).show();
                                }
                            }  catch (IOException e) {
                                e.printStackTrace();
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }else{
                    makeText(getApplicationContext(), "error", LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<TokenModel> call, Throwable t) {
                makeText(getApplicationContext(),"Token Access is denied", LENGTH_SHORT).show();


            }
        });
    }
}
