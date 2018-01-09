package appface.brongo.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import appface.brongo.R;
import appface.brongo.activity.MainActivity;
import appface.brongo.activity.Menu_Activity;
import appface.brongo.activity.ProfileActivity;
import appface.brongo.activity.ReferActivity;
import appface.brongo.adapter.ReferMoreAdapter;
import appface.brongo.model.ApiModel;
import appface.brongo.other.AllUtils;
import appface.brongo.other.NoInternetTryConnectListener;
import appface.brongo.util.AppConstants;
import appface.brongo.util.RefreshTokenCall;
import appface.brongo.util.RetrofitAPIs;
import appface.brongo.util.RetrofitBuilders;
import appface.brongo.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReferFragmentMore extends Fragment implements NoInternetTryConnectListener {
    private TextView refer_credit,refer_count,refer_viewall;
    private RecyclerView refer_recycle;
    private Button refer_more_btn;
    private ImageView referback2;
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
                Intent i = new Intent(context, ReferActivity.class);
                startActivity(i);
            }
        });
        referback2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }
    private void initialise(View view){
        context = getActivity();
        arrayList = new ArrayList<>();
        arrayList_full = new ArrayList<>();
        referMoreAdapter = new ReferMoreAdapter(context,arrayList);
        pref = getActivity().getSharedPreferences(AppConstants.PREF_NAME,0);
        refer_count = (TextView)view.findViewById(R.id.referee_count_text);
        refer_credit = (TextView)view.findViewById(R.id.refer_credit_value);
        refer_viewall = (TextView)view.findViewById(R.id.refer_viewall);
        refer_recycle = (RecyclerView)view.findViewById(R.id.referee_recycle);
        LinearLayoutManager verticalmanager = new LinearLayoutManager(context, 0, false);
        verticalmanager.setOrientation(LinearLayoutManager.VERTICAL);
        refer_recycle.setLayoutManager(verticalmanager);
        refer_recycle.setAdapter(referMoreAdapter);
        refer_more_btn = (Button)view.findViewById(R.id.refer_more_broker_btn);
        referback2 = (ImageView)view.findViewById(R.id.refer_back2);
        referApi();
        createText();
    }
    private void createText() {
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
    }

/*
 * Methods used above.
 */

    private SpannableString makeLinkSpan(CharSequence text, View.OnClickListener listener) {
        SpannableString link = new SpannableString(text);
        link.setSpan(new ReferFragmentMore.ClickableString(listener), 0, text.length(),
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        link.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.appColor)), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
        referApi();
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
                            refer_credit.setText(credit);
                            refer_count.setText("My referees(" + count + ")");
                            if (statusCode == 200 && message.equalsIgnoreCase("")) {
                                ArrayList<ApiModel.referredBrokerObject> referred_broker_list = referralData.getData().getReferredBroker();
                                if (referred_broker_list.size() != 0) {
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
                                }
                                referMoreAdapter.notifyDataSetChanged();
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
                                if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                    new AllUtils().getTokenRefresh(context);
                                    referApi();
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
                public void onFailure(Call<ApiModel.ReferralData> call, Throwable t) {
                    Utils.LoaderUtils.dismissLoader();
                    Toast.makeText(context, "Some Problem Occured", Toast.LENGTH_SHORT).show();
                /*if(pd.isShowing()) {
                    pd.dismiss();
                }*/
                }
            });
        }else{
            Utils.internetDialog(context,this);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        Utils.LoaderUtils.dismissLoader();
    }
}
