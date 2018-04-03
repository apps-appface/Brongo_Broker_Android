package in.brongo.brongo_broker.other;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import in.brongo.brongo_broker.BuildConfig;
import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.model.DeviceDetailsModel;
import in.brongo.brongo_broker.model.TokenModel;
import in.brongo.brongo_broker.util.AppConstants;
import in.brongo.brongo_broker.util.RetrofitAPIs;
import in.brongo.brongo_broker.util.RetrofitBuilders;
import in.brongo.brongo_broker.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Rohit Kumar on 12/31/2017.
 */

public class AllUtils implements NoInternetTryConnectListener {
    private String newToken;
    private Context context;
    private int statusCode = 0;
    private test interfaces;

    @Override
    public void onTryReconnect() {
        if (context != null) {
            getTokenRefresh(context);
        }
    }

    public int getTokenRefresh(final Context context) {
        try {
            this.context = context;
            final String deviceId = Utils.getDeviceId(context);
            String versionName = BuildConfig.VERSION_NAME;
            final SharedPreferences pref = context.getSharedPreferences(AppConstants.PREF_NAME, 0);
            DeviceDetailsModel.TokenGeneration tokenGeneration1 = new DeviceDetailsModel.TokenGeneration();
            tokenGeneration1.setPlatform("android");
            tokenGeneration1.setDeviceId(deviceId);
            tokenGeneration1.setVersion(versionName);
            tokenGeneration1.setMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
            if (Utils.isNetworkAvailable(context)) {
                RetrofitAPIs apiInstance = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
                Call<TokenModel> call = apiInstance.generateToken(tokenGeneration1);
                call.enqueue(new Callback<TokenModel>() {
                    @Override
                    public void onResponse(Call<TokenModel> call, Response<TokenModel> response) {
                        String responseString = null;
                        if (response != null && response.isSuccessful()) {
                            TokenModel responseModel = response.body();
                            statusCode = responseModel.getStatusCode();
                            if (statusCode == 200) {
                               // interfaces.onSuccessRes(true);
    //                            Toast.makeText(context, responseModel.getMessage(), Toast.LENGTH_SHORT).show();
                                List<TokenModel.Data> data = responseModel.getData();
                                newToken = data.get(0).getAccessToken();
                                Log.i("tokenis", newToken);
                                pref.edit().putString(AppConstants.TOKEN_ACCESS, newToken).commit();
                                Log.w("POSTMAN", " getTokenRefresh: newToken : " + newToken);

                            }
                        } else {
                            try {
                                responseString = response.errorBody().string();JSONObject jsonObject = new JSONObject(responseString);
                                String message = jsonObject.optString("message");
                                int statusCode = jsonObject.optInt("statusCode");
                                if(statusCode == 412){
                                    updateDialog(context);
                                }else {
                                   // interfaces.onSuccessRes(false);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<TokenModel> call, Throwable t) {
                        String errormessage = t.getMessage().toString();
                        //interfaces.onSuccessRes(false);
                        //Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                return statusCode;
            } else {
                Utils.internetDialog(context, this);
                return statusCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusCode;
    }

    public interface test{
        void onSuccessRes(boolean isSuccess);
    }
    public static void PaymentSuccessDialog(final Context context) {
        final Dialog dialogBlock = new Dialog(context, R.style.MyDialogTheme);
        dialogBlock.setContentView(R.layout.payment_success);
        dialogBlock.setCanceledOnTouchOutside(false);
        dialogBlock.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Button reconnect = (Button) dialogBlock.findViewById(R.id.ok_BTN);
        reconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBlock.dismiss();

            }
        });

        ImageView close = (ImageView) dialogBlock.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBlock.dismiss();
            }
        });

        dialogBlock.show();
    }

    public static void PaymentFailedDialog(final Context context, final RetryPaymentListener retryPaymentListener) {
        final Dialog dialogBlock = new Dialog(context, R.style.MyDialogTheme);
        dialogBlock.setContentView(R.layout.payment_failed);
        dialogBlock.setCanceledOnTouchOutside(false);
        dialogBlock.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Button reconnect = (Button) dialogBlock.findViewById(R.id.try_again_payment);
        reconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBlock.dismiss();
                if (retryPaymentListener != null)
                    retryPaymentListener.RetryPayment();
            }
        });

