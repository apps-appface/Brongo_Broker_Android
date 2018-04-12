package in.brongo.brongo_broker.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.activity.ProfileActivity;
import in.brongo.brongo_broker.activity.ReferActivity;
import in.brongo.brongo_broker.adapter.ReferMoreAdapter;
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
public class ReferFragmentMore extends Fragment implements NoInternetTryConnectListener,NoTokenTryListener,AllUtils.test{
    private TextView refer_credit,refer_count,refer_viewall,no_referee_text;
    private RecyclerView refer_recycle;
    private TextView toolbar_title;
    private Toolbar toolbar;
    private RelativeLayout parentLayout;
    private Button refer_more_btn;
    private ImageView edit_icon,delete_icon,add_icon,referback2;
    private ReferMoreAdapter referMoreAdapter;
    private SharedPreferences pref;
    private ArrayList<ApiModel.referredBrokerObject> arrayList;
    private ArrayList<ApiModel.referredBrokerObject> arrayList_full;
    private Context context;

    public ReferFragmentMore() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_refer_fragment_more, container, false);
        initialise(view);
        refer_more_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(context, ReferActivity.class);
                    startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        referback2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(context,ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    getActivity().finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }
    private void initialise(View view){
        try {
            context = getActivity();
            parentLayout = getActivity().findViewById(R.id.menu_parent_relative);
            arrayList = new ArrayList<>();
            arrayList_full = new ArrayList<>();
            referMoreAdapter = new ReferMoreAdapter(context,arrayList);
            pref = getActivity().getSharedPreferences(AppConstants.PREF_NAME,0);
            refer_count = view.findViewById(R.id.referee_count_text);
            refer_credit = view.findViewById(R.id.refer_credit_value);
            refer_viewall = view.findViewById(R.id.refer_viewall);
            no_referee_text = view.findViewById(R.id.no_referee_text);
            refer_recycle = view.findViewById(R.id.referee_recycle);
            LinearLayoutManager verticalmanager = new LinearLayoutManager(context, 0, false);
            verticalmanager.setOrientation(LinearLayoutManager.VERTICAL);
            refer_recycle.setLayoutManager(verticalmanager);
            refer_recycle.setAdapter(referMoreAdapter);
            edit_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_edit);
            delete_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_delete);
            add_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_add);
            toolbar = getActivity().findViewById(R.id.inventory_toolbar);
            toolbar_title = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.inventory_toolbar_title);
            toolbar.setVisibility(View.GONE);
            edit_icon.setVisibility(View.GONE);
            delete_icon.setVisibility(View.GONE);
            add_icon.setVisibility(View.GONE);
            toolbar_title.setText("Refer More");
            refer_more_btn = view.findViewById(R.id.refer_more_broker_btn);
            referback2 = view.findViewById(R.id.refer_back2);
            referApi();
            createText();
            refer_more_btn.requestFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void createText() {
        try {
            SpannableString link = makeLinkSpan("View all", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(arrayList_full.size() != 0){
                        arrayList.clear();
                        arrayList.addAll(arrayList_full);
                        referMoreAdapter.notifyDataSetChanged();
                    }
                }
            });
            refer_viewall.setText(link);
            // Set the TextView's text

            // Append the link we created above using a function defined below.


            // Append a period (this will not be a link).

            // This line makes the link clickable!
            makeLinksFocusable(refer_viewall);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*
 * Methods used above.
 */

    private SpannableString makeLinkSpan(CharSequence text, View.OnClickListener listener) {
        SpannableString link = new SpannableString(text);
        try {
            link.setSpan(new ClickableString(listener), 0, text.length(),
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            link.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.appColor)), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
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
        referApi();
    }

    @Override
    public void onTryRegenerate() {
       getToken(context);
    }

