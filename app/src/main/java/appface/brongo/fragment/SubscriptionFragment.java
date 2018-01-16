package appface.brongo.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import appface.brongo.R;
import appface.brongo.activity.MainActivity;
import appface.brongo.activity.Menu_Activity;
import appface.brongo.adapter.CustomAdapter;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubscriptionFragment extends Fragment implements NoInternetTryConnectListener{
    private Context context;
    private SharedPreferences pref;
    private TextView toolbar_title;
    private Toolbar toolbar;
    private int trial_position=100,basic_position=100,premium_position=100;
    private String trial_tc,basic_tc,premium_tc;
    private ArrayList<ApiModel.SubscriptionObject> arrayList;
    private ArrayList<String> trial_condition_list,basic_condition_list,premium_condition_list;
    private CustomAdapter trial_adapter,basic_adapter,premium_adapter;
    private RecyclerView trial_recycle,basic_recycle,premium_recycle;
    private TextView sub_username,subscription_plan,subscription_tc,subscription_expiry,subscription_plan2,subscription_tc2,sub_cur_expiry2,subscription_basic,subscription_basic_plan
            ,subscription_basic_tc,sub_upgrade_premium,subscription_premium_plan,sub_premium_price,sub_premium_expiry,subscription_premium_tc,sub_see_more;
    private ImageView sub_user_image,edit_icon,delete_icon,add_icon;;
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
        context = getActivity();
        pref = getActivity().getSharedPreferences(AppConstants.PREF_NAME,0);
        arrayList = new ArrayList<>();
        trial_condition_list = new ArrayList<>();
        basic_condition_list = new ArrayList<>();
        premium_condition_list = new ArrayList<>();
        edit_icon = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_edit);
        delete_icon =(ImageView) getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_delete);
        add_icon = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_add);
        edit_icon.setVisibility(View.GONE);
        delete_icon.setVisibility(View.GONE);
        add_icon.setVisibility(View.GONE);
        toolbar_title = (TextView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.inventory_toolbar_title);
        toolbar = (Toolbar)getActivity().findViewById(R.id.inventory_toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar_title.setText("Subscriptions");
        trial_recycle = (RecyclerView) view.findViewById(R.id.subs_trial_listview);
        basic_recycle = (RecyclerView) view.findViewById(R.id.subs_basic_listview);
        sub_user_image = (ImageView)view.findViewById(R.id.subscription_image);
        sub_username = (TextView)view.findViewById(R.id.subscription_username);
        subscription_plan = (TextView)view.findViewById(R.id.subscription_plan);
        subscription_tc = (TextView)view.findViewById(R.id.subscription_tc);
        subscription_expiry = (TextView)view.findViewById(R.id.subscription_expiry);
        subscription_plan2 = (TextView)view.findViewById(R.id.subscription_plan2);
        subscription_tc2 = (TextView)view.findViewById(R.id.subscription_tc2);
        sub_cur_expiry2 = (TextView)view.findViewById(R.id.sub_cur_expiry2);
        subscription_basic = (TextView)view.findViewById(R.id.subscription_basic);
        subscription_basic_plan = (TextView)view.findViewById(R.id.subscription_basic_plan);
        subscription_basic_tc = (TextView)view.findViewById(R.id.subscription_basic_tc);
        sub_upgrade_premium = (TextView)view.findViewById(R.id.sub_upgrade_premium);
        subscription_premium_plan = (TextView)view.findViewById(R.id.subscription_premium_plan);
        sub_premium_price = (TextView)view.findViewById(R.id.sub_premium_price);
        sub_premium_expiry = (TextView)view.findViewById(R.id.sub_premium_expiry);
        subscription_premium_tc = (TextView)view.findViewById(R.id.subscription_premium_tc);
        sub_see_more = (TextView)view.findViewById(R.id.sub_see_more);
        sub_linear_current = (LinearLayout)view.findViewById(R.id.sub_linear_current);
        basic_linear_parent = (LinearLayout)view.findViewById(R.id.basic_linear_parent);
        sub_linear_basic = (LinearLayout)view.findViewById(R.id.sub_linear_basic);
        linear_premium_parent = (LinearLayout)view.findViewById(R.id.linear_premium_parent);
        sub_linear_premium = (LinearLayout)view.findViewById(R.id.sub_linear_premium);
        trial_linear_parent = (LinearLayout)view.findViewById(R.id.trial_linear_parent);
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
        foo();
        setLayoutVisibility();
        fetchSubscriptionPlans();
    }
    private void fetchSubscriptionPlans(){
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
                                    fetchSubscriptionPlans();
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
                    }
                }

                @Override
                public void onFailure(Call<ApiModel.SubscriptionModel> call, Throwable t) {
                    Toast.makeText(context, "Some Problem Occured", Toast.LENGTH_SHORT).show();
                    Utils.LoaderUtils.dismissLoader();
                /*if(pd.isShowing()) {
                    pd.dismiss();
                }*/
                }
            });
        }else{
            Utils.internetDialog(context,this);
        }
    }
    private void setView(){
        String currentPlan = pref.getString(AppConstants.PLAN_TYPE,"");
        trial_tc = getTc(arrayList.get(trial_position).getServices());
        basic_tc = getTc(arrayList.get(basic_position).getServices());
        trial_condition_list.addAll(arrayList.get(trial_position).getConditions());
        basic_condition_list.addAll(arrayList.get(basic_position).getConditions());
        trial_adapter.notifyDataSetChanged();
        basic_adapter.notifyDataSetChanged();
        //premium_tc = getTc(arrayList.get(2).getServices());
        sub_username.setText("Hi, "+pref.getString(AppConstants.FIRST_NAME,"")+" "+pref.getString(AppConstants.LAST_NAME,""));
       /* Glide.with(getActivity()).load(pref.getString(AppConstants.USER_PIC,""))
                .diskCacheStrategy(DiskCacheStrategy.NONE).transform(new CircleTransform(context)).into(sub_user_image);*/
        Glide.with(context)
                .load(pref.getString(AppConstants.USER_PIC,""))
                .apply(CustomApplicationClass.getRequestOption(true))
                .into(sub_user_image);
        subscription_plan.setText(pref.getString(AppConstants.PLAN_TYPE,""));
        subscription_tc2.setText(trial_tc);
        subscription_basic_tc.setText(basic_tc);
      //  subscription_premium_plan.setText(arrayList.get(2).getName());
        //subscription_premium_tc.setText(premium_tc);
        if(currentPlan.equalsIgnoreCase("TRIAL PLAN")){
            subscription_tc.setText(trial_tc);
        }else if(currentPlan.equalsIgnoreCase("BASIC PLAN")){
            subscription_tc.setText(basic_tc);
        }else if(currentPlan.equalsIgnoreCase("PREMIUM PLAN")){
            subscription_tc.setText(premium_tc);
        }

    }
    private String getTc(ArrayList<String> list){
        String text="";
        for(int i =0;i<list.size();i++){
            if(i == (list.size()-1)) {
                text = text + list.get(i).toUpperCase();
            }else{
                text = text + list.get(i).toUpperCase()+ "/";
            }
        }
        text = "All ("+ text+ ")leads are free";
        return text;
    }
    private void getPlanPosition(ArrayList<ApiModel.SubscriptionObject> list){
        for(int i=0; i<list.size();i++){
            if(list.get(i).getName().equalsIgnoreCase("TRIAL_PLAN")){
                trial_position = i;
            }else if(list.get(i).getName().equalsIgnoreCase("BASIC_PLAN")){
                basic_position=i;
            }else if(list.get(i).getName().equalsIgnoreCase("PREMIUM_PLAN")){
                premium_position=i;
            }
        }
        setView();
    }
    private void foo() {
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
        MovementMethod m = tv.getMovementMethod();
        if ((m == null) || !(m instanceof LinkMovementMethod)) {
            if (tv.getLinksClickable()) {
                tv.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }

    @Override
    public void onTryReconnect() {
        fetchSubscriptionPlans();
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
        String currentPlan = pref.getString(AppConstants.PLAN_TYPE,"");
        if(currentPlan.equalsIgnoreCase("TRIAL PLAN")){
            isBasicVisible = false;
            subscription_basic.setVisibility(View.VISIBLE);
            basic_linear_parent.setVisibility(View.GONE);
        }else if(currentPlan.equalsIgnoreCase("BASIC PLAN")){
            isBasicVisible = true;
            basic_linear_parent.setVisibility(View.VISIBLE);
            trial_linear_parent.setVisibility(View.GONE);
            subscription_basic.setVisibility(View.GONE);
        }else if(currentPlan.equalsIgnoreCase("PREMIUM PLAN")){
            trial_linear_parent.setVisibility(View.GONE);
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
}
