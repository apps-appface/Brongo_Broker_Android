package appface.brongo.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applozic.mobicomkit.api.MobiComKitConstants;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.PushNotificationTask;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.api.account.user.UserLoginTask;
import com.applozic.mobicomkit.api.account.user.UserLogoutTask;
import com.applozic.mobicomkit.api.conversation.database.MessageDatabaseService;
import com.applozic.mobicomkit.uiwidgets.ApplozicSetting;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.bumptech.glide.Glide;
import com.kyleduo.switchbutton.SwitchButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import appface.brongo.R;
import appface.brongo.adapter.CarouselPagerAdapter;
import appface.brongo.adapter.MainAdapter;
import appface.brongo.fragment.ItemFragment;
import appface.brongo.model.ApiModel;
import appface.brongo.model.DeviceDetailsModel;
import appface.brongo.model.TokenModel;
import appface.brongo.other.AllUtils;
import appface.brongo.other.NoInternetTryConnectListener;
import appface.brongo.services.RegistrationIntentService;
import appface.brongo.uiwidget.WrapContentViewPager;
import appface.brongo.util.AppConstants;
import appface.brongo.util.CustomApplicationClass;
import appface.brongo.util.DatabaseHandler;
import appface.brongo.util.ResideMenu;
import appface.brongo.util.ResideMenuItem;
import appface.brongo.util.RetrofitAPIs;
import appface.brongo.util.RetrofitBuilders;
import appface.brongo.util.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,ItemFragment.ViewListener,NoInternetTryConnectListener{
    int pageMargin,currentPage = 0;
    private float  RATIO_SCALE = 0.3f, rating1 = 0.0f;;
    Timer timer;
    private int taskcompleted =0;
    final long DELAY_MS = 500,PERIOD_MS = 3000;//delay in milliseconds before task is to be executed
    private Button open_deal_buy_btn,referral_btn,open_deal_sell_btn;
    private boolean doubleBackToExitPressedOnce = false;
    private LinearLayout no_deal_linear,deal_linear,linearLayout2,switch_linear,drawer_layout;
    private RelativeLayout pager_linear,cutoff_relative;
    private ArrayList<ApiModel.BuyAndRentModel> buyAndRentList,sellAndRentList;
    public final static int LOOPS = 1;
    public CarouselPagerAdapter adapter1;
    private MainAdapter adapter2;
    public static WrapContentViewPager pager,pager1;
    private Bundle data;
    private String drop_reason = "";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Toolbar toolbar;
    private int btn_click = 0;
    private ImageView tool_navbar_image,noti_message,noti_icon;
    FragmentManager fragmentManager;
    Context context = this;
    private boolean isLoader = false,isActiveCalled = false;
    private TextView switch_text,chat_count,home_commission,home_name,home_plan,home_pot_commission,homerating,noti_count;
    private SwitchButton switchButton;
    private FloatingActionButton floatingActionButton;
    private ResideMenu resideMenu;
    private ResideMenuItem itemSettings,itemHistorical,itemInventory,itemPayment,itemHelp,itemLogout,itemRating;
    private RatingBar main_ratingbar;
    DatabaseHandler db = new DatabaseHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences(AppConstants.PREF_NAME, 0);
        editor = pref.edit();
       onNewIntent(getIntent());
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
    /*    Intent serviceIntent = new Intent(context, RegistrationIntentService.class);
        startService(serviceIntent);*/
     //   getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(!pref.getBoolean(AppConstants.DEVICE_TOKEN_UPDATED,false)){
            Intent serviceIntent = new Intent(context, RegistrationIntentService.class);
            serviceIntent.putExtra("key",100);
            startService(serviceIntent);
        }
        setContentView(R.layout.activity_home);
        /*if(MobiComUserPreference.getInstance(this).isLoggedIn()){
            int i=100;
        }*/
        initialise();


    }

    private void initialise() {
        drawer_layout = (LinearLayout)findViewById(R.id.drawer_layout);
        fragmentManager = getSupportFragmentManager();
        home_name = (TextView) findViewById(R.id.main_user_name);
        home_plan = (TextView) findViewById(R.id.main_plan);
        home_commission = (TextView) findViewById(R.id.closed_commission_text);
        resideMenu = new ResideMenu(this,fragmentManager);
        homerating = (TextView) findViewById(R.id.main_rating);
        home_pot_commission = (TextView) findViewById(R.id.opened_commission_text);
        linearLayout2 = (LinearLayout)findViewById(R.id.linearLayout2);
        switch_linear = (LinearLayout)findViewById(R.id.linear2);
       main_ratingbar = (RatingBar)findViewById(R.id.main_ratingBar);
        open_deal_buy_btn = (Button) findViewById(R.id.buy_rent);
        cutoff_relative = (RelativeLayout)findViewById(R.id.cutoff_relative);
        open_deal_sell_btn = (Button) findViewById(R.id.sell_rentout);
        no_deal_linear = (LinearLayout)findViewById(R.id.no_deal_linear);
        pager_linear = (RelativeLayout)findViewById(R.id.activity_main_layout);
        deal_linear = (LinearLayout)findViewById(R.id.deal_linear);
        buyAndRentList = new ArrayList();
        sellAndRentList = new ArrayList();
        referral_btn =(Button)findViewById(R.id.refer_broker_btn);
        //set page margin between pages for viewpager
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        pageMargin = ((metrics.widthPixels / 20) * 2);
     //   pager.setPageMargin(-pageMargin);

        floatingActionButton = (FloatingActionButton)findViewById(R.id.action_btn);
        toolbar = (Toolbar) findViewById(R.id.toolBar1);
        tool_navbar_image = (ImageView)toolbar.findViewById(R.id.side_navi_bar);
        noti_message = (ImageView)toolbar.findViewById(R.id.toolbar_message_icon);
        noti_icon = (ImageView)toolbar.findViewById(R.id.toolbar_notification_icon);
        noti_count = (TextView)toolbar.findViewById(R.id.notification_count);
        switchButton = (SwitchButton)findViewById(R.id.active_btn);
        switch_text =(TextView)findViewById(R.id.switch_status_text);
        chat_count = (TextView)toolbar.findViewById(R.id.message_count);
        // attach to current activity;
        setUpMenu();
        pager = (WrapContentViewPager)findViewById(R.id.myviewpager);
        pager1 = (WrapContentViewPager)findViewById(R.id.myviewpager1);
        pager.setPageMargin(-pageMargin);
        adapter1 = new CarouselPagerAdapter(this, getSupportFragmentManager(),buyAndRentList,this,pager);
        pager.setAdapter(adapter1);
        pager.setOffscreenPageLimit(3);
        pager1.setPageMargin(-pageMargin);
        adapter2 = new MainAdapter(this, getSupportFragmentManager(),sellAndRentList,this,pager1);
        pager1.setAdapter(adapter2);
        pager1.setOffscreenPageLimit(3);
        //setButtonText();
       /* pager.setPageTransformer(true, new ZoomOutPageTransformer());
        pager1.setPageTransformer(true, new ZoomOutPageTransformer());*/
      //generateToken(0);
    callHomeProfileApi();
        setView();
        setListener();
    }
    private void setUpMenu() {
        //set background of menu
        resideMenu.setBackgroundResource(R.drawable.drawer_background);
        String firstName = pref.getString(AppConstants.FIRST_NAME,"");
        String lastName = pref.getString(AppConstants.LAST_NAME,"");
        String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER,"");
        String microMarket = pref.getString(AppConstants.MICROMARKET_NAME,"");
        String plan = pref.getString(AppConstants.PLAN_TYPE,"");
        resideMenu.setMenuProfile(firstName + " "+lastName,plan,mobileNo+","+microMarket,pref.getString(AppConstants.USER_PIC,""),pref.getFloat(AppConstants.RATING,0.0f));
        resideMenu.attachToActivity(this);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
        // create menu items;
        itemInventory  = new ResideMenuItem(this, R.drawable.ic_inventory_icon,  "INVENTORY");
        itemHistorical     = new ResideMenuItem(this, R.drawable.ic_deal_icon,     "HISTORICAL DEALS");
        itemPayment = new ResideMenuItem(this, R.drawable.ic_subscription_icon, "PAYMENTS & SUBSCRIPTIONS");
        itemHelp     = new ResideMenuItem(this, R.drawable.ic_faq_icon,     "HELP & FAQ'S");
        itemSettings     = new ResideMenuItem(this, R.drawable.ic_settings_icon,     "SETTINGS");
        itemRating     = new ResideMenuItem(this, R.drawable.ic_ratings,     "RATINGS & REVIEWS");
        itemLogout = new ResideMenuItem(this,R.drawable.ic_logout_icon,"LOG OUT");
        itemLogout.setOnClickListener(this);
        itemRating.setOnClickListener(this);
        itemHistorical.setOnClickListener(this);
        itemInventory.setOnClickListener(this);
        itemPayment.setOnClickListener(this);
        itemHelp.setOnClickListener(this);
        itemSettings.setOnClickListener(this);
        resideMenu.addMenuItem(itemInventory, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemHistorical, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemPayment, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemHelp, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemSettings, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemRating, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemLogout,ResideMenu.DIRECTION_LEFT);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);
        tool_navbar_image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });
    }

 /*   @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }*/

    @Override
    public void onClick(View view) {
        if(view == itemHistorical){
            Utils.setAlphaAnimation(itemHistorical,this);
            Intent i = new Intent(MainActivity.this, Menu_Activity.class);
            i.putExtra("frgToLoad", "HistoricalFragment");
            startActivity(i);
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        }else if(view == itemInventory){
            Utils.setAlphaAnimation(itemInventory,this);
            Intent i = new Intent(MainActivity.this, Menu_Activity.class);
            i.putExtra("frgToLoad", "InventoryListFragment");
            startActivity(i);
        }else if(view == itemHelp){
            Utils.setAlphaAnimation(itemHelp,this);
            Intent i = new Intent(MainActivity.this, Menu_Activity.class);
            i.putExtra("frgToLoad", "HelpFragment");
            startActivity(i);
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        }else if(view == itemSettings){
            Utils.setAlphaAnimation(itemSettings,this);
            Intent i = new Intent(MainActivity.this, Menu_Activity.class);
            i.putExtra("frgToLoad", "SettingFragment");
            startActivity(i);
        }else if(view == itemLogout){
            Utils.setAlphaAnimation(itemLogout,this);
                logOutDialog();
        }else if(view == itemPayment){
            Utils.setAlphaAnimation(itemPayment,this);
            Intent i = new Intent(MainActivity.this, Menu_Activity.class);
            i.putExtra("frgToLoad", "SubscriptionFragment");
            startActivity(i);
        }else if(view == itemRating){
            Utils.setAlphaAnimation(itemPayment,this);
            Intent i = new Intent(MainActivity.this, Menu_Activity.class);
            i.putExtra("frgToLoad", "ReviewFragment");
            startActivity(i);
        }
        resideMenu.closeMenu();
    }
    // What good method is to access resideMenuï¼Ÿ
    public ResideMenu getResideMenu(){
        return resideMenu;
    }

   @Override
   public void onBackPressed() {
       if (doubleBackToExitPressedOnce) {
           super.onBackPressed();
           finishAffinity();
       }else{
           Utils.showToast(this, "click back button to exit");
       }

       this.doubleBackToExitPressedOnce = true;
       new Handler().postDelayed(new Runnable() {

           @Override
           public void run() {
               doubleBackToExitPressedOnce=false;
           }
       }, 2000);
   }
    @Override
    protected void onResume() {
        super.onResume();
        context.registerReceiver(mMessageReceiver, new IntentFilter("activity_refresh"));
        LocalBroadcastManager.getInstance(this).registerReceiver(unreadCountBroadcastReceiver, new IntentFilter(MobiComKitConstants.APPLOZIC_UNREAD_COUNT));
    }
    public void callHomeProfileApi(){
        if(Utils.isNetworkAvailable(context)) {
            if(!isLoader) {
                Utils.LoaderUtils.showLoader(context);
                isLoader = true;
            }
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
            Call<ApiModel.HomeProfileModel> call = retrofitAPIs.getHomeProfile(tokenaccess, "android", deviceId, mobileNo);
            call.enqueue(new Callback<ApiModel.HomeProfileModel>() {
                @Override
                public void onResponse(Call<ApiModel.HomeProfileModel> call, Response<ApiModel.HomeProfileModel> response) {
                    populateArrayList1();
                    if (response != null) {
                        if (response.isSuccessful()) {
                            ApiModel.HomeProfileModel homeProfileModel = response.body();
                            int statusCode = homeProfileModel.getStatusCode();
                            String message = homeProfileModel.getMessage();
                            if (statusCode == 200 && message.equalsIgnoreCase("")) {
                                editor.putString(AppConstants.FIRST_NAME, homeProfileModel.getData().get(0).getFirstName());
                                editor.putString(AppConstants.LAST_NAME, homeProfileModel.getData().get(0).getLastName());
                                editor.putString(AppConstants.USER_PIC, homeProfileModel.getData().get(0).getBrokerImage());
                                editor.putFloat(AppConstants.RATING, homeProfileModel.getData().get(0).getRating());
                                editor.putInt(AppConstants.NO_OF_CLIENT_RATED, homeProfileModel.getData().get(0).getNoOfClientsRated());
                                editor.putString(AppConstants.PLAN_TYPE, homeProfileModel.getData().get(0).getPlanType());
                                editor.putFloat(AppConstants.COMMISSION, (float) homeProfileModel.getData().get(0).getcCommission());
                                editor.putString(AppConstants.REFERRAL_ID, homeProfileModel.getData().get(0).getReferralId());
                                editor.putString(AppConstants.IMAGE_BASE_URL, homeProfileModel.getData().get(0).getImageBaseurl());
                                editor.putInt(AppConstants.REVIEW_COUNT,homeProfileModel.getData().get(0).getNoOfClientsRated());
                                editor.putString(AppConstants.PREMIUM_ID, homeProfileModel.getData().get(0).getPremiumPlan());
                                editor.putInt(AppConstants.NOTIFICATION_BADGES, homeProfileModel.getData().get(0).getNotificationsBadge());
                                editor.putString(AppConstants.USER_STATUS, homeProfileModel.getData().get(0).getIsActive());
                                editor.putString(AppConstants.MICROMARKET_NAME, homeProfileModel.getData().get(0).getMicroMarketName());
                                editor.putBoolean("homedata", true);
                                editor.commit();
                                if (!pref.getBoolean(AppConstants.CHAT_LOGIN, false)) {
                                    RegisterWithApplozic();
                                }
                            }
                        } else {
                            String responseString = null;
                            try {
                                responseString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                int statusCode = jsonObject.optInt("statusCode");
                                String message = jsonObject.optString("message");
                                if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                    new AllUtils().getTokenRefresh(context);
                                    callHomeProfileApi();
                                } else {
                                    Utils.LoaderUtils.dismissLoader();
                                    Utils.showToast(context, message);
                                }
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    //setUpMenu();
                }

                @Override
                public void onFailure(Call<ApiModel.HomeProfileModel> call, Throwable t) {
                    Utils.LoaderUtils.dismissLoader();
                    isLoader = false;
                    Utils.showToast(context, "Some Problem Occured");
                }
            });
        }else{
            taskcompleted=100;
            Utils.internetDialog(context,this);
        }
    }
    private void callActiveApi(final String status){
        if(Utils.isNetworkAvailable(context)) {
            if(!isLoader) {
                Utils.LoaderUtils.showLoader(context);
                isLoader = true;
            }
            ApiModel.UpdateStatusModel updateStatusModel = new ApiModel.UpdateStatusModel();
            updateStatusModel.setUserId("");
            updateStatusModel.setMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
            updateStatusModel.setStatus(status);
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            Call<ResponseBody> call = retrofitAPIs.updateStatusApi(tokenaccess, "android", deviceId, updateStatusModel);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Utils.LoaderUtils.dismissLoader();
                    isLoader = false;
                    if (response != null) {
                        String responseString = null;
                        if (response.isSuccessful()) {
                            try {
                                responseString = response.body().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                int statusCode = jsonObject.optInt("statusCode");
                                String message = jsonObject.optString("message");
                                JSONArray dataArray = jsonObject.getJSONArray("data");
                                JSONObject jsonObject1 = (JSONObject) dataArray.get(0);
                                String newStatus = jsonObject1.optString("isActive");
                                if (statusCode == 200 && message.equalsIgnoreCase("Status Updated Successfully")) {
                                    if (newStatus.equalsIgnoreCase("ACTIVE")) {
                                        switchButton.setThumbColorRes(R.color.appColor);
                                        switch_linear.setBackgroundColor(getResources().getColor(R.color.appColor));
                                    } else {
                                        switchButton.setThumbColorRes(R.color.round_empty_gray);
                                        switch_linear.setBackgroundColor(getResources().getColor(R.color.refer_gray));
                                    }
                                    editor.putString(AppConstants.USER_STATUS, newStatus);
                                    editor.commit();
                                    switch_text.setText(newStatus.toUpperCase());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            try {
                                responseString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                int statusCode = jsonObject.optInt("statusCode");
                                String message = jsonObject.optString("message");
                                if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                    new AllUtils().getTokenRefresh(context);
                                    callActiveApi(status);
                                } else {
                                    setSwitchView();
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
                    isLoader = false;
                    Utils.showToast(context, "Some Problem Occured");
                }
            });
        }else{
            taskcompleted = 300;
            Utils.internetDialog(context,this);
        }
    }

    private void logOut(){
        if(Utils.isNetworkAvailable(context)) {
            ApiModel.LogoutModel logoutModel = new ApiModel.LogoutModel();
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            logoutModel.setMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
            logoutModel.setDeviceId(deviceId);
            logoutModel.setPlatform("android");
            logoutModel.setUserId("");
            Call<ResponseBody> call = retrofitAPIs.logoutApi(tokenaccess, "android", deviceId, logoutModel);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Utils.LoaderUtils.dismissLoader();
                    isLoader = false;
                    if (response != null) {
                        String responseString = null;
                        if (response.isSuccessful()) {
                            try {
                                responseString = response.body().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                String message = jsonObject.optString("message");
                                int statusCode = jsonObject.optInt("statusCode");
                                if (statusCode == 200 && message.equalsIgnoreCase("Broker Successfully Logged Out")) {
                                    Utils.showToast(context, message);
                                    String mobile = pref.getString(AppConstants.MOBILE_NUMBER, "");
                                    Boolean login_remember = pref.getBoolean(AppConstants.LOGIN_REMEMBER, false);
                                    Boolean tc_read = pref.getBoolean(AppConstants.IS_TERMS_ACCEPTED, false);
                                    editor.clear();
                                    editor.putBoolean(AppConstants.IS_TERMS_ACCEPTED,tc_read);
                                    editor.commit();
                                    if (login_remember) {
                                        editor.putBoolean(AppConstants.LOGIN_REMEMBER, login_remember);
                                        editor.putString(AppConstants.MOBILE_NUMBER, mobile);
                                        editor.commit();
                                    }
                                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                    finish();
                                }
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            RegisterWithApplozic();
                            try {
                                responseString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                String message = jsonObject.optString("message");
                                int statusCode = jsonObject.optInt("statusCode");
                                if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                    new AllUtils().getTokenRefresh(context);
                                    logOut();
                                } else {
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
                    Utils.showToast(context, "Some Problem Occured");
                    Utils.LoaderUtils.dismissLoader();
                    isLoader = false;
                }
            });
        }else{
            taskcompleted = 400;
            Utils.internetDialog(context,this);
        }
    }

    private void setTouchDelegate(){

    }
    private void setView() {
       setSwitchView();
        int totalUnreadCount = new MessageDatabaseService(context).getTotalUnreadCount();
        if(totalUnreadCount>0) {
            chat_count.setVisibility(View.VISIBLE);
            chat_count.setText(totalUnreadCount + "");
        }
        displayNotification();
        resideMenu.setMenuProfile(pref.getString(AppConstants.FIRST_NAME,"") + " "+pref.getString(AppConstants.LAST_NAME,""),pref.getString(AppConstants.PLAN_TYPE,""),pref.getString(AppConstants.MOBILE_NUMBER,"")+", \n"+pref.getString(AppConstants.MICROMARKET_NAME,""),pref.getString(AppConstants.USER_PIC,""),pref.getFloat(AppConstants.RATING,0.0f));
        this.home_name.setText("Welcome " + this.pref.getString(AppConstants.FIRST_NAME, "") + " !");
        this.home_plan.setText(this.pref.getString(AppConstants.PLAN_TYPE, ""));
        String commission = AllUtils.changeNumberFormat(pref.getFloat(AppConstants.COMMISSION, 0.0f));
        String pot_commission = AllUtils.changeNumberFormat(pref.getFloat(AppConstants.POTENTIAL_COMMISSION, 0.0f));
        this.home_commission.setText(commission);
        this.home_pot_commission.setText(pot_commission);
        float rating = this.pref.getFloat(AppConstants.RATING, 0.0f);
        String rate = String.format("%.1f",rating);
        homerating.setText(rate+" Ratings & "+ pref.getInt(AppConstants.REVIEW_COUNT,0)+" Reviews");
        main_ratingbar.setRating(rating);

       /* if(!MobiComUserPreference.getInstance(this).isLoggedIn()) {
            chatlogin();
            Log.i("Chat","not logged in");
        }*/
        }


    private void scaleSecondPage(ViewPager viewPager){
     View view = viewPager.getChildAt(viewPager.getCurrentItem()+1);
     if(view != null) {
         view.setScaleY(0.7f);
     }
         view = viewPager.getChildAt(viewPager.getCurrentItem()-1);
         if(view != null) {
             view.setScaleY(0.7f);
     }
    }
    private void setSwitchView(){
        String isActive = pref.getString(AppConstants.USER_STATUS,"");
        if(isActive.equalsIgnoreCase("Active") || isActive.equalsIgnoreCase("")){
            switchButton.setChecked(true);
            switch_text.setText("ACTIVE");
            switch_linear.setBackgroundColor(getResources().getColor(R.color.appColor));
        }else{
            switchButton.setChecked(false);
            switch_text.setText("INACTIVE");
            switch_linear.setBackgroundColor(getResources().getColor(R.color.refer_gray));
        }
    }
    public void populateArrayList1() {
        if (Utils.isNetworkAvailable(context)) {
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
            Call<ApiModel.OpenDealModels> call = retrofitAPIs.getActiveDeals(tokenaccess, "android", deviceId, mobileNo,true);
            call.enqueue(new Callback<ApiModel.OpenDealModels>() {
                @Override
                public void onResponse(Call<ApiModel.OpenDealModels> call, Response<ApiModel.OpenDealModels> response) {
                    if (response != null) {
                        if (response.isSuccessful()) {
                            ApiModel.OpenDealModels openDealModels = response.body();
                            int statusCode = openDealModels.getStatusCode();
                            String message = openDealModels.getMessage();
                            if (statusCode == 200 && message.equalsIgnoreCase("")) {
                                double pot_commission = openDealModels.getData().get(0).getpCommission();
                                float p_commissions = (float) pot_commission;
                                editor.putFloat(AppConstants.POTENTIAL_COMMISSION, p_commissions);
                                editor.commit();
                                //home_pot_commission.setText(pot_commissions + "");
                            }
                            ArrayList<ApiModel.BuyAndRentModel> buyAndRentList1 = openDealModels.getData().get(0).getBuyAndRent();
                            ArrayList<ApiModel.BuyAndRentModel> sellAndRentList1 = openDealModels.getData().get(0).getSellAndRentOut();

                            if (buyAndRentList1.size() == 0 && sellAndRentList1.size() == 0) {
                                no_deal_linear.setVisibility(View.VISIBLE);
                                deal_linear.setVisibility(View.GONE);
                            } else {
                                no_deal_linear.setVisibility(View.GONE);
                                deal_linear.setVisibility(View.VISIBLE);
                            }
                            if (buyAndRentList1.size() != 0) {
                                buyAndRentList.clear();
                                for (int i = 0; i < buyAndRentList1.size(); i++) {
                                    buyAndRentList.add(buyAndRentList1.get(i));
                                }
                                btn_click = 1;
                                adapter1.notifyDataSetChanged();
                            }
                            //setViewPager(buyAndRentList);
                            if (sellAndRentList1.size() != 0) {
                                sellAndRentList.clear();
                                for (int i = 0; i < sellAndRentList1.size(); i++) {
                                    sellAndRentList.add(sellAndRentList1.get(i));
                                }
                                adapter2.notifyDataSetChanged();
                            }
                            if (buyAndRentList.size() == 0) {
                                open_deal_sell_btn.performClick();

                            }
                           /* if(buyAndRentList1.size()>1){
                                scaleSecondPage(pager);
                            };*/
                            setButtonText();
                            setView();
                    }else{
                            String responseString = null;
                            try {
                                responseString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                int statusCode = jsonObject.optInt("statusCode");
                                String message = jsonObject.optString("message");
                                if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                    new AllUtils().getTokenRefresh(context);
                                    if(!isLoader) {
                                        Utils.LoaderUtils.showLoader(context);
                                        isLoader = true;
                                    }
                                    populateArrayList1();
                                } else {
                                    Utils.showToast(context, message);
                                }
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Utils.LoaderUtils.dismissLoader();
                        isLoader = false;
                    }
                    }


                @Override
                public void onFailure(Call<ApiModel.OpenDealModels> call, Throwable t) {
                    Utils.showToast(context, "Some Problem Occured");
                    Utils.LoaderUtils.dismissLoader();
                    isLoader = false;
                }
            });
        }else{
            taskcompleted = 200;
            Utils.internetDialog(context,this);
        }
    }


    @Override
    public void clickBtn(int position,Bundle bundle1) {
       dropDialog(position,bundle1);

    }

    @Override
    public void feedBackClick(int position, Bundle bundle) {
        rating_dialog(bundle,position,true);
    }

    @Override
    public void alert(String message) {
        Utils.setSnackBar(drawer_layout,message);
    }

    private void setButtonText(){
        open_deal_buy_btn.setText("Buy/Rent("+buyAndRentList.size()+" of 3)");
        open_deal_sell_btn.setText("Sell/Rent Out("+sellAndRentList.size()+" of 3)");
        if(buyAndRentList.size()>= 3 && sellAndRentList.size() >=3){
            cutoff_relative.setVisibility(View.VISIBLE);
        }else{
            cutoff_relative.setVisibility(View.GONE);
        }

    }
    private void dropLead(final int position, final String drop_reason, final Bundle bundle) {
        if(Utils.isNetworkAvailable(context)) {
            if(!isLoader) {
                Utils.LoaderUtils.showLoader(context);
                isLoader = true;
            }
            ApiModel.ClientAcceptModel clientAcceptModel = new ApiModel.ClientAcceptModel();
            if (btn_click == 1) {
                clientAcceptModel.setClientMobileNo(buyAndRentList.get(position).getClientMobileNo());
                clientAcceptModel.setPostingType(buyAndRentList.get(position).getPostingType());
                clientAcceptModel.setPropertyId(buyAndRentList.get(position).getPropertyId());
            } else if (btn_click == 2) {
                clientAcceptModel.setClientMobileNo(sellAndRentList.get(position).getClientMobileNo());
                clientAcceptModel.setPostingType(sellAndRentList.get(position).getPostingType().toUpperCase());
                clientAcceptModel.setPropertyId(sellAndRentList.get(position).getPropertyId());
            }
            //clientAcceptModel.setRentPropertyId(rentPropertyId);
            clientAcceptModel.setBrokerMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
            clientAcceptModel.setReason(drop_reason);
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            Call<ResponseBody> call = retrofitAPIs.DropLeadApi(tokenaccess, "android", deviceId, clientAcceptModel);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Utils.LoaderUtils.dismissLoader();
                    isLoader = false;
                    if (response != null) {
                        String responseString = null;
                        if (response.isSuccessful()) {
                            try {
                                responseString = response.body().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                String message = jsonObject.optString("message");
                                int statusCode = jsonObject.optInt("statusCode");
                                if (statusCode == 200 && message.equalsIgnoreCase("Lead Dropped Successfully")) {
                                    Utils.showToast(context, message);
                                    if(!bundle.getBoolean("isClientRated",false)) {
                                        rating_dialog(bundle,position,false);
                                    }else{
                                        if (btn_click == 1) {
                                            buyAndRentList.remove(position);
                                            adapter1.notifyDataSetChanged();
                                        } else if (btn_click == 2) {
                                            sellAndRentList.remove(position);
                                            adapter2.notifyDataSetChanged();
                                        }
                                        if (buyAndRentList.size() == 0 && sellAndRentList.size() == 0) {
                                            no_deal_linear.setVisibility(View.VISIBLE);
                                            deal_linear.setVisibility(View.GONE);
                                        } else {
                                            no_deal_linear.setVisibility(View.GONE);
                                            deal_linear.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    setButtonText();
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
                                    dropLead(position, drop_reason,bundle);
                                } else {
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
                    isLoader = false;
                    Utils.showToast(context, "Some Problem Occured");
                }
            });
        }else{
            taskcompleted = 500;
            Utils.internetDialog(context,this);
        }
    }

    private void startScroll(final ViewPager viewPager, final ArrayList<ApiModel.BuyAndRentModel> arrayList){
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == arrayList.size()-1) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer .schedule(new TimerTask() { // task to be scheduled

            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
    }

    private void dropDialog(final int position, final Bundle bundle1){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.drawer_background);
        dialog.setContentView(R.layout.dialog_drop_lead);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        //dialog.setCanceledOnTouchOutside(false);
        // dialog.setCancelable(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final CheckBox checkbox1 = (CheckBox)dialog.findViewById(R.id.drop_check1);
        final CheckBox checkbox2 = (CheckBox)dialog.findViewById(R.id.drop_check2);
        final CheckBox checkbox3 = (CheckBox)dialog.findViewById(R.id.drop_check3);
        TextView drop_client_name = (TextView)dialog.findViewById(R.id.client_name_drop);
        TextView drop_client_address = (TextView)dialog.findViewById(R.id.client_address_drop);
        TextView drop_client_plan = (TextView)dialog.findViewById(R.id.drop_plan);
        ImageView drop_client_image = (ImageView)dialog.findViewById(R.id.client_image_drop);
        drop_client_name.setText(bundle1.getString("lead_name"));
        drop_client_plan.setText(bundle1.getString("lead_plan"));
        drop_client_address.setText(bundle1.getString("lead_address"));
       /* Glide.with(this).load(bundle1.getString("lead_image")).placeholder(R.drawable.placeholder1)
                .diskCacheStrategy(DiskCacheStrategy.ALL).transform(new CircleTransform(context)).into(drop_client_image);*/
        Glide.with(context)
                .load(bundle1.getString("lead_image"))
                .apply(CustomApplicationClass.getRequestOption(true))
                .into(drop_client_image);
        Button submit = (Button)dialog.findViewById(R.id.drop_Submit);

        checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkbox2.setChecked(false);
                    checkbox3.setChecked(false);
                    drop_reason = checkbox1.getText().toString();
                }
            }
        });checkbox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkbox1.setChecked(false);
                    checkbox3.setChecked(false);
                    drop_reason = checkbox2.getText().toString();
                }
            }
        });checkbox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    checkbox1.setChecked(false);
                    checkbox2.setChecked(false);
                    drop_reason = checkbox3.getText().toString();
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drop_reason == null || drop_reason.equalsIgnoreCase("")){
                    Utils.showToast(context,"select the reason first");
                }else {
                        dropLead(position, drop_reason,bundle1);
                    dialog.dismiss();
                }

            }
        });
        dialog.show();
    }

    private void fetchList(){
        RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
        String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
        String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
        String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
        Call<ApiModel.InventoryModel> call = retrofitAPIs.getInventoryApi(tokenaccess, "android", deviceId, mobileNo);
        call.enqueue(new Callback<ApiModel.InventoryModel>() {
            @Override
            public void onResponse(Call<ApiModel.InventoryModel> call, Response<ApiModel.InventoryModel> response) {
                if (response != null) {
                    if (response.isSuccessful()) {
                        ApiModel.InventoryModel inventoryModel = response.body();
                        int statusCode = inventoryModel.getStatusCode();
                        String message = inventoryModel.getMessage();
                        if (statusCode == 200 && message.equalsIgnoreCase("")) {
                            ArrayList<ApiModel.InventoryPersoanlList> inventoryPersoanlLists = inventoryModel.getData();
                            if(inventoryPersoanlLists.size() != 0){
                                db.addListItem(inventoryPersoanlLists);
                            }
                            /*if(pd.isShowing()) {
                                pd.dismiss();
                            }*/
                        }
                    } else {
                        String responseString = null;
                        try {
                            responseString = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(responseString);
                            int statusCode = jsonObject.optInt("statusCode");
                            String message = jsonObject.optString("message");
                            if(statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token"))
                            {
                                new AllUtils().getTokenRefresh(context);
                                fetchList();
                            }else{
                                Utils.showToast(context,message);
                                Utils.LoaderUtils.dismissLoader();
                                isLoader = false;
                            }
                           /* if(pd.isShowing()) {
                                pd.dismiss();
                            }*/
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<ApiModel.InventoryModel> call, Throwable t) {
                Utils.LoaderUtils.dismissLoader();
                isLoader = false;
                Toast.makeText(context, "Some Problem Occured", Toast.LENGTH_SHORT).show();
            }
        });
    }
    BroadcastReceiver unreadCountBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MobiComKitConstants.APPLOZIC_UNREAD_COUNT.equals(intent.getAction())) {
                int totalUnreadCount = new MessageDatabaseService(context).getTotalUnreadCount();
                if(totalUnreadCount>0) {
                    chat_count.setVisibility(View.VISIBLE);
                    chat_count.setText(totalUnreadCount + "");
                }
                //Update unread count in UI
            }
        }
    };
     BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            data = intent.getExtras();
            if (data != null) {
                String message1 = data.getString("message");
                String noti_type = data.getString("NotiType");
                if (noti_type != null) {
                    if (noti_type.equalsIgnoreCase("CLIENT_ACCEPT")) {
                        Utils.bidAcceptedDialog(message1, context);
                        if(!isLoader) {
                            Utils.LoaderUtils.showLoader(context);
                            isLoader = true;
                        }
                        populateArrayList1();
                    }else if (noti_type.equalsIgnoreCase("LEADS_UPDATE")) {
                        if(!isLoader) {
                            Utils.LoaderUtils.showLoader(context);
                            isLoader = true;
                        }
                        populateArrayList1();
                    }else if (noti_type.equalsIgnoreCase("DROP_DEAL")) {
                        if(!isLoader) {
                            Utils.LoaderUtils.showLoader(context);
                            isLoader = true;
                        }
                        populateArrayList1();
                    }
                }
                // Extract data included in the Intent

                //do other stuff here
            }
            displayNotification();

        }
    };
     private void displayNotification(){
         int count1 = pref.getInt(AppConstants.NOTIFICATION_BADGES,0);
         if(noti_count != null) {
             if (count1 > 0 && count1 < 100) {
                 noti_count.setVisibility(View.VISIBLE);
                 noti_count.setText(count1 + "");
             } else if (count1 > 99) {
                 noti_count.setVisibility(View.VISIBLE);
                 noti_count.setText("99+");
             }
         }
     }
    @Override
    protected void onPause() {
        super.onPause();
        Utils.LoaderUtils.dismissLoader();
        isLoader = false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(unreadCountBroadcastReceiver);
        context.unregisterReceiver(mMessageReceiver);
    }
    private void setListener(){
        this.open_deal_buy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_deal_buy_btn.setBackgroundResource(R.drawable.rounded_blue_btn);
                open_deal_sell_btn.setBackgroundResource(R.drawable.rounded_blue_empty_btn);
                open_deal_sell_btn.setTextColor(context.getResources().getColor(R.color.edit_hint_color));
                open_deal_buy_btn.setTextColor(context.getResources().getColor(R.color.white));
                //changeListValue(0);
                    btn_click = 1;
                    pager1.setVisibility(View.GONE);
                    pager.setVisibility(View.VISIBLE);

                }
        });
        this.open_deal_sell_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_deal_buy_btn.setBackgroundResource(R.drawable.rounded_blue_empty_btn);
                open_deal_buy_btn.setTextColor(context.getResources().getColor(R.color.edit_hint_color));
                open_deal_sell_btn.setBackgroundResource(R.drawable.rounded_blue_btn);
                open_deal_sell_btn.setTextColor(context.getResources().getColor(R.color.white));
                    pager.setVisibility(View.GONE);
                    pager1.setVisibility(View.VISIBLE);
                    btn_click = 2;
            }
        });
        referral_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.setAlphaAnimation(referral_btn,context);
                Intent i = new Intent(MainActivity.this, ReferActivity.class);
                startActivity(i);
            }
        });
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                        callActiveApi("Active");
                }else{
                        callActiveApi("INActive");
                }
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Menu_Activity.class);
                i.putExtra("frgToLoad", "AddInventoryFragment");
                i.putExtra("activity_name","MainActivity");
                startActivity(i);
            }
        });
        noti_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chat_count.setVisibility(View.GONE);
                Intent intent = new Intent(MainActivity.this, ConversationActivity.class);
                startActivity(intent);
            }
        });
        noti_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Menu_Activity.class);
                noti_count.setVisibility(View.GONE);
                i.putExtra("frgToLoad", "NotificationFragment");
                startActivity(i);
            }
        });
        linearLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent i = new Intent(MainActivity.this, Menu_Activity.class);
                i.putExtra("frgToLoad", "ReviewFragment");
                startActivity(i);*/
                Intent i = new Intent(context, Menu_Activity.class);
                i.putExtra("frgToLoad", "RatingFragment");
                context.startActivity(i);
            }
        });
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // getIntent() should always return the most recent
        data = intent.getExtras();
        if (data != null) {
            String message1 = data.getString("message");
            String noti_type = data.getString("NotiType");
            if(noti_type != null) {
                if (noti_type.equalsIgnoreCase("CLIENT_ACCEPT")) {
                    Utils.bidAcceptedDialog(message1,context);
                }
            }
            if (intent.getBooleanExtra("shouldShowDialog", false)) {
                Utils.clientAcceptDialog(context);
            }else if(intent.getBooleanExtra("missedDialog", false)){
                String client_name = intent.getStringExtra("clientName");
                String post_type = intent.getStringExtra("postType");
                if(client_name != null && post_type != null) {
                    clientMissedDialog(client_name, post_type);
                }
            }
        }
       displayNotification();
    }

    private void RegisterWithApplozic() {
        UserLoginTask.TaskListener listener = new UserLoginTask.TaskListener() {
            @Override
            public void onSuccess(RegistrationResponse registrationResponse, final Context context) {
                ApplozicSetting.getInstance(context).hideStartNewGroupButton();
                ApplozicSetting.getInstance(context).setHideGroupAddButton(true);
                ApplozicSetting.getInstance(context).setHideGroupNameEditButton(true);
                if (MobiComUserPreference.getInstance(context).isRegistered()) {
                    PushNotificationTask pushNotificationTask = null;
                    PushNotificationTask.TaskListener listener = new PushNotificationTask.TaskListener() {
                        @Override
                        public void onSuccess(RegistrationResponse registrationResponse) {
                            editor.putBoolean(AppConstants.CHAT_LOGIN, true);
                            editor.commit();
                        }

                        @Override
                        public void onFailure(RegistrationResponse registrationResponse, Exception exception) {

                        }
                    };
                    pushNotificationTask = new PushNotificationTask(pref.getString(AppConstants.DEVICE_TOKEN, ""), listener, MainActivity.this);
                    pushNotificationTask.execute((Void) null);
                }
            }

            @Override
            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                Log.e("Chat Error", exception.toString());
            }
        };

        User user = new User();
        user.setUserId(pref.getString(AppConstants.MOBILE_NUMBER,"")); //userId it can be any unique user identifier
        user.setDisplayName(pref.getString(AppConstants.FIRST_NAME,"")+" "+pref.getString(AppConstants.LAST_NAME,"")); //displayName is the name of the user which will be shown in chat messages
        user.setEmail(pref.getString(AppConstants.EMAIL_ID,"")); //optional
        user.setAuthenticationTypeId(User.AuthenticationType.APPLOZIC.getValue());  //User.AuthenticationType.APPLOZIC.getValue() for password verification from Applozic server and User.AuthenticationType.CLIENT.getValue() for access Token verification from your server set access token as password
        user.setPassword("Brongo_Broker"); //optional, leave it blank for testing purpose, read this if you want to add additional security by verifying password from your server https://www.applozic.com/docs/configuration.html#access-token-url
        user.setImageLink(pref.getString(AppConstants.USER_PIC,""));//optional,pass your image link
        new UserLoginTask(user, listener, context).execute((Void) null);
    }
    private void applozicLogout(){
        UserLogoutTask.TaskListener userLogoutTaskListener = new UserLogoutTask.TaskListener() {
            @Override
            public void onSuccess(Context context) {
                editor.putBoolean(AppConstants.CHAT_LOGIN,false).commit();
                logOut();
                //Logout success
            }
            @Override
            public void onFailure(Exception exception) {
                Utils.showToast(context,exception.getMessage().toString());
                Utils.LoaderUtils.dismissLoader();
                isLoader = false;
                //Logout failure
            }
        };
        UserLogoutTask userLogoutTask = new UserLogoutTask(userLogoutTaskListener, context);
        userLogoutTask.execute((Void) null);
    }
    private void generateToken(){
        if(Utils.isNetworkAvailable(context)) {
            if(!isLoader) {
                Utils.LoaderUtils.showLoader(context);
                isLoader = true;
            }
            final String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            final SharedPreferences pref = context.getSharedPreferences(AppConstants.PREF_NAME, 0);
            final SharedPreferences.Editor editor = pref.edit();
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            DeviceDetailsModel.TokenGeneration tokenGeneration = new DeviceDetailsModel.TokenGeneration();
            tokenGeneration.setPlatform("android");
            tokenGeneration.setDeviceId(deviceId);
            tokenGeneration.setMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
            Call<TokenModel> call = retrofitAPIs.generateToken(tokenGeneration);
            call.enqueue(new Callback<TokenModel>() {
                @Override
                public void onResponse(Call<TokenModel> call, Response<TokenModel> response) {
                    if (response != null) {
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
                                    callHomeProfileApi();
                                }
                            } catch (Exception e) {
                            }
                        } else {
                            Utils.LoaderUtils.dismissLoader();
                            isLoader = false;
                            String responseString = null;
                            try {
                                responseString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                int statusCode = jsonObject.optInt("statusCode");
                                String message = jsonObject.optString("message");
                                if (statusCode == 503) {
                                    Utils.showToast(context, message);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }


                @Override
                public void onFailure(Call<TokenModel> call, Throwable t) {
                    String message = t.getMessage();
                    Utils.LoaderUtils.dismissLoader();
                    isLoader = false;
                    Utils.showToast(context, message);
                }
            });
        }else{
            taskcompleted = 1000;
            Utils.internetDialog(context,this);
        }
    }
    private void logOutDialog(){
        final Activity activity = (Activity)context;
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Logout!")
                .setMessage("Do you want to logout?")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean isChatLogin = pref.getBoolean(AppConstants.CHAT_LOGIN,false);
                        if(isChatLogin){
                            if(!isLoader) {
                                Utils.LoaderUtils.showLoader(context);
                                isLoader = true;
                            }
                            applozicLogout();
                        }
                      else{
                            if(!isLoader) {
                                Utils.LoaderUtils.showLoader(context);
                                isLoader = true;
                            }
                            logOut();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    @Override
    public void onTryReconnect() {
        switch (taskcompleted){
            case 100:
                callHomeProfileApi();
                break;
            case 200:
                if(!isLoader) {
                    Utils.LoaderUtils.showLoader(context);
                    isLoader = true;
                }
                populateArrayList1();
                break;
            case 400:
                logOutDialog();
                break;
            case 1000:
                generateToken();
                break;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.LoaderUtils.dismissLoader();
        isLoader = false;
    }
    private void clientMissedDialog(String client_name,String posting_type){
        SpannableStringBuilder name = Utils.convertToSpannableString(client_name,0,client_name.length(),"black");
        SpannableStringBuilder posting = Utils.convertToSpannableString(posting_type,0,posting_type.length(),"black");
        String message = "You just missed a new deal from '" ;
        message = message + name;
        message = message.concat("'request for ‘");
        message = message + posting;
        message = message.concat(" property’. Please check your internet connection and ensure your Brongo App is open at all times to accept the next deal!");
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_missed_deal);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        final Button got_it_btn = (Button)dialog.findViewById(R.id.missed_deal_dialog_btn);
        TextView message_textview = (TextView)dialog.findViewById(R.id.missed_dialog_message);
        message_textview.setText(message);
        got_it_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void rating_dialog(final Bundle bundle, final int position, final boolean isCancellable){
        final String posting = bundle.getString("lead_posting_type");
        final ArrayList<String> arrayList = new ArrayList<>();
        final ArrayList<String> buy_sell_list1 = new ArrayList<String>(Arrays.asList("Delayed Commission", "Didn't pay my commission", "Difficult to reach Client","Always late for meeting","Takes time to make decision"));
        final ArrayList<String> buy_sell_list2 = new ArrayList<String>(Arrays.asList("Treated me profession","clear about Requirement","Always on time","Always reachable","paid commission on time"));
        final ArrayList<String> buy_sell_list3 = new ArrayList<String>(Arrays.asList("6 Star service", "Open for suggestions","Great Attitude","Organised","Clear about Requirements"));
        final ArrayList<String> rent_list1 = new ArrayList<String>(Arrays.asList("unorganised", "Not clear about the requirement", "Difficult to reach Client","Always late for meeting","Takes time to make decision"));
        final ArrayList<String> rent_list2 = new ArrayList<String>(Arrays.asList("Treated me profession","clear about Requirement","Always on time","Always reachable","organised"));
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.drawer_background);
/*        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);*/
        dialog.setContentView(R.layout.dialog_client_rating);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        if(!isCancellable){
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }
        ImageView rating_client_image = (ImageView) dialog.findViewById(R.id.client_image_rating);
        RatingBar dialog_ratingbar = (RatingBar)dialog.findViewById(R.id.rating_dialog_ratingBar);
        TextView ratimg_client_name = (TextView)dialog.findViewById(R.id.client_name_rating);
        TextView ratimg_client_address = (TextView)dialog.findViewById(R.id.client_address_rating);
        //  TextView rating_client_plan = (TextView)dialog.findViewById(R.id.client_rating_plan);
        Button dialog_submit = (Button)dialog.findViewById(R.id.client_rating_btn);
        final LinearLayout rating_linear = (LinearLayout)dialog.findViewById(R.id.rating_check_linear);
        final CheckBox rating_check1 = (CheckBox)dialog.findViewById(R.id.rating_check1);
        final CheckBox rating_check2 = (CheckBox)dialog.findViewById(R.id.rating_check2);
        final CheckBox rating_check3 = (CheckBox)dialog.findViewById(R.id.rating_check3);
        final CheckBox rating_check4 = (CheckBox)dialog.findViewById(R.id.rating_check4);
        final CheckBox rating_check5 = (CheckBox)dialog.findViewById(R.id.rating_check5);
        ratimg_client_name.setText(bundle.getString("lead_name"));
        //rating_client_plan.setText(bundle.getString("lead_plan"));
        ratimg_client_address.setText(bundle.getString("lead_address"));
       /* Glide.with(context).load(bundle.getString("lead_image")).placeholder(R.drawable.placeholder1)
                .diskCacheStrategy(DiskCacheStrategy.ALL).transform(new CircleTransform(getActivity())).dontAnimate().into(rating_client_image);*/
        Glide.with(context)
                .load(bundle.getString("lead_image"))
                .apply(CustomApplicationClass.getRequestOption(true))
                .into(rating_client_image);
        dialog_ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rating_linear.setVisibility(View.VISIBLE);
                rating_check1.setChecked(false);
                rating_check2.setChecked(false);
                rating_check3.setChecked(false);
                rating_check4.setChecked(false);
                rating_check5.setChecked(false);
                if(rating<1.0f){
                    ratingBar.setRating(1.0f);
                }
                arrayList.clear();
                rating1 = rating;
                if(posting.equalsIgnoreCase("Sell") || posting.equalsIgnoreCase("Buy")){
                    if(rating1<3.0){
                        rating_check1.setText(buy_sell_list1.get(0));
                        rating_check2.setText(buy_sell_list1.get(1));
                        rating_check3.setText(buy_sell_list1.get(2));
                        rating_check4.setText(buy_sell_list1.get(3));
                        rating_check5.setText(buy_sell_list1.get(4));
                    }else if(rating1 >= 3.0 && rating1 <5.0){
                        rating_check1.setText(buy_sell_list2.get(0));
                        rating_check2.setText(buy_sell_list2.get(1));
                        rating_check3.setText(buy_sell_list2.get(2));
                        rating_check4.setText(buy_sell_list2.get(3));
                        rating_check5.setText(buy_sell_list2.get(4));
                    } else if(rating1 == 5.0){
                        rating_check1.setText(buy_sell_list3.get(0));
                        rating_check2.setText(buy_sell_list3.get(1));
                        rating_check3.setText(buy_sell_list3.get(2));
                        rating_check4.setText(buy_sell_list3.get(3));
                        rating_check5.setText(buy_sell_list3.get(4));
                    }
                }else if(posting.equalsIgnoreCase("rent") || posting.equalsIgnoreCase("rent_out")){
                    if(rating1 == 1.0 || rating1 == 2.0){
                        rating_check1.setText(rent_list1.get(0));
                        rating_check2.setText(rent_list1.get(1));
                        rating_check3.setText(rent_list1.get(2));
                        rating_check4.setText(rent_list1.get(3));
                        rating_check5.setText(rent_list1.get(4));
                    }else if(rating1 == 3.0 || rating1 == 4.0){
                        rating_check1.setText(rent_list2.get(0));
                        rating_check2.setText(rent_list2.get(1));
                        rating_check3.setText(rent_list2.get(2));
                        rating_check4.setText(rent_list2.get(3));
                        rating_check5.setText(rent_list2.get(4));
                    } else if(rating1 == 5.0){
                        rating_check1.setText(buy_sell_list3.get(0));
                        rating_check2.setText(buy_sell_list3.get(1));
                        rating_check3.setText(buy_sell_list3.get(2));
                        rating_check4.setText(buy_sell_list3.get(3));
                        rating_check5.setText(buy_sell_list3.get(4));
                    }
                }
            }
        });
        dialog_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrayList.size()>0) {
                    updateRating(rating1, arrayList, isCancellable,bundle,position);
                    dialog.dismiss();
                }else{
                    Utils.showToast(context,"Select atleast 1 comment");
                }

            }
        });

        rating_check4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    arrayList.add(rating_check4.getText().toString());
                }else{
                    if(arrayList.contains(rating_check4.getText().toString())){
                        arrayList.remove(rating_check4.getText().toString());
                    }
                }
            }
        });
        rating_check1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    arrayList.add(rating_check1.getText().toString());
                }else{
                    if(arrayList.contains(rating_check1.getText().toString())){
                        arrayList.remove(rating_check1.getText().toString());
                    }
                }
            }
        });
        rating_check2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    arrayList.add(rating_check2.getText().toString());
                }else{
                    if(arrayList.contains(rating_check2.getText().toString())){
                        arrayList.remove(rating_check2.getText().toString());
                    }
                }
            }
        });
        rating_check3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    arrayList.add(rating_check3.getText().toString());
                }else{
                    if(arrayList.contains(rating_check3.getText().toString())){
                        arrayList.remove(rating_check3.getText().toString());
                    }
                }
            }
        });
        rating_check5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    arrayList.add(rating_check5.getText().toString());
                }else{
                    if(arrayList.contains(rating_check5.getText().toString())){
                        arrayList.remove(rating_check5.getText().toString());
                    }
                }
            }
        });

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dialog.show();
    }
    private void updateRating(final float rating, final ArrayList<String> arrayList, final boolean isCancellable, final Bundle bundle, final int position){
        if (Utils.isNetworkAvailable(context)) {
            if(!isLoader) {
                Utils.LoaderUtils.showLoader(context);
                isLoader = true;
            }
            ApiModel.RatingModel ratingModel = new ApiModel.RatingModel();
            ratingModel.setBrokerMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
            ratingModel.setClientMobileNo(bundle.getString("lead_mobile"));
            ratingModel.setRating(rating);
            ratingModel.setReview(arrayList);
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            Call<ResponseBody> call = retrofitAPIs.updateRatingApi(tokenaccess, "android", deviceId, ratingModel);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Utils.LoaderUtils.dismissLoader();
                    isLoader = false;
                    if (response != null) {
                        String responseString = null;
                        if (response.isSuccessful()) {
                            try {
                                responseString = response.body().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                int statusCode = jsonObject.optInt("statusCode");
                                String message = jsonObject.optString("message");
                                if (statusCode == 200 && message.equalsIgnoreCase("Thanks For Your Valuable Feedback")) {
                                    if (btn_click == 1) {
                                        if(!isCancellable) {
                                            buyAndRentList.remove(position);
                                        }else{
                                            buyAndRentList.get(position).setClientRated(true);
                                        }
                                        adapter1.notifyDataSetChanged();
                                    } else if (btn_click == 2) {
                                        if(!isCancellable) {
                                            buyAndRentList.remove(position);
                                        }else{
                                            buyAndRentList.get(position).setClientRated(true);
                                        }
                                        adapter2.notifyDataSetChanged();
                                    }
                                    if (buyAndRentList.size() == 0 && sellAndRentList.size() == 0) {
                                        no_deal_linear.setVisibility(View.VISIBLE);
                                        deal_linear.setVisibility(View.GONE);
                                    } else {
                                        no_deal_linear.setVisibility(View.GONE);
                                        deal_linear.setVisibility(View.VISIBLE);
                                    }
                                    //ratingBar.setRating(rating);
                                    Utils.showToast(context, message);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            try {
                                responseString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                int statusCode = jsonObject.optInt("statusCode");
                                String message = jsonObject.optString("message");
                                if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                    new AllUtils().getTokenRefresh(context);
                                    updateRating(rating, arrayList, isCancellable, bundle,position);
                                } else {
                                    if (btn_click == 1) {
                                        if(!isCancellable) {
                                            buyAndRentList.remove(position);
                                        }
                                        adapter1.notifyDataSetChanged();
                                    } else if (btn_click == 2) {
                                        if(!isCancellable) {
                                            buyAndRentList.remove(position);
                                        }
                                        adapter2.notifyDataSetChanged();
                                    }
                                    if (buyAndRentList.size() == 0 && sellAndRentList.size() == 0) {
                                        no_deal_linear.setVisibility(View.VISIBLE);
                                        deal_linear.setVisibility(View.GONE);
                                    } else {
                                        no_deal_linear.setVisibility(View.GONE);
                                        deal_linear.setVisibility(View.VISIBLE);
                                    }
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
                    isLoader = false;
                    if (btn_click == 1) {
                        if(!isCancellable) {
                            buyAndRentList.remove(position);
                        }
                        adapter1.notifyDataSetChanged();
                    } else if (btn_click == 2) {
                        if(!isCancellable) {
                            buyAndRentList.remove(position);
                        }
                        adapter2.notifyDataSetChanged();
                    }
                    if (buyAndRentList.size() == 0 && sellAndRentList.size() == 0) {
                        no_deal_linear.setVisibility(View.VISIBLE);
                        deal_linear.setVisibility(View.GONE);
                    } else {
                        no_deal_linear.setVisibility(View.GONE);
                        deal_linear.setVisibility(View.VISIBLE);
                    }
                    Utils.showToast(context, "Some Problem Occured");
                }
            });
        }else{
            Utils.internetDialog(context,this);
        }
    }
}
