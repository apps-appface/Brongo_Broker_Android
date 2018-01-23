package appface.brongo.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.text.SpannableStringBuilder;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import appface.brongo.R;
import appface.brongo.model.ApiModel;
import appface.brongo.model.ClientDetailsModel;
import appface.brongo.model.DeviceDetailsModel;
import appface.brongo.model.SignUpModel;
import appface.brongo.model.TokenModel;
import appface.brongo.other.AllUtils;
import appface.brongo.other.NoInternetTryConnectListener;
import appface.brongo.services.MusicService;
import appface.brongo.services.RegistrationIntentService;
import appface.brongo.uiwidget.FlowLayout;
import appface.brongo.util.AppConstants;
import appface.brongo.util.CircleTransform;
import appface.brongo.util.CustomApplicationClass;
import appface.brongo.util.DatabaseHandler;
import appface.brongo.util.RefreshTokenCall;
import appface.brongo.util.RetrofitAPIs;
import appface.brongo.util.RetrofitBuilders;
import appface.brongo.util.Utils;
import de.hdodenhof.circleimageview.CircleImageView;
import ng.max.slideview.SlideView;
import ng.max.slideview.Util;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by panka_000 on 14-03-2016.
 */
public class PushAlertActivity extends Activity implements NoInternetTryConnectListener/*implements View.OnTouchListener */{

    Bundle data;
    int currentTime,matchedProperties;
    private TextView noti_address,noti_bhk,noti_budget,noti_status,noti_prop_type,noti_commission,noti_client_name,noti_reject,noti_progress,noti_client_type,noti_matching,plan_textview;
   private ImageView noti_client_pic;
   private RatingBar noti_ratingbar;
   private FlowLayout push_flowlayout;
   private int taskcompleted = 0;
    private CountDownTimer countDownTimer;
    private String clientMobileNo,reject_reason="";
    private RelativeLayout progress_container;
    private String prop_address,prop_bhk,prop_status,prop_type,budget,prop_client_name,prop_client_type,client_image,prop_id,posting_type,sub_property_type,commission1,plantype;
    private float rating1;
    private MediaPlayer mediaPlayer;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar progressBar;
    private SlideView slideView;
    private BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter1;
    private Vibrator v;
    Intent serviceIntent;
    private Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        data = intent.getExtras();
        if (data != null) {
            prop_address = data.getString("microMarketName");
            clientMobileNo = data.getString("clientMobileNo");
            prop_bhk = data.getString("bedRoomType");
            prop_status = data.getString("propertyStatus");
            prop_type = data.getString("propertyType");
            commission1 = data.getString("commission");
            prop_id = data.getString("propertyId");
            prop_client_name = data.getString("clientName");
            String rating2 = data.getString("rating");
           rating1 = Float.parseFloat(rating2);
            client_image = data.getString("image");
            posting_type = data.getString("postingType");
            budget = data.getString("budget");
            //plantype = data.getString("planType");
            plantype = "PREMIUM";
            sub_property_type = data.getString("subPropertyType");
            String matchedProperties1 = data.getString("matchedProperties");
            //matchedProperties = Integer.parseInt(matchedProperties1);
            matchedProperties = matchCount(posting_type,prop_type);
            matchedProperties = 0;
        }else{
            prop_address=prop_bhk=prop_status=prop_type=budget=prop_client_name=prop_client_type=client_image=prop_id=posting_type=sub_property_type=commission1=plantype="";
        }
        setContentView(R.layout.popup_new_notification);
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
        registerReceiver(broadcastReceiver,intentFilter1);

       /* enableButton.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(boolean active) {
            if(active){
                mediaPlayer.stop();
                progress_container.setVisibility(View.GONE);
                enableButton.setVisibility(View.GONE);
                clientAccept();
                pd.show();
            }
            }
        });*/
        slideView.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                stopService();
                // vibrate the device

