package in.brongo.brongo_broker.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.model.ApiModel;
import in.brongo.brongo_broker.model.ClientDetailsModel;
import in.brongo.brongo_broker.other.AllUtils;
import in.brongo.brongo_broker.other.NoInternetTryConnectListener;
import in.brongo.brongo_broker.other.NoTokenTryListener;
import in.brongo.brongo_broker.services.MusicService;
import in.brongo.brongo_broker.uiwidget.FlowLayout;
import in.brongo.brongo_broker.util.AppConstants;
import in.brongo.brongo_broker.util.CustomApplicationClass;
import in.brongo.brongo_broker.util.RetrofitAPIs;
import in.brongo.brongo_broker.util.RetrofitBuilders;
import in.brongo.brongo_broker.util.Utils;
import ng.max.slideview.SlideView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by panka_000 on 14-03-2016.
 */
public class PushAlertActivity extends Activity implements NoInternetTryConnectListener, NoTokenTryListener, AllUtils.test/*implements View.OnTouchListener */ {
    private Bundle data;
    private int apicode = 0;
    private int currentTime, matchedProperties;
    private TextView noti_commission, noti_client_name, noti_reject, noti_progress, noti_client_type, noti_matching, plan_textview,noti_commission_two;
    private ImageView noti_client_pic;
    private RatingBar noti_ratingbar;
    private FlowLayout push_flowlayout;
    private int taskcompleted = 0;
    private CountDownTimer countDownTimer;
    private String clientMobileNo, reject_reason = "";
    private RelativeLayout progress_container;
    private String prop_address, prop_bhk, prop_status, prop_type, budget, prop_client_name, prop_client_type, client_image, prop_id, posting_type, sub_property_type, commission1, plantype, impFields;
    private float rating1;
    private MediaPlayer mediaPlayer;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar progressBar;
    private SlideView slideView;
    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter1;
    private ArrayList<String> keyList;
    private Vibrator v;
    private Intent serviceIntent;
    private ScrollView parentLayout;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_new_notification);
        try {
            Intent intent = getIntent();
            data = intent.getExtras();
            if (data != null) {
                prop_address = data.getString("microMarketName", "");
                clientMobileNo = data.getString("clientMobileNo", "");
                prop_bhk = data.getString("bedRoomType", "");
                prop_status = data.getString("propertyStatus", "");
                prop_type = data.getString("propertyType", "");
                commission1 = data.getString("commission", "");
                prop_id = data.getString("propertyId", "");
                prop_client_name = data.getString("clientName", "");
                String rating2 = data.getString("rating", "");
                rating1 = Float.parseFloat(rating2);
                client_image = data.getString("image", "");
                posting_type = data.getString("postingType", "");
                budget = data.getString("budget", "");
                impFields = data.getString("impFields", "");
                plantype = data.getString("planType");
                //plantype = "PREMIUM";
                sub_property_type = data.getString("subPropertyType", "");
                String matchedProperties1 = data.getString("matchedProperties", "");
                matchedProperties = Integer.parseInt(matchedProperties1);
                matchedProperties = 0;
            } else {
                prop_address = prop_bhk = prop_status = prop_type = budget = prop_client_name = prop_client_type = client_image = prop_id = posting_type = sub_property_type = commission1 = plantype = impFields = "";
            }
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            initialise();
            intentFilter1 = new IntentFilter();
            intentFilter1.addAction("3_broker_done");
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    countDownTimer.onFinish();
                }
            };
            registerReceiver(broadcastReceiver, intentFilter1);
            slideView.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
                @Override
                public void onSlideComplete(SlideView slideView) {
                    stopService();
                    countDownTimer.cancel();
                    clientAccept();
                    v.cancel();
                    slideView.setVisibility(View.GONE);
                }
            });
            noti_reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stopService();
                    rejectDialog();
                    v.cancel();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialise() {
        try {
            context = this;
            parentLayout = findViewById(R.id.push_parent_scroll);
            parentLayout.setVisibility(View.GONE);
            mediaPlayer = new MediaPlayer();
            if (isDeviceLocked(context)) {
                unlockScreen();
            }
            serviceIntent = new Intent(context, MusicService.class);
            pref = getSharedPreferences(AppConstants.PREF_NAME, 0);
            v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            editor = pref.edit();
            keyList = new ArrayList<>();
            push_flowlayout = findViewById(R.id.push_flowlayout);
            noti_commission = findViewById(R.id.noti_commission);
            noti_commission_two = findViewById(R.id.noti_commission_two);
            noti_client_name = findViewById(R.id.notification_client_name);
            noti_reject = findViewById(R.id.noti_reject);
            plan_textview = findViewById(R.id.push_plan_text);
            noti_matching = findViewById(R.id.noti_matching_property);
            noti_progress = findViewById(R.id.progress_text);
            noti_client_type = findViewById(R.id.noti_client_type);
            noti_client_pic = findViewById(R.id.client__notifiation_pic);
            noti_ratingbar = findViewById(R.id.noti_ratingBar);
            slideView = findViewById(R.id.slideView);
            progressBar = findViewById(R.id.noti_progressBar);
            progress_container = findViewById(R.id.container);
            if (pref.getString(AppConstants.NOTI_TYPE, "").equalsIgnoreCase("call")) {
                editor.remove(AppConstants.NOTI_TYPE).commit();
                getImportantKeys();
                startService(serviceIntent);
                showTimer();
                setValue();
                parentLayout.setVisibility(View.VISIBLE);
                callNotificationApi();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clientAccept() {
        if (Utils.isNetworkAvailable(context)) {
            Utils.LoaderUtils.showLoader(context);
            ApiModel.ClientAcceptModel clientAcceptModel = new ApiModel.ClientAcceptModel();
            clientAcceptModel.setClientMobileNo(clientMobileNo);
            //clientAcceptModel.setRentPropertyId(rentPropertyId);
            clientAcceptModel.setBrokerMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
            clientAcceptModel.setPostingType(posting_type.toUpperCase());
            clientAcceptModel.setPropertyId(prop_id);
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            Call<ResponseBody> call = retrofitAPIs.ClentAcceptApi(clientAcceptModel);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Utils.LoaderUtils.dismissLoader();
                    if (response != null) {
                        String responseString = null;
                        if (response.isSuccessful()) {
                            try {
                                responseString = response.body().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                String message = jsonObject.optString("message");
                                int statusCode = jsonObject.optInt("statusCode");
                                if (statusCode == 200 && message.equalsIgnoreCase("Client And Broker Connection Is Established")) {
                                    Intent intent = new Intent(PushAlertActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    intent.putExtra("shouldShowDialog", true);
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                responseString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                String message = jsonObject.optString("message");
                                int statusCode = jsonObject.optInt("statusCode");
                                Utils.setSnackBar(parentLayout, message);
                                Intent intent = new Intent(PushAlertActivity.this, MainActivity.class);
                                intent.putExtra("shouldShowDialog", false);
                                startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    makeScreenOff();
                    Utils.setSnackBar(parentLayout, t.getMessage().toString());
                    Utils.LoaderUtils.dismissLoader();
                    goToMainPage();
                }
            });
        } else {
            taskcompleted = 100;
            Utils.internetDialog(context, this);
        }
    }

    private void showTimer() {
        try {
            currentTime = 1;
            progressBar.setProgress(0);
            progressBar.setMax(120);
            long[] pattern = {0, 200, 0};
            v.vibrate(pattern, 0);
            //playMedia();
            countDownTimer = new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    currentTime++;
                    progressBar.setProgress(currentTime + 1);
                    if (currentTime > 50) {
                        noti_progress.setText("00:0" + (60 - currentTime));
                    } else {
                        noti_progress.setText("00:" + (60 - currentTime));
                    }
                }

                @Override
                public void onFinish() {
                    currentTime++;
                    stopService();
                    progressBar.setProgress(currentTime);
                    if (currentTime > 50) {
                        noti_progress.setText("00:0" + (60 - currentTime));
                    } else {
                        noti_progress.setText("00:" + (60 - currentTime));
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                    noti_progress.setVisibility(View.INVISIBLE);
                    parentLayout.setVisibility(View.GONE);
                    countDownTimer.cancel();
                    //stopMedia();
                    v.cancel();
                    missedTask();
                }
            };
            countDownTimer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unlockScreen() {
        try {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setValue() {
        try {
            if (data != null) {
                plan_textview.setText(plantype);
                if (plantype.equalsIgnoreCase("")) {
                    plan_textview.setVisibility(View.GONE);
                }

                noti_client_type.setText(posting_type.toUpperCase() + "/" + prop_type.toUpperCase());
                String back_color = Utils.getPostingColor(posting_type);
                noti_client_type.setBackgroundColor(Color.parseColor(back_color));
               setCommissionText(posting_type.toUpperCase());
                noti_client_name.setText(prop_client_name);
                Glide.with(context)
                        .load(client_image)
                        .apply(CustomApplicationClass.getRequestOption(true))
                        .into(noti_client_pic);
                noti_matching.setText(matchedProperties + " matching properties in your inventory");
                noti_ratingbar.setRating(rating1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onPause() {
        Utils.LoaderUtils.dismissLoader();
        super.onPause();
        Log.i("pushActiviry", "pause");
        v.cancel();
        if (!isDeviceLocked(context)) {
            stopService();
        }
    }

    private void rejectDialog() {
        try {
            final Dialog dialog = new Dialog(PushAlertActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.drawer_background);
            dialog.setContentView(R.layout.reject_popup);
            Window window = dialog.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            TextView client_name = dialog.findViewById(R.id.reject_client_name);
            TextView client_address = dialog.findViewById(R.id.reject_client_address);
            TextView client_plan = dialog.findViewById(R.id.drop_plan_call);
            ImageView client_image1 = dialog.findViewById(R.id.reject_client_image);
            RatingBar reject_ratingbar = dialog.findViewById(R.id.reject_dialog_ratingBar);
            client_name.setText(prop_client_name);
            client_address.setText(prop_address);
            client_plan.setText(plantype);
            Glide.with(context)
                    .load(client_image)
                    .apply(CustomApplicationClass.getRequestOption(false))
                    .into(client_image1);
            final CheckBox checkbox1 = dialog.findViewById(R.id.reject_checkbox1);
            final CheckBox checkbox2 = dialog.findViewById(R.id.reject_checkbox2);
            final CheckBox checkbox3 = dialog.findViewById(R.id.reject_checkbox3);
            Button submit = dialog.findViewById(R.id.lead_reject_submit);
            reject_ratingbar.setRating(rating1);
            checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        checkbox2.setChecked(false);
                        checkbox3.setChecked(false);
                        reject_reason = checkbox1.getText().toString();
                    }
                }
            });
            checkbox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        checkbox1.setChecked(false);
                        checkbox3.setChecked(false);
                        reject_reason = checkbox2.getText().toString();
                    }
                }
            });
            checkbox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        checkbox1.setChecked(false);
                        checkbox2.setChecked(false);
                        reject_reason = checkbox3.getText().toString();
                    }
                }
            });

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (reject_reason == null || reject_reason.equalsIgnoreCase("")) {
                        Utils.setSnackBar(parentLayout, "Select the reason first");
                    } else {
                        leadRejectApi();
                        dialog.dismiss();
                    }
                }
            });
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void leadRejectApi() {
        try {
            if (Utils.isNetworkAvailable(context)) {
                Utils.LoaderUtils.showLoader(context);
                ApiModel.ClientAcceptModel clientAcceptModel = new ApiModel.ClientAcceptModel();
                clientAcceptModel.setClientMobileNo(clientMobileNo);
                clientAcceptModel.setBrokerMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
                clientAcceptModel.setPostingType(posting_type.toUpperCase());
                clientAcceptModel.setPropertyId(prop_id);
                clientAcceptModel.setReason(reject_reason);
                clientAcceptModel.setPostedUser("client");
                RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
                String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
                String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
                Call<ResponseBody> call = retrofitAPIs.rejectLeadApi(tokenaccess, "android", deviceId, clientAcceptModel);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response != null) {
                            Utils.LoaderUtils.dismissLoader();
                            String responseString = null;
                            if (response.isSuccessful()) {
                                try {
                                    responseString = response.body().string();
                                    JSONObject jsonObject = new JSONObject(responseString);
                                    String message = jsonObject.optString("message");
                                    int statusCode = jsonObject.optInt("statusCode");
                                    if (statusCode == 200 && message.equalsIgnoreCase("Thanks For Your Valuable Feedback")) {
                                        countDownTimer.cancel();
                                        slideView.setVisibility(View.GONE);
                                        progress_container.setVisibility(View.GONE);
                                        Utils.setSnackBar(parentLayout, message);
                                        goToMainPage();
                                        int noti_count = pref.getInt(AppConstants.NOTIFICATION_COUNT, 0);
                                        if (noti_count > 0) {
                                            editor.putInt(AppConstants.NOTIFICATION_COUNT, noti_count - 1);
                                        }
                                    }
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    responseString = response.errorBody().string();
                                    JSONObject jsonObject = new JSONObject(responseString);
                                    String message = jsonObject.optString("message");
                                    int statusCode = jsonObject.optInt("statusCode");
                                    if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                        apicode = 200;
                                        openTokenDialog(context);
                                    } else {
                                        goToMainPage();
                                        Utils.setSnackBar(parentLayout, message);
                                    }
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Utils.LoaderUtils.dismissLoader();
                        Utils.setSnackBar(parentLayout, t.getMessage().toString());
                        goToMainPage();
                    }
                });
            } else {
                taskcompleted = 200;
                Utils.internetDialog(context, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        Log.i("pushActiviry", "stop");
        super.onStop();
        try {
            v.cancel();
            if (!isDeviceLocked(context)) {
                stopService();
            }
            //stopService();
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("pushActiviry", "resume");
        registerReceiver(broadcastReceiver, intentFilter1);
    }

    @Override
    public void onTryReconnect() {
        switch (taskcompleted) {
            case 100:
                clientAccept();
                break;
            case 200:
                leadRejectApi();
                break;
            case 1000:
                callNotificationApi();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService();
    //stopMedia();
    makeScreenOff();
        Log.i("pushActiviry", "destroy");
        try {
            Utils.LoaderUtils.dismissLoader();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callNotificationApi() {
        try {
            if (Utils.isNetworkAvailable(context)) {
                Call<ResponseBody> call = null;
                ClientDetailsModel.NotificatioModel notificatioModel = new ClientDetailsModel.NotificatioModel();
                notificatioModel.setBrokerMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
                notificatioModel.setClientMobileNo(clientMobileNo);
                notificatioModel.setPostingType(posting_type);
                notificatioModel.setPropertyId(prop_id);
                RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
                call = retrofitAPIs.notificationApi(notificatioModel);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response != null) {
                            String responseString = null;
                            if (response.isSuccessful()) {

                            } else {
                                try {
                                    responseString = response.errorBody().string();
                                    JSONObject jsonObject = new JSONObject(responseString);
                                    String message = jsonObject.optString("message");
                                    int statusCode = jsonObject.optInt("statusCode");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            } else {
                taskcompleted = 1000;
                Utils.internetDialog(context, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isDeviceLocked(Context context) {
        boolean isLocked = false;

        try {
            // First we check the locked state
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            boolean inKeyguardRestrictedInputMode = keyguardManager.inKeyguardRestrictedInputMode();

            if (inKeyguardRestrictedInputMode) {
                isLocked = true;

            } else {
                // If password is not set in the settings, the inKeyguardRestrictedInputMode() returns false,
                // so we need to check if screen on for this case

                PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                    isLocked = !powerManager.isInteractive();
                } else {
                    //noinspection deprecation
                    isLocked = !powerManager.isScreenOn();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isLocked;
    }

    private void missedTask() {
        try {
            Intent intent = new Intent(PushAlertActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("missedDialog", true);
            intent.putExtra("clientName", prop_client_name);
            intent.putExtra("postType", posting_type);
            startActivity(intent);
            makeScreenOff();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopService() {
        stopService(new Intent(context, MusicService.class));
    }

    private void makeScreenOff() {
        try {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopMedia() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    private void goToMainPage() {
        try {
            Intent intent = new Intent(PushAlertActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getImportantKeys() {
        try {
            if (impFields != null && !impFields.isEmpty()) {
                impFields = impFields.substring(1, impFields.length() - 1);
                if (impFields.length() > 0) {
                    keyList = new ArrayList<>(Arrays.asList(impFields.split(",")));
                    addviewTolayout(keyList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addviewTolayout(ArrayList<String> arrayList) {
        if (arrayList.size() != 0) {
            for (int i = 0; i < arrayList.size(); i++) {
                try {
                    if (arrayList.get(i) != null && !(arrayList.get(i).isEmpty())) {
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View layout2 = inflater.inflate(R.layout.deal_child, null);
                        TextView deal_textview = layout2.findViewById(R.id.deal_text);
                        deal_textview.setText(arrayList.get(i));
                        deal_textview.setBackgroundResource(R.drawable.rounded_empty_btn);
                        deal_textview.setTextColor(getResources().getColor(R.color.appColor));
                        push_flowlayout.addView(layout2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    String error = e.toString();
                }
            }
        }
    }

    @Override
    public void onTryRegenerate() {
        switch (apicode) {
            case 200:
                Utils.LoaderUtils.showLoader(context);
                getToken(context);
                break;
        }
    }

    private void openTokenDialog(Context context) {
        try {
            Utils.tokenDialog(context, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getToken(Context context) {
        new AllUtils().getToken(context, this);
    }

    @Override
    public void onSuccessRes(boolean isSuccess) {
        if (isSuccess) {
            switch (apicode) {
                case 200:
                    Utils.LoaderUtils.dismissLoader();
                    leadRejectApi();
                    break;
            }
        } else {
            Utils.LoaderUtils.dismissLoader();
            openTokenDialog(context);
        }
    }

    private void setCommissionText(String posting_type){
        try {
            switch(posting_type){
                case "BUY":
                    setBuyCommission();
                    break;
                case "SELL":
                    setSellCommission();
                    break;
                case "RENT":
                    setRentCommission();
                    break;
                case "RENT_OUT":
                    setRentOutCommission();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setBuyCommission(){
        try {
            if(prop_type.equalsIgnoreCase("RESIDENTIAL") || prop_type.equalsIgnoreCase("COMMERCIAL")){
                noti_commission.setText("Commission for New Property : 0%");
                noti_commission_two.setText("Commission for Resale Property : "+commission1 + "%");
            }else if(prop_type.equalsIgnoreCase("LAND")){
                if(sub_property_type != null){
                    if(sub_property_type.equalsIgnoreCase("RESIDENTIAL_ZONE")){
                        noti_commission_two.setText("Commission For Land : "+commission1+"%");
                        noti_commission.setText("Commission For New Gated property : 0%");
                    }else if(sub_property_type.equalsIgnoreCase("COMMERCIAL_ZONE")){
                        noti_commission.setText(commission1 + "% Commission");
                        noti_commission_two.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setSellCommission(){
        try {
            noti_commission.setText(commission1 + "% Commission");
            noti_commission_two.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setRentCommission(){
        try {
            Float comm = Float.valueOf(commission1);
            if(comm == 100.0f) {
                noti_commission.setText("1 Month Commission");
                noti_commission_two.setVisibility(View.GONE);
            }else{
                int number = (int) ((30*comm)/100);
                noti_commission.setText(number+" days Commission");
                noti_commission_two.setVisibility(View.GONE);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    private void setRentOutCommission(){
        try {
            Float comm = Float.valueOf(commission1);
            if(comm == 100.0f) {
                noti_commission.setText("1 Month Commission");
                noti_commission_two.setVisibility(View.GONE);
            }else{
                int number = (int) ((30*comm)/100);
                noti_commission.setText(number+" days Commission");
                noti_commission_two.setVisibility(View.GONE);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}