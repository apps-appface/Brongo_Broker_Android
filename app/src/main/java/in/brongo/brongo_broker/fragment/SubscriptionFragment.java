package in.brongo.brongo_broker.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.activity.Menu_Activity;
import in.brongo.brongo_broker.adapter.CustomAdapter;
import in.brongo.brongo_broker.model.ApiModel;
import in.brongo.brongo_broker.model.PaymentHashModel;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class SubscriptionFragment extends Fragment implements NoInternetTryConnectListener,NoTokenTryListener,AllUtils.test{
    private Context context;
    private SharedPreferences pref;
    private TextView toolbar_title;
    private Toolbar toolbar;
    private RelativeLayout parentLayout;
    private int Task_completed = 1000;
    private int apicode=0;
    private int trial_position=100,basic_position=100,premium_position=100;
    private String trial_tc,basic_tc,premium_tc,plan_tc,cur_plan_id="";
    private ArrayList<ApiModel.SubscriptionObject> arrayList;
    private ArrayList<String> trial_condition_list,basic_condition_list,premium_condition_list;
    private CustomAdapter trial_adapter,basic_adapter,premium_adapter;
    private RecyclerView trial_recycle,basic_recycle,premium_recycle;
    private RelativeLayout premium_linear_rate;
    private TextView sub_username,subscription_plan,subscription_tc,subscription_expiry,subscription_plan2,subscription_tc2,sub_cur_expiry2,subscription_basic,subscription_basic_plan
            ,subscription_basic_tc,sub_upgrade_premium,subscription_premium_plan,sub_premium_price,sub_premium_expiry,subscription_premium_tc,sub_see_more;
    private ImageView sub_user_image,edit_icon,delete_icon,add_icon;
    private LinearLayout sub_linear_current,basic_linear_parent,sub_linear_basic,linear_premium_parent,sub_linear_premium,trial_linear_parent;
    private boolean isBasicVisible;
    public SubscriptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subscription, container, false);
        initialiseView(view);
        sub_see_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return view;
    }
    private void initialiseView(View view){
        try {
            context = getActivity();
            parentLayout = getActivity().findViewById(R.id.menu_parent_relative);
            trial_tc = basic_tc =premium_tc = plan_tc="";
            pref = getActivity().getSharedPreferences(AppConstants.PREF_NAME,0);
            arrayList = new ArrayList<>();
            trial_condition_list = new ArrayList<>();
            basic_condition_list = new ArrayList<>();
            premium_condition_list = new ArrayList<>();
            edit_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_edit);
            delete_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_delete);
            add_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_add);
            edit_icon.setVisibility(View.GONE);
            delete_icon.setVisibility(View.GONE);
            add_icon.setVisibility(View.GONE);
            toolbar_title = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.inventory_toolbar_title);
            toolbar = getActivity().findViewById(R.id.inventory_toolbar);
            toolbar.setVisibility(View.VISIBLE);
            toolbar_title.setText("Subscription");
            trial_recycle = view.findViewById(R.id.subs_trial_listview);
            basic_recycle = view.findViewById(R.id.subs_basic_listview);
            premium_recycle = view.findViewById(R.id.subs_premium_listview);
            sub_user_image = view.findViewById(R.id.subscription_image);
            sub_username = view.findViewById(R.id.subscription_username);
            subscription_plan = view.findViewById(R.id.subscription_plan);
            subscription_tc = view.findViewById(R.id.subscription_tc);
            subscription_expiry = view.findViewById(R.id.subscription_expiry);
            subscription_plan2 = view.findViewById(R.id.subscription_plan2);
            subscription_tc2 = view.findViewById(R.id.subscription_tc2);
            sub_cur_expiry2 = view.findViewById(R.id.sub_cur_expiry2);
            subscription_basic = view.findViewById(R.id.subscription_basic);
            subscription_basic_plan = view.findViewById(R.id.subscription_basic_plan);
            subscription_basic_tc = view.findViewById(R.id.subscription_basic_tc);
            sub_upgrade_premium = view.findViewById(R.id.sub_upgrade_premium);
            subscription_premium_plan = view.findViewById(R.id.subscription_premium_plan);
            sub_premium_price = view.findViewById(R.id.sub_premium_price);
            sub_premium_expiry = view.findViewById(R.id.sub_premium_expiry);
            subscription_premium_tc = view.findViewById(R.id.subscription_premium_tc);
            sub_see_more = view.findViewById(R.id.sub_see_more);
            sub_linear_current = view.findViewById(R.id.sub_linear_current);
            basic_linear_parent = view.findViewById(R.id.basic_linear_parent);
            sub_linear_basic = view.findViewById(R.id.sub_linear_basic);
            linear_premium_parent = view.findViewById(R.id.linear_premium_parent);
            sub_linear_premium = view.findViewById(R.id.sub_linear_premium);
            trial_linear_parent = view.findViewById(R.id.trial_linear_parent);
            LinearLayoutManager verticalmanager = new LinearLayoutManager(context, 0, false);
            verticalmanager.setOrientation(LinearLayoutManager.VERTICAL);
            trial_recycle.setLayoutManager(verticalmanager);
            trial_adapter = new CustomAdapter(trial_condition_list,context);
            trial_recycle.setAdapter(trial_adapter);
            LinearLayoutManager verticalmanager1 = new LinearLayoutManager(context, 0, false);
            verticalmanager1.setOrientation(LinearLayoutManager.VERTICAL);
            basic_recycle.setLayoutManager(verticalmanager1);
            basic_adapter = new CustomAdapter(basic_condition_list,context);
            basic_recycle.setAdapter(basic_adapter);
            premium_linear_rate = view.findViewById(R.id.premium_linear_rate);
            LinearLayoutManager verticalmanager2 = new LinearLayoutManager(context, 0, false);
            verticalmanager2.setOrientation(LinearLayoutManager.VERTICAL);
            premium_recycle.setLayoutManager(verticalmanager2);
            premium_adapter = new CustomAdapter(premium_condition_list,context);
            premium_recycle.setAdapter(premium_adapter);
            foo();
            setLayoutVisibility();
            fetchSubscriptionPlans();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void fetchSubscriptionPlans(){
        try {
            if(Utils.isNetworkAvailable(context)) {
                Utils.LoaderUtils.showLoader(context);
                RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
                String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
                String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
                String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
                Call<ApiModel.SubscriptionModel> call = retrofitAPIs.getSubscriptionPlanApi(tokenaccess, "android", deviceId, mobileNo);
                call.enqueue(new Callback<ApiModel.SubscriptionModel>() {
                    @Override
                    public void onResponse(Call<ApiModel.SubscriptionModel> call, Response<ApiModel.SubscriptionModel> response) {
                        Utils.LoaderUtils.dismissLoader();
                        if (response != null) {
                            if (response.isSuccessful()) {
                                ApiModel.SubscriptionModel subscriptionModel = response.body();
                                int statusCode = subscriptionModel.getStatusCode();
                                String message = subscriptionModel.getMessage();
                                if (statusCode == 200 && message.equalsIgnoreCase("")) {
                                    ArrayList<ApiModel.SubscriptionObject> subscription_list = subscriptionModel.getData();
                                    if (subscription_list.size() != 0) {
                                        arrayList.clear();
                                        arrayList.addAll(subscription_list);
                                        getPlanPosition(arrayList);
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
                                        apicode = 100;
                                        openTokenDialog(context);
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
                    public void onFailure(Call<ApiModel.SubscriptionModel> call, Throwable t) {
                        Utils.showToast(context, t.getLocalizedMessage().toString(),"Failure");
                        Utils.LoaderUtils.dismissLoader();
                    }
                });
            }else{
                Task_completed = 100;
                Utils.internetDialog(context,this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setView(){
        try {
            sub_username.setText("Hi, "+pref.getString(AppConstants.FIRST_NAME,"")+" "+pref.getString(AppConstants.LAST_NAME,""));
            Glide.with(context)
                    .load(pref.getString(AppConstants.USER_PIC,""))
                    .apply(CustomApplicationClass.getRequestOption(true))
                    .into(sub_user_image);
            subscription_plan.setText(pref.getString(AppConstants.PLAN_TYPE,""));
            subscription_tc2.setText(trial_tc);
            subscription_basic_tc.setText(basic_tc);
            subscription_premium_tc.setText(premium_tc);
            subscription_tc.setText(plan_tc);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private String getTc(ArrayList<String> list){
        String text="";
        try {
            for(int i =0;i<list.size();i++){
                if(i == (list.size()-1)) {
                    text = text + list.get(i).toUpperCase();
                }else{
                    text = text + list.get(i).toUpperCase()+ "/";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }
    private void getPlanPosition(ArrayList<ApiModel.SubscriptionObject> list){
        try {
            String currentPlan = pref.getString(AppConstants.PLAN_TYPE,"");
            for(int i=0; i<list.size();i++){
                if(list.get(i).getName().contains("TRIAL")){
                    trial_position = i;
                    String trial_tc1 = getTc(arrayList.get(trial_position).getServices());
                    trial_tc = "All ("+ trial_tc1 + ") leads are free";
                    trial_condition_list.addAll(arrayList.get(trial_position).getConditions());
                    trial_adapter.notifyDataSetChanged();
                }else if(list.get(i).getName().contains("BASIC")){
                    basic_position=i;
                    String basic_tc1 = getTc(arrayList.get(basic_position).getServices());
                    basic_tc = "All ("+ basic_tc1 +") leads are free";
                    basic_condition_list.addAll(arrayList.get(basic_position).getConditions());
                    basic_adapter.notifyDataSetChanged();
                }else if(list.get(i).getName().contains("PREMIUM")){
                    premium_position=i;
                    String premium_tc1 = getTc(arrayList.get(premium_position).getServices());
                    premium_tc = "All ("+ premium_tc1 +") leads are free";
                    premium_condition_list.addAll(arrayList.get(premium_position).getConditions());
                    premium_adapter.notifyDataSetChanged();
                }
            }
            if(currentPlan.contains("PREMIUM")){
               cur_plan_id = list.get(premium_position).get_id();
            }else if(currentPlan.contains("TRIAL")){
                cur_plan_id = list.get(trial_position).get_id();
            }else {
                cur_plan_id = list.get(basic_position).get_id();
            }
            fetchCurrentPlan(cur_plan_id);
            setView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void foo() {
        try {
            SpannableString premium_see_more = makeLinkSpan("See More", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, Menu_Activity.class);
                    i.putExtra("frgToLoad", "premiumFragment");
                    startActivity(i);
                }
            });
            SpannableString basic_plan_link = makeLinkSpan("See Basic Plan features", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if(isBasicVisible){
                       isBasicVisible=false;
                       basic_linear_parent.setVisibility(View.GONE);
                   }else{
                       isBasicVisible=true;
                       basic_linear_parent.setVisibility(View.VISIBLE);
                   }
                }
            });
            subscription_basic.setText(basic_plan_link);
            // Set the TextView's text
            sub_see_more.setText(premium_see_more);

            // This line makes the link clickable!
            makeLinksFocusable(subscription_basic);
            makeLinksFocusable(sub_see_more);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*
 * Methods used above.
 */

    private SpannableString makeLinkSpan(CharSequence text, View.OnClickListener listener) {
        SpannableString link = new SpannableString(text);
        String data = text.toString();
        link.setSpan(new ClickableString(listener), 0, text.length(),
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        if(data.equalsIgnoreCase("See More")) {
             link.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.edit_hint_color)), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return link;
    }

    private void makeLinksFocusable(TextView tv) {
        try {
            MovementMethod m = tv.getMovementMethod();
            if ((m == null) || !(m instanceof LinkMovementMethod)) {
                if (tv.getLinksClickable()) {
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTryReconnect() {
        switch (Task_completed){
            case 100:
                fetchSubscriptionPlans();
                break;
            case 200:
                fetchCurrentPlan(cur_plan_id);
                break;
        }
    }

    @Override
    public void onTryRegenerate() {
        switch (apicode){
            case 100:
               getToken(context);
                break;
            case 200:
              getToken(context);
                break;
        }
    }

/*
 * ClickableString class
 */

    private  class ClickableString extends ClickableSpan {
        private View.OnClickListener mListener;
        public ClickableString(View.OnClickListener listener) {
            mListener = listener;
        }
        @Override
        public void onClick(View v) {
            mListener.onClick(v);
        }
    }
    private void setLayoutVisibility(){
        try {
            String currentPlan = pref.getString(AppConstants.PLAN_TYPE,"");
            if(currentPlan.contains("TRIAL")){
                isBasicVisible = false;
                subscription_basic.setVisibility(View.VISIBLE);
                basic_linear_parent.setVisibility(View.GONE);
                trial_linear_parent.setVisibility(View.VISIBLE);
                sub_cur_expiry2.setVisibility(View.VISIBLE);
                sub_upgrade_premium.setVisibility(View.VISIBLE);
                sub_linear_premium.setVisibility(View.GONE);
                premium_linear_rate.setVisibility(View.VISIBLE);
            }else if(currentPlan.contains("BASIC")){
                isBasicVisible = true;
                basic_linear_parent.setVisibility(View.VISIBLE);
                trial_linear_parent.setVisibility(View.GONE);
                subscription_basic.setVisibility(View.GONE);
                sub_cur_expiry2.setVisibility(View.VISIBLE);
                sub_upgrade_premium.setVisibility(View.VISIBLE);
                sub_linear_premium.setVisibility(View.GONE);
                premium_linear_rate.setVisibility(View.VISIBLE);
            }else if(currentPlan.contains("PREMIUM")){
                trial_linear_parent.setVisibility(View.GONE);
                basic_linear_parent.setVisibility(View.GONE);
                subscription_basic.setVisibility(View.GONE);
                sub_cur_expiry2.setVisibility(View.GONE);
                sub_upgrade_premium.setVisibility(View.GONE);
                sub_linear_premium.setVisibility(View.VISIBLE);
                premium_linear_rate.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        Utils.LoaderUtils.dismissLoader();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.LoaderUtils.dismissLoader();
    }
    private void fetchCurrentPlan(String planId){
        try {
            if(Utils.isNetworkAvailable(context)) {
                Utils.LoaderUtils.showLoader(context);
                RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
                String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
                String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
                String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
                Call<PaymentHashModel.CurrentPlanModel> call = retrofitAPIs.getCurrentPlanApi(tokenaccess, "android", deviceId, mobileNo,planId);
                call.enqueue(new Callback<PaymentHashModel.CurrentPlanModel>() {
                    @Override
                    public void onResponse(Call<PaymentHashModel.CurrentPlanModel> call, Response<PaymentHashModel.CurrentPlanModel> response) {
                        Utils.LoaderUtils.dismissLoader();
                        if (response != null) {
                            if (response.isSuccessful()) {
                                PaymentHashModel.CurrentPlanModel currentPlanModel = response.body();
                                int statusCode = currentPlanModel.getStatusCode();
                                String message = currentPlanModel.getMessage();
                                if (statusCode == 200 && message.equalsIgnoreCase("")) {
                                    ArrayList<PaymentHashModel.CurrentPlanObject> subscription_list = currentPlanModel.getData();
                                    if (subscription_list.size() != 0) {
                                      setCurrentPlan(subscription_list);
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
                                        apicode = 200;
                                        openTokenDialog(context);
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
                    public void onFailure(Call<PaymentHashModel.CurrentPlanModel> call, Throwable t) {
                        Utils.showToast(context, t.getLocalizedMessage().toString(),"Failure");
                        Utils.LoaderUtils.dismissLoader();
                    }
                });
            }else{
                Task_completed = 200;
                Utils.internetDialog(context,this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setCurrentPlan(ArrayList<PaymentHashModel.CurrentPlanObject> arrayList){
        try {
            String cur_plan_tc = getTc(arrayList.get(0).getServices());
            plan_tc = "All (" + cur_plan_tc + ") leads \n are ";
            subscription_tc.setText(plan_tc);
            String text1 = "FREE";
            SpannableStringBuilder str = Utils.convertToSpannableString(text1, 0, text1.length(), "green");
            subscription_tc.append(str);
            subscription_expiry.setText("Your subscription ends in " + arrayList.get(0).getExpiredPeriod());
            if (arrayList.size() > 0 && arrayList.get(0).getSubPlans().size() > 0) {
                String plan_rate = AllUtils.changeNumberFormat(arrayList.get(0).getSubPlans().get(0).getPayed());
                sub_premium_price.setText(plan_rate);
                sub_premium_price.setPaintFlags(sub_premium_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                sub_premium_expiry.setText(arrayList.get(0).getSubPlans().get(0).getDuration() + " month");
                String expiryText = changeTimeFormat(arrayList.get(0).getExpireTime());
                sub_cur_expiry2.setText("Post expiry you will be moved to ");
                String basic_plan = "BASIC PLAN";
                SpannableStringBuilder str1 = new SpannableStringBuilder(basic_plan);
                str1.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, basic_plan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                str1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.appColor)), 0, basic_plan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                sub_cur_expiry2.append(str1);
                sub_cur_expiry2.append(expiryText);

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public String changeTimeFormat(String DateString){
        int day=0;
        String formattedDate = DateString != null ? DateString : "";
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(formattedDate);
            calendar.setTime(date);
            SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
            //SimpleDateFormat day_date = new SimpleDateFormat("dd");
            String month_name = month_date.format(calendar.getTime());
        day = calendar.get(Calendar.DAY_OF_MONTH);
        formattedDate =getExpiryString(day,month_name);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }
  private  String getExpiryString(final int n,String month_name) {
        String date_string="";
      try {
          if (n > 1 && n < 32) {
              if (n >= 11 && n <= 13) {
              date_string = n+"th";
              }
              switch (n % 10) {
                  case 1:
                      date_string = n+"st";
                  case 2:
                      date_string = n+"nd";
                  case 3:
                      date_string = n+"rd";
                  default:
                      date_string = n+"th";
              }
          }

          date_string = " from "+ date_string +" of "+month_name;
      } catch (Exception e) {
          e.printStackTrace();
      }
      return date_string;
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
            switch (apicode){
                case 100:
                    fetchSubscriptionPlans();
                    break;
                case 200:
                    fetchCurrentPlan(cur_plan_id);
                    break;
            }
        }else{
            Utils.LoaderUtils.dismissLoader();
            openTokenDialog(context);
        }
    }
}