/*
 * ClickableString class
 */

    private static class ClickableString extends ClickableSpan {
        private View.OnClickListener mListener;
        public ClickableString(View.OnClickListener listener) {
            mListener = listener;
        }
        @Override
        public void onClick(View v) {
            mListener.onClick(v);
        }
    }
    private void referApi(){
        try {
            if(Utils.isNetworkAvailable(context)) {
                Utils.LoaderUtils.showLoader(context);
                RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
                String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
                String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
                String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
                Call<ApiModel.ReferralData> call = retrofitAPIs.getReferralApi(tokenaccess, "android", deviceId, mobileNo);
                call.enqueue(new Callback<ApiModel.ReferralData>() {
                    @Override
                    public void onResponse(Call<ApiModel.ReferralData> call, Response<ApiModel.ReferralData> response) {
                        Utils.LoaderUtils.dismissLoader();
                        if (response != null) {
                            if (response.isSuccessful()) {
                                ApiModel.ReferralData referralData = response.body();
                                int statusCode = referralData.getStatusCode();
                                String message = referralData.getMessage();
                                String credit = referralData.getData().getTotalReferralBonus();
                                String count = referralData.getData().getReferralCount();
                                Float creditvalue = Float.valueOf(credit);
                                String creditString = AllUtils.changeNumberFormat(creditvalue);
                                refer_credit.setText(creditString);
                                refer_count.setText("My referees(" + count + ")");
                                if (statusCode == 200 && message.equalsIgnoreCase("")) {
                                    ArrayList<ApiModel.referredBrokerObject> referred_broker_list = referralData.getData().getReferredBroker();
                                    if (referred_broker_list.size() != 0) {
                                        if(referred_broker_list.size()>3) {
                                            refer_viewall.setVisibility(View.VISIBLE);
                                        }
                                        refer_recycle.setVisibility(View.VISIBLE);
                                        no_referee_text.setVisibility(View.GONE);
                                        arrayList_full.addAll(referred_broker_list);
                                        if (referred_broker_list.size() > 2) {
                                            arrayList.add(referred_broker_list.get(0));
                                            arrayList.add(referred_broker_list.get(1));
                                            arrayList.add(referred_broker_list.get(2));
                                        } else {
                                            for (int i = 0; i < referred_broker_list.size(); i++) {
                                                arrayList.add(referred_broker_list.get(i));
                                            }
                                        }
                                    }else{
                                        refer_viewall.setVisibility(View.INVISIBLE);
                                        refer_recycle.setVisibility(View.INVISIBLE);
                                        no_referee_text.setVisibility(View.VISIBLE);
                                    }
                                    referMoreAdapter.notifyDataSetChanged();
                                /*if(pd.isShowing()) {
                                    pd.dismiss();
                                }*/
                                }
                            } else {
                                refer_viewall.setVisibility(View.INVISIBLE);
                                refer_recycle.setVisibility(View.INVISIBLE);
                                no_referee_text.setVisibility(View.VISIBLE);
                                String responseString = null;
                                try {
                                    responseString = response.errorBody().string();
                                    JSONObject jsonObject = new JSONObject(responseString);
                                    int statusCode = jsonObject.optInt("statusCode");
                                    String message = jsonObject.optString("message");
                                    if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                        openTokenDialog(context);
                                    } else {
                                        if(message.equalsIgnoreCase("referrals Not Found.Please refer your friends")){
                                            String creditString = AllUtils.changeNumberFormat(0.0f);
                                            refer_credit.setText(creditString);
                                        }
                                        Utils.setSnackBar(parentLayout,message);
                                    }
                               /* if(pd.isShowing()) {
                                    pd.dismiss();
                                }*/
                                } catch (Exception e) {
                                    String error = e.toString();
                                    e.printStackTrace();
                                }
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<ApiModel.ReferralData> call, Throwable t) {
                        Utils.LoaderUtils.dismissLoader();
                        if (t.getMessage().equals("Too many follow-up requests: 21")) {
                           openTokenDialog(context);
                        }else {
                            Utils.showToast(context, t.getLocalizedMessage().toString(), "Failure");
                        }
                    /*if(pd.isShowing()) {
                        pd.dismiss();
                    }*/
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
    public void onPause() {
        super.onPause();
        Utils.LoaderUtils.dismissLoader();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.LoaderUtils.dismissLoader();
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
        try {
            if(isSuccess){
                referApi();
            }else{
                Utils.LoaderUtils.dismissLoader();
                openTokenDialog(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
