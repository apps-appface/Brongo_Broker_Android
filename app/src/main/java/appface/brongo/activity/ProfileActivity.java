package appface.brongo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import appface.brongo.R;
import appface.brongo.model.ApiModel;
import appface.brongo.other.AllUtils;
import appface.brongo.other.NoInternetTryConnectListener;
import appface.brongo.util.AppConstants;
import appface.brongo.util.CircleTransform;
import appface.brongo.util.CustomApplicationClass;
import appface.brongo.util.RefreshTokenCall;
import appface.brongo.util.RetrofitAPIs;
import appface.brongo.util.RetrofitBuilders;
import appface.brongo.util.Utils;
import de.hdodenhof.circleimageview.CircleImageView;
import ng.max.slideview.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements NoInternetTryConnectListener{
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    private ArrayList<ApiModel.ProfileObject> mSelectedList;
    private ArrayList<ApiModel.SubscriptionObject> arrayList;
    private ImageView  prof_rera_certificate, prof_id_proof, prof_address_proof,profile_back;
    private CircleImageView prof_image;
    private Button prof_refer, prof_upgrade_now;
    private RatingBar profile_ratingbar;
    private TextView prof_name, prof_address, prof_rating, prof_close_deal, prof_open_deal, prof_total_deal, prof_close_deal_count, prof_open_deal_count, prof_inventory_count,
            prof_email, prof_comp_type, prof_real_estate, prof_micromarket, prof_office_address, prof_credits, prof_see_plans,prof_refer_text,prof_subscrip_plan,
            prof_plan_expiry,toolbar_title,prof_plan_tc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initialise();
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
                startActivity(i);
            }
        });
    }
    private void initialise(){
        context =this;
        pref = getSharedPreferences(AppConstants.PREF_NAME, 0);
        arrayList = new ArrayList<>();
        mSelectedList = new ArrayList<>();
        editor = pref.edit();
        profile_ratingbar = (RatingBar)findViewById(R.id.profile_ratingBar);
        prof_name = (TextView) findViewById(R.id.profile_username);
        prof_address = (TextView) findViewById(R.id.profile_uaddress);
        prof_plan_tc = (TextView)findViewById(R.id.profile_plan_tc);
        prof_close_deal = (TextView) findViewById(R.id.profile_closed_commission_text);
        prof_open_deal = (TextView) findViewById(R.id.profile_opened_commission_text);
        prof_total_deal = (TextView) findViewById(R.id.profile_total_deal);
        prof_close_deal_count = (TextView) findViewById(R.id.profile_closed_deal);
        prof_open_deal_count = (TextView) findViewById(R.id.profile_open_deal);
        prof_inventory_count = (TextView) findViewById(R.id.profile_inventory);
        prof_email = (TextView) findViewById(R.id.profile_email);
        prof_comp_type = (TextView) findViewById(R.id.profile_comp_type);
        prof_real_estate = (TextView) findViewById(R.id.profile_real_estate);
        prof_micromarket = (TextView) findViewById(R.id.profile_micromarket);
        prof_office_address = (TextView) findViewById(R.id.profile_office_address);
        prof_credits = (TextView) findViewById(R.id.profile_credit);
        prof_subscrip_plan = (TextView) findViewById(R.id.profile_plan);
        prof_refer_text = (TextView)findViewById(R.id.profile_referred);
        prof_see_plans = (TextView)findViewById(R.id.profile_see_plans);
        prof_plan_expiry = (TextView) findViewById(R.id.profile_plan_expiry);
        prof_refer = (Button) findViewById(R.id.profile_refer);
        prof_upgrade_now = (Button)findViewById(R.id.profile_upgrade_now);
        prof_image = (CircleImageView) findViewById(R.id.profile_image);
        prof_rera_certificate = (ImageView)findViewById(R.id.profile_rera_certificate);
        prof_id_proof = (ImageView) findViewById(R.id.profile_id_proof);
        prof_address_proof = (ImageView)findViewById(R.id.profile_address_proof);
        prof_rating = (TextView)findViewById(R.id.profile_rating);
        toolbar_title = (TextView)findViewById(R.id.profile_toolbar_title);
        profile_back = (ImageView)findViewById(R.id.profile_toolbar_back);
        callProfileApi();
    }
    private void setView() {
        toolbar_title.setText("My Profile");
        prof_name.setText(pref.getString(AppConstants.FIRST_NAME,"")+" "+ pref.getString(AppConstants.LAST_NAME,""));
        prof_address.setText(pref.getString(AppConstants.MOBILE_NUMBER,"") + "," + pref.getString(AppConstants.MICROMARKET_NAME,""));
       /* if (!this.isFinishing ()) {
            Glide.with(context).load(pref.getString(AppConstants.USER_PIC, ""))
                    .diskCacheStrategy(DiskCacheStrategy.ALL).transform(new CircleTransform(context)).into(prof_image);
        }*/
        Glide.with(context)
                .load(pref.getString(AppConstants.USER_PIC,""))
                .apply(CustomApplicationClass.getRequestOption(true))
                .into(prof_image);
        prof_subscrip_plan.setText(pref.getString(AppConstants.PLAN_TYPE,""));
        float rating = pref.getFloat(AppConstants.RATING, 0f);
        String rating_string = String.format("%.1f",rating);
        profile_ratingbar.setRating(rating);
       /* Gson gson = new Gson();
        ArrayList<ApiModel.ProfileObject> mSelectedList = gson.fromJson(pref.getString("ProfileList", ""),
                new TypeToken<ArrayList<ApiModel.ProfileObject>>() {
                }.getType());*/
        if(mSelectedList != null) {
            prof_email.setText(mSelectedList.get(0).getEmailId());
            prof_micromarket.setText(mSelectedList.get(0).getMicro1MarketName() + "," + mSelectedList.get(0).getMicro2MarketName() + "," + mSelectedList.get(0).getMicro3MarketName());
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
            // Glide.with(context).load(mSelectedList.get(0).getProfileImage().toString()).into(prof_image);
            prof_close_deal_count.setText(mSelectedList.get(0).getClosedDeals() + "");
            prof_inventory_count.setText(mSelectedList.get(0).getInventoryList() + "");
            prof_open_deal_count.setText(mSelectedList.get(0).getOpenDeals() + "");
            prof_total_deal.setText(mSelectedList.get(0).getTotalDeals() + "");
            prof_comp_type.setText(mSelectedList.get(0).getTypeOfCompany());
            prof_close_deal.setText("₹" + pref.getInt(AppConstants.COMMISSION, 0) + "");
            prof_open_deal.setText("₹" + pref.getInt(AppConstants.COMMISSION, 0) + "");
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
      /*  Glide.with(context).load(mSelectedList.get(0).getReraCertificate().toString()).into(prof_rera_certificate);
        Glide.with(context).load(mSelectedList.get(0).getiDProof().toString()).into(prof_id_proof);
        Glide.with(context).load(mSelectedList.get(0).getAddressProof().toString()).into(prof_address_proof);*/
      if(mSelectedList.get(0).getReraCertificate().equalsIgnoreCase("")){
          prof_rera_certificate.setBackgroundResource(R.drawable.no_image);
      }else if (!this.isFinishing ()) {

          Glide.with(context).load(mSelectedList.get(0).getReraCertificate().toString())
                  .into(prof_rera_certificate);
      }
            if(mSelectedList.get(0).getiDProof().equalsIgnoreCase("")){
                prof_id_proof.setBackgroundResource(R.drawable.no_image);
            }else if (!this.isFinishing ()) {
                Glide.with(context).load(mSelectedList.get(0).getiDProof().toString()).into(prof_id_proof);
            } if(mSelectedList.get(0).getAddressProof().equalsIgnoreCase("")){
                prof_address_proof.setBackgroundResource(R.drawable.no_image);
            }else if (!this.isFinishing ()) {
            Glide.with(context).load(mSelectedList.get(0).getAddressProof().toString()).into(prof_address_proof);
            }
            prof_plan_expiry.setText(mSelectedList.get(0).getPlanExpiredTime());
            prof_credits.setText("₹" + mSelectedList.get(0).getCredits() + "");
            prof_rating.setText(rating_string + " Ratings & " + mSelectedList.get(0).getReviewsCount() + " Reviews");
            prof_refer_text.setText("You have referred " + mSelectedList.get(0).getReferredNo() + " brokers");
        }
    }

    private void getTc(ArrayList<String> list){
        String text="";
        for(int i =0;i<list.size();i++){
            if(i == (list.size()-1)) {
                text = text + list.get(i).toUpperCase();
            }else{
                text = text + list.get(i).toUpperCase()+ "/";
            }
        }
        text = "All ("+ text+ ")leads are ";
        String  text1 = "FREE";
        SpannableStringBuilder str = Utils.convertToSpannableString(text1,0,text1.length(),"green");
        prof_plan_tc.setText(text);
        prof_plan_tc.append(str);
    }

    public void callProfileApi(){
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
                                    new AllUtils().getTokenRefresh(context);
                                    callProfileApi();
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
                public void onFailure(Call<ApiModel.ProfileModel> call, Throwable t) {
                    Utils.LoaderUtils.dismissLoader();
                    Utils.showToast(context, "Some Problem Occured");
                }
            });
        }else{
            Utils.internetDialog(context,this);
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
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

}
