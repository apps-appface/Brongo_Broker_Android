package appface.brongo.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kyleduo.switchbutton.SwitchButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import appface.brongo.R;
import appface.brongo.model.ApiModel;
import appface.brongo.other.AllUtils;
import appface.brongo.other.NoInternetTryConnectListener;
import appface.brongo.util.AppConstants;
import appface.brongo.util.RefreshTokenCall;
import appface.brongo.util.RetrofitAPIs;
import appface.brongo.util.RetrofitBuilders;
import appface.brongo.util.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static appface.brongo.R.id.progress;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment implements NoInternetTryConnectListener{
    private SwitchButton offer_noti_switch,builder_noti_switch,noti_sound_switch;
    private static SeekBar sound_seekbar;
    private int i=0;
    private TextView toolbar_title;
    private Toolbar toolbar;
    private Context context;
    private ImageView edit_icon,delete_icon,add_icon;
    private int taskcompleted = 0;
    private static AudioManager audio;
    private LinearLayout setting_parent;
    private SharedPreferences pref;
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
        context = getActivity();
        pref = getActivity().getSharedPreferences(AppConstants.PREF_NAME,0);
        editor = pref.edit();
        audio = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
        offer_noti_switch = (SwitchButton)view.findViewById(R.id.setting_offer_btn);
        setting_parent = (LinearLayout)view.findViewById(R.id.setting_linear);
        builder_noti_switch = (SwitchButton)view.findViewById(R.id.setting_builder_btn);
        edit_icon = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_edit);
        delete_icon = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_delete);
        add_icon = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_add);
        edit_icon.setVisibility(View.GONE);
        delete_icon.setVisibility(View.GONE);
        add_icon.setVisibility(View.GONE);
        toolbar_title = (TextView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.inventory_toolbar_title);
        toolbar = (Toolbar)getActivity().findViewById(R.id.inventory_toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar_title.setText("Settings");
        setting_parent.setVisibility(View.INVISIBLE);
        noti_sound_switch = (SwitchButton)view.findViewById(R.id.setting_noti_sound);
        sound_seekbar = (SeekBar)view.findViewById(R.id.setting_seekbar);
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
    }
    private void setListeners(){
        offer_noti_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    offer_noti_switch.setThumbColorRes(R.color.appColor);
                    offerNoti = true;
                    if(i==1) {
                            updateSetings(offerNoti, builderNoti, notiSound);
                    }
                    // pd.show(};
                }else{
                    offer_noti_switch.setThumbColorRes(R.color.switch_off);
                    offerNoti = false;
                    if(i==1) {
                            updateSetings(offerNoti, builderNoti, notiSound);
                    }
                   // pd.show();
                }
            }
        });
        builder_noti_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    builderNoti = true;
                    builder_noti_switch.setThumbColorRes(R.color.appColor);
                    if(i==1) {
                            updateSetings(offerNoti, builderNoti, notiSound);
                    }
                    // pd.show(};
                }else{
                    builder_noti_switch.setThumbColorRes(R.color.switch_off);
                    builderNoti = false;
                    if(i==1) {
                            updateSetings(offerNoti, builderNoti, notiSound);
                    }
                    // pd.show();
                }
            }
        });
        noti_sound_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    notiSound = true;
                    noti_sound_switch.setThumbColorRes(R.color.appColor);
                    sound_seekbar.setProgress(1);
                    setVolume(1);
                    // pd.show(};
                }else{
                    noti_sound_switch.setThumbColorRes(R.color.switch_off);
                    notiSound = false;
                    sound_seekbar.setProgress(0);
                    setVolume(0);
                    // pd.show();
                }
            }
        });
        sound_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setVolume(progress);
                if(progress == 0){
                    noti_sound_switch.setChecked(false);
                }else{
                    noti_sound_switch.setChecked(true);
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
    private void setView(){
        builder_noti_switch.setChecked(pref.getBoolean(AppConstants.BUILDER_NOTI,true));
        offer_noti_switch.setChecked(pref.getBoolean(AppConstants.OFFER_NOTI,true));
        noti_sound_switch.setChecked(pref.getBoolean(AppConstants.NOTI_SOUND,true));
        i =1;
        setting_parent.setVisibility(View.VISIBLE);
        Utils.LoaderUtils.dismissLoader();
    }

    private void fetchSettings(){
        if(Utils.isNetworkAvailable(context)) {
            Utils.LoaderUtils.showLoader(context);
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
            Call<ApiModel.SettingPlanModel> call = retrofitAPIs.getSettingsApi(tokenaccess, "android", deviceId, mobileNo);
            call.enqueue(new Callback<ApiModel.SettingPlanModel>() {
                @Override
                public void onResponse(Call<ApiModel.SettingPlanModel> call, Response<ApiModel.SettingPlanModel> response) {
                    Utils.LoaderUtils.dismissLoader();
                    if (response != null) {
                        if (response.isSuccessful()) {
                            ApiModel.SettingPlanModel settingPlanModel = response.body();
                            int statusCode = settingPlanModel.getStatusCode();
                            String message = settingPlanModel.getMessage();
                            if (statusCode == 200 && message.equalsIgnoreCase("")) {
                                offerNoti = settingPlanModel.getData().isOffers();
                                builderNoti = settingPlanModel.getData().isBuilderProject();
                                notiSound = settingPlanModel.getData().isNotificationSound();
                                // editor.putBoolean(AppConstants.NOTI_SOUND,notiSound);
                                editor.putBoolean(AppConstants.OFFER_NOTI, offerNoti);
                                editor.putBoolean(AppConstants.BUILDER_NOTI, builderNoti);
                                editor.commit();
                            }
                            // referAdapter.notifyDataSetChanged();
                        } else {
                            String responseString = null;
                            try {
                                responseString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                int statusCode = jsonObject.optInt("statusCode");
                                String message = jsonObject.optString("message");
                                if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                    new AllUtils().getTokenRefresh(context);
                                    fetchSettings();
                                } else {
                                    Utils.showToast(context, message);
                                }
                           /* if(pd.isShowing()) {
                                pd.dismiss();
                            }*/
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
                    Toast.makeText(context, "Some Problem Occured", Toast.LENGTH_SHORT).show();
                /*if(pd.isShowing()) {
                    pd.dismiss();
                }*/
                }
            });
        }else{
            taskcompleted = 100;
            Utils.internetDialog(context,this);
            }
    }

    private void updateSetings(final boolean offerNoti, final boolean builderNoti, final boolean notiSound){
        if(Utils.isNetworkAvailable(context)) {
            Utils.LoaderUtils.showLoader(context);
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
                    if (response != null) {
                        String responseString = null;
                        if (response.isSuccessful()) {
                            ApiModel.SettingPlanModel settingPlanModel = response.body();
                            int statusCode = settingPlanModel.getStatusCode();
                            String message = settingPlanModel.getMessage();
                            if (statusCode == 200 && message.equalsIgnoreCase("")) {
                                Utils.showToast(context, "Setting updated successfully");
                                boolean offerNoti1 = settingPlanModel.getData().isOffers();
                                boolean builderNoti1 = settingPlanModel.getData().isBuilderProject();
                                boolean notiSound1 = settingPlanModel.getData().isNotificationSound();
                                editor.putBoolean(AppConstants.NOTI_SOUND, notiSound1);
                                editor.putBoolean(AppConstants.OFFER_NOTI, offerNoti1);
                                editor.putBoolean(AppConstants.BUILDER_NOTI, builderNoti1);
                                editor.commit();
                            }
                        } else {
                            try {
                                responseString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                String message = jsonObject.optString("message");
                                int statusCode = jsonObject.optInt("statusCode");
                                if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                    new AllUtils().getTokenRefresh(context);
                                    updateSetings(offerNoti, builderNoti, notiSound);
                                } else {
                                    Utils.showToast(context, message);
                                }
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Utils.LoaderUtils.dismissLoader();
                }

                @Override
                public void onFailure(Call<ApiModel.SettingPlanModel> call, Throwable t) {
                    Utils.showToast(context, "Some Problem Occured");
                    Utils.LoaderUtils.dismissLoader();
                }
            });
        }{
            taskcompleted = 200;
            Utils.internetDialog(context,this);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        Utils.LoaderUtils.dismissLoader();
    }
    private void setVolume(int progress){
        int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_RING);
        audio.setStreamVolume(AudioManager.STREAM_RING, progress, 0);
    }
    private void setProgress(){
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_RING);
        int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_RING);
        sound_seekbar.setMax(maxVolume);
        sound_seekbar.incrementProgressBy(1);
        sound_seekbar.setProgress(currentVolume);
        if (currentVolume > 0) {
            noti_sound_switch.setVisibility(View.VISIBLE);
            //noti_sound_switch.setChecked(true);
        }else{
            //noti_sound_switch.setChecked(false);
            noti_sound_switch.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTryReconnect() {
        fetchSettings();
    }
    public static void myOnKeyDown(int key_code){
        if(key_code == KeyEvent.KEYCODE_VOLUME_DOWN || key_code == KeyEvent.KEYCODE_VOLUME_UP) {
            int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_RING);
            int currentVolume = audio.getStreamVolume(AudioManager.STREAM_RING);
            sound_seekbar.setProgress(currentVolume);
        }
        //do whatever you want here
    }
    private static void setKeyValue(){

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.LoaderUtils.dismissLoader();
    }
}
