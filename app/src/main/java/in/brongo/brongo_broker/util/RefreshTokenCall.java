package in.brongo.brongo_broker.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.activity.MainActivity;
import in.brongo.brongo_broker.model.DeviceDetailsModel;
import in.brongo.brongo_broker.model.TokenModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.Toast.makeText;

/**
 * Created by Rohit Kumar on 9/19/2017.
 */

public class RefreshTokenCall {
    int i;
    Handler handler;
    Runnable runnable;
    ProgressDialog pd;
    int runTime;
    SharedPreferences preferences;

public RefreshTokenCall(final Context context,int isAppRunning) {
    pd = new ProgressDialog(context, R.style.CustomProgressDialog);
    pd.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress_loader));
    pd.setCancelable(true);
    pd.setCanceledOnTouchOutside(false);
    i=isAppRunning;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        handler = new Handler();
        runnable = new Runnable() {
@Override
public void run() {
        generate_Token1(context);
        }
        };
        handler.postDelayed(runnable,1000);
        //handler.postDelayed(runnable, 60*1000);
        }
    private void generate_Token1(final Context context) {
        final String deviceId = Utils.getDeviceId(context);
        final SharedPreferences pref = context.getSharedPreferences(AppConstants.PREF_NAME,0);
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
               try {
                   if(i == 1){
                       Activity activity = (Activity)context;
                       if(activity != null){
                           activity.recreate();
                       }
                   }
               }catch (Exception e){

               }
                if (response != null && response.isSuccessful()) {
                    if (response.body() != null) {
                        handler.postDelayed(runnable,29*60*1000);
                        if (response.isSuccessful()) {
                            try {
                                TokenModel tokenModel1 = response.body();
                                int statusCode = tokenModel1.getStatusCode();
                                String message = tokenModel1.getMessage();
                                if (statusCode == 200 && message.equalsIgnoreCase("")) {
                                   // Utils.showToast(context,"Token is generated");
                                    String mobileNo = tokenModel1.getData().get(0).getMobileNo();
                                    String tokenAccess = tokenModel1.getData().get(0).getAccessToken();
                                    editor.putString(AppConstants.DEVICE_ID, deviceId);
                                    editor.putString(AppConstants.TOKEN_ACCESS, tokenAccess);
                                    editor.commit();
                                    if(i == 0){
                                        i++;
                                        context.startActivity(new Intent(context, MainActivity.class));
                                    }

                                }
                            } catch (Exception e) {
                            }
                        } else {

                        }
                    }
                }else{
                    String responseString = null;
                    try {
                        responseString = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(responseString);
                        int statusCode = jsonObject.optInt("statusCode");
                        String message = jsonObject.optString("message");
                            Utils.showToast(context,message,"Error");
                    }  catch (IOException e) {
                        e.printStackTrace();
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }


            @Override
            public void onFailure(Call<TokenModel> call, Throwable t) {
                String message = t.getMessage();
                Utils.showToast(context, t.getLocalizedMessage().toString(),"Failure");
                if(i == 0){
                    i++;
                    context.startActivity(new Intent(context, MainActivity.class));
                }
            }
        });
    }
}