        ImageView close = (ImageView) dialogBlock.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBlock.dismiss();
            }
        });

        dialogBlock.show();
    }

    public static void PaymentErrorDialog(final Context context, final RetryPaymentListener retryPaymentListener) {
        try {
            final Dialog dialogBlock = new Dialog(context, R.style.MyDialogTheme);
            dialogBlock.setContentView(R.layout.payment_error);
            dialogBlock.setCanceledOnTouchOutside(false);
            dialogBlock.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

            Button reconnect = (Button) dialogBlock.findViewById(R.id.try_again_payment);
            reconnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogBlock.dismiss();
                    if (retryPaymentListener != null)
                        retryPaymentListener.RetryPayment();
                }
            });

            ImageView close = (ImageView) dialogBlock.findViewById(R.id.close);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogBlock.dismiss();
                }
            });

            dialogBlock.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String changeNumberFormat(float amount) {
        String moneyString = "";
        try {
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            moneyString = formatter.format(amount);
            if(moneyString.contains(".")) {
                moneyString = moneyString.substring(0, moneyString.indexOf("."));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return moneyString;
    }
    public void getToken(final Context context, final test interfaces) {
        try {
            this.context = context;
            this.interfaces = interfaces;
            final String deviceId = Utils.getDeviceId(context);
            String versionName = BuildConfig.VERSION_NAME;
            final SharedPreferences pref = context.getSharedPreferences(AppConstants.PREF_NAME, 0);
            DeviceDetailsModel.TokenGeneration tokenGeneration1 = new DeviceDetailsModel.TokenGeneration();
            tokenGeneration1.setPlatform("android");
            tokenGeneration1.setDeviceId(deviceId);
            tokenGeneration1.setVersion(versionName);
            tokenGeneration1.setMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
            if (Utils.isNetworkAvailable(context)) {
                RetrofitAPIs apiInstance = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
                Call<TokenModel> call = apiInstance.generateToken(tokenGeneration1);
                call.enqueue(new Callback<TokenModel>() {
                    @Override
                    public void onResponse(Call<TokenModel> call, Response<TokenModel> response) {
                        String responseString = null;
                        if (response != null && response.isSuccessful()) {
                            TokenModel responseModel = response.body();
                            statusCode = responseModel.getStatusCode();
                            if (statusCode == 200) {
                                //updateDialog(context);
    //                            Toast.makeText(context, responseModel.getMessage(), Toast.LENGTH_SHORT).show();
                                List<TokenModel.Data> data = responseModel.getData();
                                newToken = data.get(0).getAccessToken();
                                Log.i("tokenis", newToken);
                                pref.edit().putString(AppConstants.TOKEN_ACCESS, newToken).commit();
                                Log.w("POSTMAN", " getTokenRefresh: newToken : " + newToken);
                                if(interfaces != null) {
                                    interfaces.onSuccessRes(true);
                                }
                            }
                        } else {
                            try {
                                responseString = response.errorBody().string();JSONObject jsonObject = new JSONObject(responseString);
                                String message = jsonObject.optString("message");
                                int statusCode = jsonObject.optInt("statusCode");
                                if(statusCode == 412){
                                    updateDialog(context);
                                }else {
                                    if(interfaces != null) {
                                        interfaces.onSuccessRes(false);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<TokenModel> call, Throwable t) {
                        String errormessage = t.getMessage().toString();
                        if(interfaces != null) {
                            interfaces.onSuccessRes(false);
                        }
                        //Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Utils.internetDialog(context, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateDialog(final Context context){
        try {
            final Activity activity = (Activity)context ;
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.walkthrough_back);
            dialog.setContentView(R.layout.update_dialog);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            TextView update_btn = (TextView)dialog.findViewById(R.id.update_dialog_btn);
            update_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Uri webpage = Uri.parse("https://play.google.com/store/apps/details?id=in.brongo.brongo_broker");
                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                        if (intent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(intent);
                        }
                        dialog.dismiss();
                        activity.finishAndRemoveTask();
                    }catch (Exception e){
                    }
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

