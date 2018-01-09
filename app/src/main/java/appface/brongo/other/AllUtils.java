package appface.brongo.other;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import appface.brongo.R;
import appface.brongo.model.DeviceDetailsModel;
import appface.brongo.model.TokenModel;
import appface.brongo.util.AppConstants;
import appface.brongo.util.RetrofitAPIs;
import appface.brongo.util.RetrofitBuilders;
import appface.brongo.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Rohit Kumar on 12/31/2017.
 */

public class AllUtils implements NoInternetTryConnectListener{
    private String newToken;
    private Context context;
    @Override
    public void onTryReconnect() {
        if(context != null) {
            getTokenRefresh(context);
        }
    }

    public static class DensityUtils {

        public static int dpToPx(int dp) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
        }

        public static int pxToDp(float px) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, Resources.getSystem().getDisplayMetrics());
        }
    }
    public String getTokenRefresh(final Context context) {
        this.context = context;
        final String deviceId = Utils.getDeviceId(context);
        final SharedPreferences pref = context.getSharedPreferences(AppConstants.PREF_NAME, 0);
        DeviceDetailsModel.TokenGeneration tokenGeneration1 = new DeviceDetailsModel.TokenGeneration();
        tokenGeneration1.setPlatform("android");
        tokenGeneration1.setDeviceId(deviceId);
        tokenGeneration1.setMobileNo(pref.getString(AppConstants.MOBILE_NUMBER,""));
        if (Utils.isNetworkAvailable(context)) {
            RetrofitAPIs apiInstance = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            Call<TokenModel> call = apiInstance.generateToken(tokenGeneration1);
            call.enqueue(new Callback<TokenModel>() {
                @Override
                public void onResponse(Call<TokenModel> call, Response<TokenModel> response) {
                    if (response != null && response.isSuccessful()) {
                        TokenModel responseModel = response.body();
                        if (responseModel.getStatusCode() == 200) {
//                            Toast.makeText(context, responseModel.getMessage(), Toast.LENGTH_SHORT).show();
                            List<TokenModel.Data> data = responseModel.getData();
                            newToken = data.get(0).getAccessToken();
                            Log.i("tokenis", newToken);
                            pref.edit().putString(AppConstants.TOKEN_ACCESS, newToken).commit();

                            Log.w("POSTMAN", " getTokenRefresh: newToken : " + newToken);

                        } else {
                            Toast.makeText(context, responseModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Please Try Again After Sometime", Toast.LENGTH_SHORT).show();
                        try {
                            Log.e("error", response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<TokenModel> call, Throwable t) {
                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } else {
           Utils.internetDialog(context,this);
        }
        return newToken;
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

        ImageView close = (ImageView)dialogBlock.findViewById(R.id.close);
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

        Button reconnect = (Button)dialogBlock.findViewById(R.id.try_again_payment);
        reconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBlock.dismiss();
                if (retryPaymentListener != null)
                    retryPaymentListener.RetryPayment();
            }
        });

        ImageView close = (ImageView)dialogBlock.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBlock.dismiss();
            }
        });

        dialogBlock.show();
    }

    public static void PaymentErrorDialog(final Context context, final RetryPaymentListener retryPaymentListener) {
        final Dialog dialogBlock = new Dialog(context, R.style.MyDialogTheme);
        dialogBlock.setContentView(R.layout.payment_error);
        dialogBlock.setCanceledOnTouchOutside(false);
        dialogBlock.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Button reconnect = (Button)dialogBlock.findViewById(R.id.try_again_payment);
        reconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBlock.dismiss();
                if (retryPaymentListener != null)
                    retryPaymentListener.RetryPayment();
            }
        });

        ImageView close = (ImageView)dialogBlock.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBlock.dismiss();
            }
        });

        dialogBlock.show();
    }
}

