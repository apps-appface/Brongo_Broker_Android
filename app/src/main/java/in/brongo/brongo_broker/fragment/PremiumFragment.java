package in.brongo.brongo_broker.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Payu.PayuConstants;
import com.payu.payuui.Activity.PayUBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.adapter.CustomAdapter;
import in.brongo.brongo_broker.model.PaymentHashModel;
import in.brongo.brongo_broker.model.PaymentHashResponseModel;
import in.brongo.brongo_broker.other.AllUtils;
import in.brongo.brongo_broker.other.NoInternetTryConnectListener;
import in.brongo.brongo_broker.other.NoTokenTryListener;
import in.brongo.brongo_broker.other.RetryPaymentListener;
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
public class PremiumFragment extends Fragment implements NoInternetTryConnectListener,RetryPaymentListener,NoTokenTryListener,AllUtils.test{
    ArrayList<PaymentHashModel.SubPlanObject> arrayList;
    LinearLayout plans_linear;
    private ImageView edit_icon,delete_icon,add_icon;
    private TextView toolbar_title;
    private Toolbar toolbar;
    private Context context;
    private RelativeLayout parentLayout;
    private int Task_completed = 10000;
    private RecyclerView premium_condition_recycle;
   private CustomAdapter premiumAdapter;
   private ArrayList<String> premium_conditionlist;
    private SharedPreferences pref;