                countDownTimer.cancel();
                //stopMedia();
                clientAccept();
                v.cancel();
                slideView.setVisibility(View.GONE);
                // go to a new activity
            }
        });
        noti_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService();
                //stopMedia();
                rejectDialog();
                v.cancel();
              /*  startActivity(new Intent(PushAlertActivity.this,MainActivity.class));
                finish();*/
            }
        });
    }
    private void initialise() {
        context = this;
        mediaPlayer = new MediaPlayer();
        if(isDeviceLocked(context)) {
            unlockScreen();
        }
        serviceIntent = new Intent(context, MusicService.class);
        pref = getSharedPreferences(AppConstants.PREF_NAME,0);
        //mediaPlayer = MediaPlayer.create(context,R.raw.ios7_radiate);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        editor = pref.edit();
        noti_address = (TextView)findViewById(R.id.noti_address);
        noti_bhk = (TextView)findViewById(R.id.noti_bhk);
        push_flowlayout = (FlowLayout)findViewById(R.id.push_flowlayout);
        noti_budget = (TextView)findViewById(R.id.noti_budget);
        noti_status = (TextView)findViewById(R.id.noti_prop_status);
        noti_prop_type = (TextView)findViewById(R.id.noti_prop_type);
        noti_commission = (TextView)findViewById(R.id.noti_commission);
        noti_client_name = (TextView)findViewById(R.id.notification_client_name);
        noti_reject = (TextView)findViewById(R.id.noti_reject);
        plan_textview = (TextView)findViewById(R.id.push_plan_text);
        noti_matching = (TextView)findViewById(R.id.noti_matching_property);
        noti_progress = (TextView)findViewById(R.id.progress_text);
        noti_client_type = (TextView)findViewById(R.id.noti_client_type);
        noti_client_pic = (ImageView)findViewById(R.id.client__notifiation_pic);
        noti_ratingbar = (RatingBar)findViewById(R.id.noti_ratingBar);
        slideView = (SlideView)findViewById(R.id.slideView);
        progressBar = (ProgressBar) findViewById(R.id.noti_progressBar);
        progress_container = (RelativeLayout)findViewById(R.id.container);
        slideView.setVisibility(View.GONE);
        noti_reject.setVisibility(View.GONE);
        if(pref.getString(AppConstants.NOTI_TYPE,"").equalsIgnoreCase("call")) {
            editor.remove(AppConstants.NOTI_TYPE).commit();
            callNotificationApi();
            slideView.setVisibility(View.VISIBLE);
            noti_reject.setVisibility(View.VISIBLE);
            startService(serviceIntent);
            showTimer();
            setValue();
        }else{

        }
    }
    private void addview(String text) {
        if(text != null) {
            if (!text.isEmpty()) {
                try {
                    LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View layout2 = inflater.inflate(R.layout.deal_child, null);
                    TextView deal_textview = (TextView) layout2.findViewById(R.id.deal_text);
                    deal_textview.setText(text);
                    deal_textview.setBackgroundResource(R.drawable.rounded_empty_btn);
                    deal_textview.setTextColor(getResources().getColor(R.color.appColor));
                    push_flowlayout.addView(layout2);
                } catch (Exception e) {
                    String error = e.toString();
                }
            }
        }
    }
    private void clientAccept(){
        if(Utils.isNetworkAvailable(context)) {
            Utils.LoaderUtils.showLoader(context);
            ApiModel.ClientAcceptModel clientAcceptModel = new ApiModel.ClientAcceptModel();
            clientAcceptModel.setClientMobileNo(clientMobileNo);
            //clientAcceptModel.setRentPropertyId(rentPropertyId);
            clientAcceptModel.setBrokerMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
            clientAcceptModel.setPostingType(posting_type.toUpperCase());
            clientAcceptModel.setPropertyId(prop_id);
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            Call<ResponseBody> call = retrofitAPIs.ClentAcceptApi(tokenaccess, "android", deviceId, clientAcceptModel);
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
                                    //finish();
                                   // Utils.showToast(context, message);
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
                                    new AllUtils().getTokenRefresh(context);
                                    clientAccept();
                                } else {
                                    Utils.showToast(context, message);
                                    Intent intent = new Intent(PushAlertActivity.this, MainActivity.class);
                                    intent.putExtra("shouldShowDialog", false);
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    makeScreenOff();
                    Utils.LoaderUtils.dismissLoader();
                    Utils.showToast(context, "Some Problem Occured");
                    goToMainPage();
                }
            });
        }else{
            taskcompleted =100;
            Utils.internetDialog(context,this);
        }
    }
    private void showTimer() {
        currentTime = 1;
        progressBar.setProgress(0);
        progressBar.setMax(120);
        long[] pattern = { 0, 200, 0 };
        v.vibrate(pattern,0);
        //playMedia();
/*        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                currentTime++;
                progressBar.setProgress(currentTime + 1);
                noti_progress.setText((30-currentTime)+"");
                if(currentTime != 30){
                    handler.postDelayed(runnable,1000);
                }else {
                    handler.removeCallbacks(runnable);
                    currentTime++;
                    progressBar.setProgress(currentTime);
                    progressBar.setVisibility(View.INVISIBLE);
                    //countDownTimer.cancel();
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    v.cancel();
                    //clientMissedDialog(prop_client_name,posting_type);
                }
            }
        };
        handler.postDelayed(runnable,0);*/
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                currentTime++;
               /* if(currentTime >44){
                    progressBar.getIndeterminateDrawable()
                            .setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);;
                }*/
                progressBar.setProgress(currentTime + 1);
                if(currentTime > 50){
                    noti_progress.setText("00:0" + (60 - currentTime));
                }
                else {
                    noti_progress.setText("00:" + (60 - currentTime));
                }
            }

            @Override
            public void onFinish() {
                slideView.setVisibility(View.GONE);
                noti_reject.setVisibility(View.GONE);
                currentTime++;
                stopService();
                progressBar.setProgress(currentTime);
                if(currentTime > 50){
                    noti_progress.setText("00:0" + (60 - currentTime));
                }
                else {
                    noti_progress.setText("00:" + (60 - currentTime));
                }
                progressBar.setVisibility(View.INVISIBLE);
                noti_progress.setVisibility(View.INVISIBLE);
                countDownTimer.cancel();
                //stopMedia();
                v.cancel();
                    missedTask();
            }
        };
        countDownTimer.start();
    }
    private void unlockScreen() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }
    private void setValue(){
        if(data != null) {
            budget = Utils.stringToInt(budget);
            addview(prop_address);
            addview(prop_status);
            addview(prop_bhk);
            addview(budget);
            addview(sub_property_type);
            plan_textview.setText(plantype);
            if (plantype.equalsIgnoreCase("")) {
                plan_textview.setVisibility(View.GONE);
            }

            noti_client_type.setText(posting_type.toUpperCase() + "/" + prop_type.toUpperCase());
            String back_color = Utils.getPostingColor(posting_type);
            noti_client_type.setBackgroundColor(Color.parseColor(back_color));
            noti_commission.setText(commission1 + "% Commission");
            if (commission1.equalsIgnoreCase("")) {
                noti_commission.setVisibility(View.GONE);
            }
            noti_client_name.setText(prop_client_name);
            Glide.with(context)
                    .load(client_image)
                    .apply(CustomApplicationClass.getRequestOption(true))
                    .into(noti_client_pic);
            noti_matching.setText(matchedProperties + " matching properties in your inventory");
            noti_ratingbar.setRating(rating1);
        }
    }
    private void playMedia(){
        String audioUri = "android.resource://" + getPackageName() + "/" + R.raw.ios7_radiate;
        try {
          //  mediaPlayer.setDataSource(context, defaultRingtoneUri);
          //  mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
           // mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audio.setMode(AudioManager.MODE_NORMAL);
           /* audio.setStreamVolume(AudioManager.STREAM_RING,
                    90, 0);*/
            mediaPlayer.setDataSource(context,Uri.parse(audioUri));
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            mediaPlayer.setScreenOnWhilePlaying(true);
            //mediaPlayer.setVolume(0.9f,0.9f);
            mediaPlayer.prepare();
          /*  mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    mp.release();
                }
            });*/
            mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
        Log.i("pushActiviry","pause");
        v.cancel();
        if(!isDeviceLocked(context)){
            stopService();
        }
        //stopMedia();
       //stopService();
    }

    private void rejectDialog(){
        final Dialog dialog = new Dialog(PushAlertActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.drawer_background);
        dialog.setContentView(R.layout.reject_popup);
        Window window = dialog.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        //dialog.setCanceledOnTouchOutside(false);
        // dialog.setCancelable(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        TextView client_name = (TextView)dialog.findViewById(R.id.reject_client_name);
        TextView client_address = (TextView)dialog.findViewById(R.id.reject_client_address);
        ImageView client_image1 = (ImageView)dialog.findViewById(R.id.reject_client_image);
        RatingBar reject_ratingbar = (RatingBar)dialog.findViewById(R.id.reject_dialog_ratingBar);
        client_name.setText(prop_client_name);
        client_address.setText(prop_address);
      /*  Glide.with(this).load(client_image).skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE).transform(new CircleTransform(context)).into(client_image1);*/
        Glide.with(context)
                .load(client_image)
                .apply(CustomApplicationClass.getRequestOption(false))
                .into(client_image1);
        final CheckBox checkbox1 = (CheckBox)dialog.findViewById(R.id.reject_checkbox1);
        final CheckBox checkbox2 = (CheckBox)dialog.findViewById(R.id.reject_checkbox2);
        final CheckBox checkbox3 = (CheckBox)dialog.findViewById(R.id.reject_checkbox3);
        Button submit = (Button)dialog.findViewById(R.id.lead_reject_submit);
       reject_ratingbar.setRating(rating1);
        checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkbox2.setChecked(false);
                    checkbox3.setChecked(false);
                    reject_reason = checkbox1.getText().toString();
                }
            }
        });checkbox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkbox1.setChecked(false);
                    checkbox3.setChecked(false);
                    reject_reason = checkbox2.getText().toString();
                }
            }
        });checkbox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    checkbox1.setChecked(false);
                    checkbox2.setChecked(false);
                    reject_reason = checkbox3.getText().toString();
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reject_reason == null || reject_reason.equalsIgnoreCase("")){
                    Utils.showToast(context,"select the reason first");
                }else {
                        leadRejectApi();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void leadRejectApi(){
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
                                    Utils.showToast(context, message);
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
                                    new AllUtils().getTokenRefresh(context);
                                    leadRejectApi();
                                } else {
                                    goToMainPage();
                                    Utils.showToast(context, message);
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
                    Utils.showToast(context, "Some Problem Occured");
                    goToMainPage();
                }
            });
        }else{
            taskcompleted = 200;
            Utils.internetDialog(context,this);
        }
    }

    @Override
    protected void onStop() {
        Log.i("pushActiviry","stop");
        super.onStop();
        v.cancel();
        if(!isDeviceLocked(context)){
            stopService();
        }
        //stopService();
        unregisterReceiver(broadcastReceiver);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("pushActiviry","resume");
        registerReceiver(broadcastReceiver,intentFilter1);
    }

    private int matchCount(String posting_type,String property_type){
        int count = 0;
        DatabaseHandler db = new DatabaseHandler(this);
        Cursor cursor = db.getListItem();
        if (cursor != null) {
            cursor.moveToNext();
            for(int i =0;i<cursor.getCount();i++) {
                String post_type = cursor.getString(cursor.getColumnIndex("postingtype"));
                String prop_type = cursor.getString(cursor.getColumnIndex("propertytype"));
                if (Utils.match_deal(posting_type, post_type, property_type, prop_type)) {
                    count++;
                }
                cursor.moveToNext();
            }
        }
        return count;
    }
    private void generateToken(final int i){
            final String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            final SharedPreferences pref = context.getSharedPreferences(AppConstants.PREF_NAME,0);
            final SharedPreferences.Editor editor = pref.edit();
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            DeviceDetailsModel.TokenGeneration tokenGeneration = new DeviceDetailsModel.TokenGeneration();
         tokenGeneration.setPlatform("android");
         tokenGeneration.setDeviceId(deviceId);
         tokenGeneration.setMobileNo(pref.getString(AppConstants.MOBILE_NUMBER,""));
            Call<TokenModel> call = retrofitAPIs.generateToken(tokenGeneration);
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
                                        // Utils.showToast(context,"Token is generated");
                                        String mobileNo = tokenModel1.getData().get(0).getMobileNo();
                                        String tokenAccess = tokenModel1.getData().get(0).getAccessToken();
                                        editor.putString(AppConstants.DEVICE_ID, deviceId);
                                        editor.putString(AppConstants.TOKEN_ACCESS, tokenAccess);
                                        editor.commit();
                                        if(i == 0){
                                            leadRejectApi();
                                        }else if(i == 1){
                                            clientAccept();
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
                            if(statusCode == 503){
                                Utils.showToast(context,message);
                            }
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
                    Utils.showToast(context,message);
                }
            });
    }

    @Override
    public void onTryReconnect() {
        switch (taskcompleted){
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
        Log.i("pushActiviry","destroy");
        Utils.LoaderUtils.dismissLoader();
    }
    private void callNotificationApi() {
        if(Utils.isNetworkAvailable(context)) {
            Call<ResponseBody> call = null;
            ClientDetailsModel.NotificatioModel notificatioModel = new ClientDetailsModel.NotificatioModel();
            notificatioModel.setBrokerMobileNo(pref.getString(AppConstants.MOBILE_NUMBER,""));
            notificatioModel.setClientMobileNo(clientMobileNo);
            notificatioModel.setPostingType(posting_type);
            notificatioModel.setPropertyId(prop_id);
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            call = retrofitAPIs.notificationApi(notificatioModel);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }else{
            taskcompleted = 1000;
            Utils.internetDialog(context,this);
        }
    }
    public static boolean isDeviceLocked(Context context) {
        boolean isLocked = false;

        // First we check the locked state
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean inKeyguardRestrictedInputMode = keyguardManager.inKeyguardRestrictedInputMode();

        if (inKeyguardRestrictedInputMode) {
            isLocked = true;

        } else {
            // If password is not set in the settings, the inKeyguardRestrictedInputMode() returns false,
            // so we need to check if screen on for this case

            PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                isLocked = !powerManager.isInteractive();
            } else {
                //noinspection deprecation
                isLocked = !powerManager.isScreenOn();
            }
        }
        return isLocked;
    }
    private void missedTask(){
        Intent intent = new Intent(PushAlertActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("missedDialog", true);
        intent.putExtra("clientName",prop_client_name);
        intent.putExtra("postType",posting_type);
        startActivity(intent);
        makeScreenOff();
        finish();
    }
    private void stopService(){
        stopService(new Intent(context,MusicService.class));
    }
    private void makeScreenOff(){
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    private void stopMedia(){
        if(mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
    private void goToMainPage(){
        Intent intent = new Intent(PushAlertActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }
}