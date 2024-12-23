package in.brongo.brongo_broker.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.model.ApiModel;
import in.brongo.brongo_broker.other.AllUtils;
import in.brongo.brongo_broker.other.NoInternetTryConnectListener;
import in.brongo.brongo_broker.other.NoTokenTryListener;
import in.brongo.brongo_broker.util.AppConstants;
import in.brongo.brongo_broker.util.CustomApplicationClass;
import in.brongo.brongo_broker.util.RetrofitAPIs;
import in.brongo.brongo_broker.util.RetrofitBuilders;
import in.brongo.brongo_broker.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements NoInternetTryConnectListener,NoTokenTryListener,AllUtils.test{
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    private LinearLayout parentLayout;
    private ArrayList<ApiModel.ProfileObject> mSelectedList;
    private ArrayList<ApiModel.SubscriptionObject> arrayList;
    private ImageView  prof_rera_certificate, prof_id_proof, prof_address_proof,profile_back;
    private ImageView prof_image;
    private Button prof_refer, prof_upgrade_now;
    private RatingBar profile_ratingbar;
    private TextView prof_name, prof_address, prof_rating, prof_close_deal, prof_open_deal, prof_total_deal, prof_close_deal_count, prof_open_deal_count, prof_inventory_count,
            prof_email, prof_comp_type, prof_real_estate, prof_micromarket, prof_office_address, prof_credits, prof_see_plans,prof_refer_text,prof_subscrip_plan,
            prof_plan_expiry,toolbar_title,prof_plan_tc,profile_plan_upgrade_text,profile_trumpcard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initialise();
    }
    private void initialise(){
        try {
            context =this;
            parentLayout = findViewById(R.id.profile_parent_linear);
            pref = getSharedPreferences(AppConstants.PREF_NAME, 0);
            arrayList = new ArrayList<>();
            mSelectedList = new ArrayList<>();
            editor = pref.edit();
            profile_ratingbar = findViewById(R.id.profile_ratingBar);
            prof_name = findViewById(R.id.profile_username);
            prof_address = findViewById(R.id.profile_uaddress);
            prof_plan_tc = findViewById(R.id.profile_plan_tc);
            prof_close_deal = findViewById(R.id.profile_closed_commission_text);
            prof_open_deal = findViewById(R.id.profile_opened_commission_text);
            prof_total_deal = findViewById(R.id.profile_total_deal);
            prof_close_deal_count = findViewById(R.id.profile_closed_deal);
            prof_open_deal_count = findViewById(R.id.profile_open_deal);
            prof_inventory_count = findViewById(R.id.profile_inventory);
            profile_plan_upgrade_text = findViewById(R.id.profile_plan_upgrade_text);
            prof_email = findViewById(R.id.profile_email);
            prof_comp_type = findViewById(R.id.profile_comp_type);
            prof_real_estate = findViewById(R.id.profile_real_estate);
            prof_micromarket = findViewById(R.id.profile_micromarket);
            prof_office_address = findViewById(R.id.profile_office_address);
            prof_credits = findViewById(R.id.profile_credit);
            prof_subscrip_plan = findViewById(R.id.profile_plan);
            prof_refer_text = findViewById(R.id.profile_referred);
            prof_see_plans = findViewById(R.id.profile_see_plans);
            prof_plan_expiry = findViewById(R.id.profile_plan_expiry);
            prof_refer = findViewById(R.id.profile_refer);
            prof_upgrade_now = findViewById(R.id.profile_upgrade_now);
            prof_image = findViewById(R.id.profile_image);
            prof_rera_certificate = findViewById(R.id.profile_rera_certificate);
            prof_id_proof = findViewById(R.id.profile_id_proof);
            prof_address_proof = findViewById(R.id.profile_address_proof);
            prof_rating = findViewById(R.id.profile_rating);
            profile_trumpcard = findViewById(R.id.profile_trumpcard);
            toolbar_title = findViewById(R.id.profile_toolbar_title);
            profile_back = findViewById(R.id.profile_toolbar_back);
            setView();
            callProfileApi();
            setListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setView() {
        try {
            toolbar_title.setText("My Profile");
            profile_trumpcard.setText(pref.getString(AppConstants.TRUMP_CARD,""));
            prof_name.setText(pref.getString(AppConstants.FIRST_NAME,"")+" "+ pref.getString(AppConstants.LAST_NAME,""));
            prof_address.setText(pref.getString(AppConstants.MOBILE_NUMBER,"") + "," + pref.getString(AppConstants.MICROMARKET_NAME,""));
            Glide.with(context)
                    .load(pref.getString(AppConstants.USER_PIC,""))
                    .apply(CustomApplicationClass.getRequestOption(true))
                    .into(prof_image);
            prof_subscrip_plan.setText(pref.getString(AppConstants.PLAN_TYPE,""));
            if(pref.getString(AppConstants.PLAN_TYPE,"").contains("premium")){
                prof_upgrade_now.setVisibility(View.GONE);
                profile_plan_upgrade_text.setVisibility(View.GONE);
            }
            float rating = pref.getFloat(AppConstants.RATING, 0f);
            String rating_string = String.format("%.1f",rating);
            profile_ratingbar.setRating(rating);
            if(mSelectedList != null && mSelectedList.size() !=0) {
                prof_email.setText(mSelectedList.get(0).getEmailId());
                prof_micromarket.setText(mSelectedList.get(0).getMicro1MarketName());
                String market2 = mSelectedList.get(0).getMicro2MarketName();
                String market3 = mSelectedList.get(0).getMicro3MarketName();
                if(market2 != null && !market2.isEmpty()){
                    prof_micromarket.append(","+market2);
                }
                if(market3 != null && !market3.isEmpty()){
                    prof_micromarket.append(","+market3);
                }
                String real_estate = "";
                for (int i = 0; i < mSelectedList.get(0).getRealEstateType().size(); i++) {
                    if (i == 0) {
                        real_estate = real_estate + mSelectedList.get(0).getRealEstateType().get(i);
                    } else if (i == 1) {
                        real_estate = real_estate + "," + mSelectedList.get(0).getRealEstateType().get(i);
                    }
                }
                getTc(mSelectedList.get(0).getServices());
                prof_real_estate.setText(real_estate);
                prof_close_deal_count.setText(mSelectedList.get(0).getClosedDeals() + "");
                prof_inventory_count.setText(mSelectedList.get(0).getInventoryList() + "");
                prof_open_deal_count.setText(mSelectedList.get(0).getOpenDeals() + "");
                prof_total_deal.setText(mSelectedList.get(0).getTotalDeals() + "");
                prof_comp_type.setText(mSelectedList.get(0).getTypeOfCompany());
                String commission = AllUtils.changeNumberFormat(pref.getFloat(AppConstants.COMMISSION, 0.0f));
                String pot_commission = AllUtils.changeNumberFormat(pref.getFloat(AppConstants.POTENTIAL_COMMISSION, 0.0f));
                prof_close_deal.setText(commission);
                prof_open_deal.setText(pot_commission);
                String office_address = mSelectedList.get(0).getOfficeAddress().getLine1();
               if(!mSelectedList.get(0).getOfficeAddress().getLine2().equalsIgnoreCase("")){
                   office_address = office_address+"," + mSelectedList.get(0).getOfficeAddress().getLine2();
               }
                if(!mSelectedList.get(0).getOfficeAddress().getCity().equalsIgnoreCase("")){
                    office_address = office_address+"," + mSelectedList.get(0).getOfficeAddress().getCity();
                }
                if(!(mSelectedList.get(0).getOfficeAddress().getPinCode()==0)){
                    office_address = office_address+"," + mSelectedList.get(0).getOfficeAddress().getPinCode();
                }
                prof_office_address.setText(office_address);
         if(mSelectedList.get(0).getReraCertificate().equalsIgnoreCase("")){
              prof_rera_certificate.setBackgroundResource(R.drawable.no_image);
          }else if (!this.isFinishing ()) {

              Glide.with(context).load(mSelectedList.get(0).getReraCertificate().toString())
                      .apply(CustomApplicationClass.getPropertyImage(true)).into(prof_rera_certificate);
              Glide.with(context).load(mSelectedList.get(0).getAddressProof().toString())
                      .apply(CustomApplicationClass.getPropertyImage(true)).into(prof_address_proof);
              Glide.with(context).load(mSelectedList.get(0).getiDProof().toString())
                      .apply(CustomApplicationClass.getPropertyImage(true)).into(prof_id_proof);
          }
                prof_plan_expiry.setText(mSelectedList.get(0).getPlanExpiredTime());
         String credits = AllUtils.changeNumberFormat((float)mSelectedList.get(0).getCredits());
                prof_credits.setText(credits);
                prof_rating.setText(rating_string + " Ratings & " + mSelectedList.get(0).getReviewsCount() + " Reviews");
                prof_refer_text.setText("You have referred " + mSelectedList.get(0).getReferredNo() + " brokers");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getTc(ArrayList<String> list){
        try {
            String text="";
            for(int i =0;i<list.size();i++){
                if(i == (list.size()-1)) {
                    text = text + list.get(i).toUpperCase();
                }else{
                    text = text + list.get(i).toUpperCase()+ "/";
                }
            }
            //text = "All ("+ text+ ")leads \n are ";
            text = "All (BUY/SELL/RENT) leads \n are ";
            String  text1 = "FREE";
            SpannableStringBuilder str = Utils.convertToSpannableString(text1,0,text1.length(),"green");
            prof_plan_tc.setText(text);
            prof_plan_tc.append(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callProfileApi(){
        try {
            if(Utils.isNetworkAvailable(context)) {
                Utils.LoaderUtils.showLoader(context);
                RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
                String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
                String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
                String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
                Call<ApiModel.ProfileModel> call = retrofitAPIs.getProfile(tokenaccess, "android", deviceId, mobileNo);
                call.enqueue(new Callback<ApiModel.ProfileModel>() {

                    @Override
                    public void onResponse(Call<ApiModel.ProfileModel> call, Response<ApiModel.ProfileModel> response) {
                        Utils.LoaderUtils.dismissLoader();
                        if (response != null) {
                            if (response.isSuccessful()) {
                                ApiModel.ProfileModel profileModel = new ApiModel.ProfileModel();
                                profileModel = response.body();
                                int statusCode = profileModel.getStatusCode();
                                if (statusCode == 200) {
                                    ArrayList<ApiModel.ProfileObject> profilelist = profileModel.getData();
                                    if (profilelist.size() != 0) {
                                        editor.putString(AppConstants.USER_PIC, profilelist.get(0).getProfileImage().toString());
                                        editor.putString(AppConstants.REFERRAL_ID, profilelist.get(0).getReferralId());
                                        editor.putString(AppConstants.EMAIL_ID,profilelist.get(0).getEmailId());
                                        editor.commit();
                                        mSelectedList.addAll(profilelist);
                                        setView();
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
                                        openTokenDialog(context);
                                    } else {
                                        Utils.setSnackBar(parentLayout,message);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiModel.ProfileModel> call, Throwable t) {
                        Utils.LoaderUtils.dismissLoader();
                        if (t.getMessage().equals("Too many follow-up requests: 21")) {
                          openTokenDialog(context);
                        }else {
                            Utils.showToast(context, t.getLocalizedMessage().toString(), "Failure");
                        }
                    }
                });
            }else{
                Utils.internetDialog(context,this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        try {
            Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Utils.LoaderUtils.dismissLoader();
    }

    @Override
    public void onTryReconnect() {
        callProfileApi();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            Utils.LoaderUtils.dismissLoader();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListener(){
        try {
            profile_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();
                }
            });
            prof_refer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.setAlphaAnimation(prof_refer,getApplicationContext());
                    Intent i = new Intent(ProfileActivity.this, Menu_Activity.class);
                    i.putExtra("frgToLoad", "ReferFragmentMore");
                    startActivity(i);
                }
            });
            prof_open_deal_count.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.setAlphaAnimation(prof_open_deal_count,context);
                    Intent i = new Intent(ProfileActivity.this, Menu_Activity.class);
                    i.putExtra("frgToLoad", "HistoricalFragment");
                    startActivity(i);
                }
            });
            prof_inventory_count.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.setAlphaAnimation(prof_inventory_count,context);
                    Intent i = new Intent(ProfileActivity.this, Menu_Activity.class);
                    i.putExtra("frgToLoad", "InventoryListFragment");
                    startActivity(i);
                }
            });
            prof_see_plans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ProfileActivity.this, Menu_Activity.class);
                    i.putExtra("frgToLoad", "SubscriptionFragment");
                    i.putExtra("activity_name","profile");
                    startActivity(i);
                }
            });
            prof_upgrade_now.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ProfileActivity.this, Menu_Activity.class);
                    i.putExtra("frgToLoad", "premiumFragment");
                    i.putExtra("activity_name","profile");
                    startActivity(i);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void openTokenDialog(Context context){
        try {
            Utils.tokenDialog(context,this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTryRegenerate() {
        try {
            getToken(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            callProfileApi();
        }else{
            Utils.LoaderUtils.dismissLoader();
            openTokenDialog(context);
        }
    }
}