    public PremiumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_premium, container, false);
        initialise(view);
        return view;
    }
    private void initialise(View view){
        try {
            context = getActivity();
            pref = context.getSharedPreferences(AppConstants.PREF_NAME,0);
            parentLayout = getActivity().findViewById(R.id.menu_parent_relative);
            plans_linear = view.findViewById(R.id.premium_sub_linear);
            edit_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_edit);
            delete_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_delete);
            add_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_add);
            edit_icon.setVisibility(View.GONE);
            delete_icon.setVisibility(View.GONE);
            add_icon.setVisibility(View.GONE);
            premium_condition_recycle = view.findViewById(R.id.premium_list);
            toolbar_title = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.inventory_toolbar_title);
            toolbar = getActivity().findViewById(R.id.inventory_toolbar);
            toolbar.setVisibility(View.VISIBLE);
            toolbar_title.setText("Upgrade to premium");
            LinearLayoutManager verticalmanager = new LinearLayoutManager(context, 0, false);
            verticalmanager.setOrientation(LinearLayoutManager.VERTICAL);
            premium_condition_recycle.setLayoutManager(verticalmanager);
            arrayList = new ArrayList<>();
            premium_conditionlist = new ArrayList<>();
            premiumAdapter = new CustomAdapter(premium_conditionlist,context);
            premium_condition_recycle.setAdapter(premiumAdapter);
            fetchCurrentPlan();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void addPlans(ArrayList<PaymentHashModel.SubPlanObject> arrayList){
        for (int i = 0; i < arrayList.size(); i++) {
            try {
                String actualRate = AllUtils.changeNumberFormat(arrayList.get(i).getAmountToSub());
                String dis_rate = AllUtils.changeNumberFormat(arrayList.get(i).getPayed());
                String perMonth_rate = AllUtils.changeNumberFormat(arrayList.get(i).getAmountFMonth());
                final int position = i;
                View layout2 = LayoutInflater.from(getActivity()).inflate(R.layout.premium_subplan_child, plans_linear, false);
                Button pay_btn = layout2.findViewById(R.id.pay_btn);
                TextView plan_type = layout2.findViewById(R.id.premium_plan_type);
                TextView plan_actual_rate = layout2.findViewById(R.id.premium_actual_rate);
                TextView plan_paying_rate = layout2.findViewById(R.id.premium_paying_rate);
                TextView plan_offer = layout2.findViewById(R.id.premium_save);
                TextView plan_net_worth = layout2.findViewById(R.id.premium_net_worth);
                plan_type.setText(arrayList.get(i).getName());
                plan_actual_rate.setText(actualRate);
                if(!(arrayList.get(i).getOffers() == 0.0f)) {
                    plan_paying_rate.setText(dis_rate);
                    plan_offer.setText(arrayList.get(i).getOffers()+"%");
                    if(arrayList.get(i).getAmountToSub() != arrayList.get(i).getPayed()) {
                        plan_actual_rate.setPaintFlags(plan_actual_rate.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                }
                plan_net_worth.setText(perMonth_rate);
                final int finalI = i;
                pay_btn.setEnabled(false);
                pay_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                plans_linear.addView(layout2);
            } catch (Exception e) {
                e.printStackTrace();
                String error = e.toString();
            }
        }
    }
    public void MakePayment(final int i) {
        if (Utils.isNetworkAvailable(context)) {
            final String baseUrl = "https://devapi.brongo.in/QuickBroker/broker";
            String headerDeviceId = Utils.getDeviceId(context);
            String headerPlatform = "android";
            String headerToken = pref.getString("token", "");
            final String userMobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
            final String firstName = pref.getString(AppConstants.FIRST_NAME, "");
            final String email = pref.getString(AppConstants.EMAIL_ID, "");

            String postingType = "", propertyId = "", brokerNo = "";
            PaymentHashModel paymentHashModel = new PaymentHashModel();
            paymentHashModel.setAmount("1");
            paymentHashModel.setFirstname(firstName);
            paymentHashModel.setProductInfo("subscription");
            paymentHashModel.setEmail(email);
            paymentHashModel.setMobileNo(userMobileNo);
            paymentHashModel.setPropertyId(propertyId);
            paymentHashModel.setPaymentMode("Development");

            Utils.LoaderUtils.showLoader(context);
            RetrofitAPIs apiInstance = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            Call<PaymentHashResponseModel> call = apiInstance.getPaymentHash(headerToken, headerPlatform, headerDeviceId, paymentHashModel);
            call.enqueue(new Callback<PaymentHashResponseModel>() {
                @Override
                public void onResponse(Call<PaymentHashResponseModel> call, Response<PaymentHashResponseModel> response) {
                    Utils.LoaderUtils.dismissLoader();
                    if (response != null && response.body() != null && response.isSuccessful()) {
                        PaymentHashResponseModel hashResponseModel = response.body();
                        List<PaymentHashResponseModel.Data> data = hashResponseModel.getData();

                        PaymentParams paymentParams = new PaymentParams();
                        paymentParams.setKey("gtKFFx");                  //DEVELOPMENT
                        //paymentParams.setKey("FHOPnO");               //PRODUCTION
                        paymentParams.setTxnId(data.get(0).getTxnid());

                        paymentParams.setAmount("1");
                        paymentParams.setProductInfo("subscription");
                        paymentParams.setFirstName(firstName);
                        //paymentParams.setVpa(data.get(0).getVapsHash());
                        paymentParams.setEmail(email);
                        paymentParams.setUdf1("");
                        paymentParams.setUdf2("");
                        paymentParams.setUdf3("");
                        paymentParams.setUdf4("");
                        paymentParams.setUdf5("");
                        paymentParams.setPhone(userMobileNo);

                        paymentParams.setSurl(baseUrl + "/paymentStatus");
                        paymentParams.setFurl(baseUrl + "/paymentStatus");

                        PayuHashes payuHashes = new PayuHashes();
                        payuHashes.setPaymentHash(data.get(0).getSha512());
                        payuHashes.setVasForMobileSdkHash(data.get(0).getVapsHash());                       //
                        payuHashes.setPaymentRelatedDetailsForMobileSdkHash(data.get(0).getPaymentHash());              //
                        //payuHashes.setPaymentRelatedDetailsForMobileSdkHash(data.get(0).getPayment_related_details_for_mobile_sdk_hash());              //


                        paymentParams.setHash(payuHashes.getPaymentHash());
                        paymentParams.setUserCredentials(userMobileNo + ":Brongo_Client");

                        PayuConfig payuConfig = new PayuConfig();
                        payuConfig.setEnvironment(PayuConstants.MOBILE_STAGING_ENV);
                        //payuConfig.setEnvironment(PayuConstants.PRODUCTION_ENV);

                        Intent intent = new Intent(getActivity(), PayUBaseActivity.class);
                        intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
                        intent.putExtra(PayuConstants.PAYMENT_PARAMS, paymentParams);
                        intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);
                        startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
                    } else {

                    }
                }

                @Override
                public void onFailure(Call<PaymentHashResponseModel> call, Throwable t) {
                    Utils.LoaderUtils.dismissLoader();
                   // AllUtils.DialogUtils.PaymentErrorDialog(context, PremiumFragment.this);
                }
            });
        } else {
          Utils.internetDialog(context, this);
        }

    }

    @Override
    public void onTryReconnect() {
        switch (Task_completed){
            case 200:
                fetchCurrentPlan();
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            if (data != null) {

                /**
                 * Here, data.getStringExtra("payu_response") ---> Implicit response sent by PayU
                 * data.getStringExtra("result") ---> Response received from merchant's Surl/Furl
                 *
                 * PayU sends the same response to merchant server and in app. In response check the value of key "status"
                 * for identifying status of transaction. There are two possible status like, success or failure
                 * */
                try {
                    String result = new JSONObject(data.getStringExtra("payu_response")).optString("status");
                    if (result != null) {
                        switch (result) {
                            case "success":
                                AllUtils.PaymentSuccessDialog(context);
                                break;
                            case "failure":
                                AllUtils.PaymentFailedDialog(context, this);
                                break;
                        }
                    }
                } catch (JSONException e) {
                    AllUtils.PaymentErrorDialog(context, this);
                    e.printStackTrace();
                }

            } else {
                AllUtils.PaymentErrorDialog(context, this);
            }
        } else {
            AllUtils.PaymentErrorDialog(context, this);
        }
    }

    @Override
    public void RetryPayment() {

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.LoaderUtils.dismissLoader();
    }
    private void fetchCurrentPlan(){
        try {
            if(Utils.isNetworkAvailable(context)) {
                Utils.LoaderUtils.showLoader(context);
                RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
                String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
                String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
                String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
                String premium_id = pref.getString(AppConstants.PREMIUM_ID,"");
                Call<PaymentHashModel.CurrentPlanModel> call = retrofitAPIs.getCurrentPlanApi(tokenaccess, "android", deviceId, mobileNo,premium_id);
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
                                        ArrayList<String> conditionList = currentPlanModel.getData().get(0).getConditions();
                                        if(conditionList.size() != 0){
                                            premium_conditionlist.clear();
                                            premium_conditionlist.addAll(conditionList);
                                            premiumAdapter.notifyDataSetChanged();
                                        }
                                       ArrayList<PaymentHashModel.SubPlanObject> subplanList = subscription_list.get(0).getSubPlans();
                                       if(subplanList.size() != 0){
                                         arrayList.clear();
                                         arrayList.addAll(subplanList);
                                         addPlans(arrayList);
                                       }
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
                                        openTokenDialog(context);
                                    } else {
                                        Utils.setSnackBar(parentLayout,message);
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
                    public void onFailure(Call<PaymentHashModel.CurrentPlanModel> call, Throwable t) {
                        Utils.showToast(context, t.getLocalizedMessage().toString(),"Failure");
                        Utils.LoaderUtils.dismissLoader();
                    /*if(pd.isShowing()) {
                        pd.dismiss();
                    }*/
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

    @Override
    public void onTryRegenerate() {
        getToken(context);
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
            fetchCurrentPlan();
        }else{
            Utils.LoaderUtils.dismissLoader();
            openTokenDialog(context);
        }
    }
}
