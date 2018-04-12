package in.brongo.brongo_broker.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.model.ApiModel;
import in.brongo.brongo_broker.other.AllUtils;
import in.brongo.brongo_broker.other.NoInternetTryConnectListener;
import in.brongo.brongo_broker.other.NoTokenTryListener;
import in.brongo.brongo_broker.util.AppConstants;
import in.brongo.brongo_broker.util.RetrofitAPIs;
import in.brongo.brongo_broker.util.RetrofitBuilders;
import in.brongo.brongo_broker.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment implements NoInternetTryConnectListener,NoTokenTryListener,AllUtils.test{
    private Switch offer_noti_switch,builder_noti_switch,noti_sound_switch;
    private static SeekBar sound_seekbar;
    private int i=0;
    private TextView toolbar_title;
    private Toolbar toolbar;
    private Context context;
    private RelativeLayout parentLayout;
    private ImageView edit_icon,delete_icon,add_icon;
    private int taskcompleted = 0;
    private static AudioManager audio;
    private LinearLayout setting_parent;
    private SharedPreferences pref;
    private boolean isLoader = false,isUpdateRequired=false;
    private boolean offerNoti,builderNoti,notiSound;
    private SharedPreferences.Editor editor;
    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        initialise(view);
        return view;
    }
    private void initialise(View view){
        try {
            context = getActivity();
            parentLayout = getActivity().findViewById(R.id.menu_parent_relative);
            pref = getActivity().getSharedPreferences(AppConstants.PREF_NAME,0);
            editor = pref.edit();
            audio = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
            offer_noti_switch = view.findViewById(R.id.setting_offer_btn);
            setting_parent = view.findViewById(R.id.setting_linear);
            builder_noti_switch = view.findViewById(R.id.setting_builder_btn);
            edit_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_edit);
            delete_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_delete);
            add_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_add);
            edit_icon.setVisibility(View.GONE);
            delete_icon.setVisibility(View.GONE);
            add_icon.setVisibility(View.GONE);
            toolbar_title = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.inventory_toolbar_title);
            toolbar = getActivity().findViewById(R.id.inventory_toolbar);
            toolbar.setVisibility(View.VISIBLE);
            toolbar_title.setText("Settings");
            setting_parent.setVisibility(View.INVISIBLE);
            noti_sound_switch = view.findViewById(R.id.setting_noti_sound);
            sound_seekbar = view.findViewById(R.id.setting_seekbar);
     /*   sound_seekbar.incrementProgressBy(10);*/
            setProgress();
       /* sound_seekbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });*/
            fetchSettings();
            setListeners();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setListeners(){
        offer_noti_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    offerNoti = true;
                    if(isUpdateRequired) {

                            updateSetings(offerNoti, builderNoti, notiSound);
                    }
                }else{
                    offerNoti = false;
                    if(isUpdateRequired) {
                            updateSetings(offerNoti, builderNoti, notiSound);
                    }
                }
            }
        });
        builder_noti_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    builderNoti = true;
                    if(isUpdateRequired) {
                        updateSetings(offerNoti, builderNoti, notiSound);
                    }
                }else{
                    builderNoti = false;
                    if(isUpdateRequired) {
                        updateSetings(offerNoti, builderNoti, notiSound);
                    }
                }
            }
        });
        noti_sound_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    notiSound = true;
                    sound_seekbar.setProgress(1);
                    setVolume(1);
                }else{
                    notiSound = false;
                    if(sound_seekbar.getProgress() != 0) {
                        sound_seekbar.setProgress(0);
                    }
                    setVolume(0);
                }
            }
        });
        sound_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setVolume(progress);
                if(progress == 0){
                   if(noti_sound_switch.isChecked()) {
                       noti_sound_switch.setChecked(false);
                   }
                }else{
                    if(!noti_sound_switch.isChecked()) {
                        noti_sound_switch.setChecked(true);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    private void
    setView(){
        builder_noti_switch.setChecked(pref.getBoolean(AppConstants.BUILDER_NOTI,true));
        offer_noti_switch.setChecked(pref.getBoolean(AppConstants.OFFER_NOTI,true));
        setting_parent.setVisibility(View.VISIBLE);
        Utils.LoaderUtils.dismissLoader();
        isLoader = false;
        isUpdateRequired = true;
    }

    private void fetchSettings(){
        try {
            if(Utils.isNetworkAvailable(context)) {
                if(!isLoader) {
                    Utils.LoaderUtils.showLoader(context);
                    isLoader = true;
                }
                RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
                String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
                String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
                String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
                Call<ApiModel.SettingPlanModel> call = retrofitAPIs.getSettingsApi(tokenaccess, "android", deviceId, mobileNo);
                call.enqueue(new Callback<ApiModel.SettingPlanModel>() {
                    @Override
                    public void onResponse(Call<ApiModel.SettingPlanModel> call, Response<ApiModel.SettingPlanModel> response) {
                        Utils.LoaderUtils.dismissLoader();
                        isLoader = false;
                        if (response != null) {
                            if (response.isSuccessful()) {
                                ApiModel.SettingPlanModel settingPlanModel = response.body();
                                int statusCode = settingPlanModel.getStatusCode();
                                String message = settingPlanModel.getMessage();
                                if (statusCode == 200 && message.equalsIgnoreCase("")) {
                                    offerNoti = settingPlanModel.getData().isOffers();
                                    builderNoti = settingPlanModel.getData().isBuilderProject();
                                    editor.putBoolean(AppConstants.OFFER_NOTI, offerNoti);
                                    editor.putBoolean(AppConstants.BUILDER_NOTI, builderNoti);
                                    editor.commit();
                                }
                            } else {
                                String responseString = null;
                                try {
                                    responseString = response.errorBody().string();
                                    JSONObject jsonObject = new JSONObject(responseString);
                                    int statusCode = jsonObject.optInt("statusCode");
                                    String message = jsonObject.optString("message");
                                    if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                       openTokenDialog(context);
                                    }
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            setView();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiModel.SettingPlanModel> call, Throwable t) {
                        Utils.LoaderUtils.dismissLoader();
                        isLoader = false;
                        if (t.getMessage().equals("Too many follow-up requests: 21")) {
                           openTokenDialog(context);
                        }else {
                            Utils.showToast(context, t.getLocalizedMessage().toString(), "Failure");
                        }
                    }
                });
            }else{
                taskcompleted = 100;
                Utils.internetDialog(context,this);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateSetings(final boolean offerNoti, final boolean builderNoti, final boolean notiSound){
        try {
            if(Utils.isNetworkAvailable(context)) {
                 if(!isLoader) {
                                Utils.LoaderUtils.showLoader(context);
                                isLoader = true;
                            }
                ApiModel.SettingPlanObject settingPlanObject = new ApiModel.SettingPlanObject();
                settingPlanObject.setMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
                settingPlanObject.setOffers(offerNoti);
                settingPlanObject.setBuilderProject(builderNoti);
                settingPlanObject.setNotificationSound(notiSound);
                RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
                String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
                String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
                Call<ApiModel.SettingPlanModel> call = retrofitAPIs.updateSettingApi(tokenaccess, "android", deviceId, settingPlanObject);
                call.enqueue(new Callback<ApiModel.SettingPlanModel>() {
                    @Override
                    public void onResponse(Call<ApiModel.SettingPlanModel> call, Response<ApiModel.SettingPlanModel> response) {
                        Utils.LoaderUtils.dismissLoader();
                        isLoader = false;
                        if (response != null) {
                            String responseString = null;
                            if (response.isSuccessful()) {
                                ApiModel.SettingPlanModel settingPlanModel = response.body();
                                int statusCode = settingPlanModel.getStatusCode();
                                String message = settingPlanModel.getMessage();
                                if (statusCode == 200 && message.equalsIgnoreCase("")) {
                                    Utils.setSnackBar(parentLayout, "Setting updated successfully");
                                    boolean offerNoti1 = settingPlanModel.getData().isOffers();
                                    boolean builderNoti1 = settingPlanModel.getData().isBuilderProject();
                                    editor.putBoolean(AppConstants.OFFER_NOTI, offerNoti1);
                                    editor.putBoolean(AppConstants.BUILDER_NOTI, builderNoti1);
                                    editor.commit();
                                }
                            } else {
                                try {
                                    isUpdateRequired = false;
                                    setView();
                                    responseString = response.errorBody().string();
                                    JSONObject jsonObject = new JSONObject(responseString);
                                    String message = jsonObject.optString("message");
                                    int statusCode = jsonObject.optInt("statusCode");
                                    if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                        new AllUtils().getTokenRefresh(context);
                                        Utils.setSnackBar(parentLayout,"Please try again");
                                    } else {
                                        Utils.setSnackBar(parentLayout,message);
                                    }
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiModel.SettingPlanModel> call, Throwable t) {
                        isUpdateRequired = false;
                        setView();
                        if (t.getMessage().equals("Too many follow-up requests: 21")) {
                            new AllUtils().getTokenRefresh(context);
                            Utils.setSnackBar(parentLayout,"Please try again");
                        }else {
                            Utils.showToast(context, t.getLocalizedMessage().toString(), "Failure");
                        }
                        Utils.LoaderUtils.dismissLoader();
                        isLoader = false;
                    }
                });
            }else{
                taskcompleted = 200;
                Utils.internetDialog(context,this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        Utils.LoaderUtils.dismissLoader();
        isLoader = false;
    }
    private void setVolume(int progress){
        try {
            int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_RING);
            audio.setStreamVolume(AudioManager.STREAM_RING, progress, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setProgress(){
        try {
            int currentVolume = audio.getStreamVolume(AudioManager.STREAM_RING);
            int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_RING);
            sound_seekbar.setMax(maxVolume);
            sound_seekbar.incrementProgressBy(1);
            sound_seekbar.setProgress(currentVolume);
            noti_sound_switch.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTryReconnect() {
        switch (taskcompleted){
            case 100:
                fetchSettings();
                break;
            case 200:
                updateSetings(offerNoti, builderNoti, notiSound);
                break;
        }
    }
    public static void myOnKeyDown(int key_code){
        try {
            if(key_code == KeyEvent.KEYCODE_VOLUME_DOWN || key_code == KeyEvent.KEYCODE_VOLUME_UP) {
                Handler handler = new Handler();
              handler.postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       //Do something after 100ms
                   }
               }, 200);
                int currentVolume = audio.getStreamVolume(AudioManager.STREAM_RING);
                sound_seekbar.setProgress(currentVolume);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //do whatever you want here
    }
    private static void setKeyValue(){

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.LoaderUtils.dismissLoader();
        isLoader = false;
    }
    private void openTokenDialog(Context context){
        try {
            if(!getActivity().isFinishing()) {
                Utils.tokenDialog(context, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTryRegenerate() {
       getToken(context);
    }
    private void getToken(Context context){
        try {
            new AllUtils().getToken(context,this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccessRes(boolean isSuccess) {
        if(isSuccess){
            fetchSettings();
        }else{
            Utils.LoaderUtils.dismissLoader();
            openTokenDialog(context);
        }
    }
}
