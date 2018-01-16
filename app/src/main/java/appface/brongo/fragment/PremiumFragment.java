package appface.brongo.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Payu.PayuConstants;
import com.payu.payuui.Activity.PayUBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import appface.brongo.R;
import appface.brongo.activity.ReminderActivity;
import appface.brongo.model.PaymentHashModel;
import appface.brongo.model.PaymentHashResponseModel;
import appface.brongo.other.AllUtils;
import appface.brongo.other.NoInternetTryConnectListener;
import appface.brongo.other.RetryPaymentListener;
import appface.brongo.util.AppConstants;
import appface.brongo.util.RetrofitAPIs;
import appface.brongo.util.RetrofitBuilders;
import appface.brongo.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class PremiumFragment extends Fragment implements NoInternetTryConnectListener,RetryPaymentListener {
    ArrayList<String> arrayList;
    LinearLayout plans_linear;
    private ImageView edit_icon,delete_icon,add_icon;
    private TextView toolbar_title;
    private Toolbar toolbar;
    private Context context;
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
        context = getActivity();
        pref = context.getSharedPreferences(AppConstants.PREF_NAME,0);
        plans_linear = (LinearLayout)view.findViewById(R.id.premium_sub_linear);
        edit_icon = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_edit);
        delete_icon = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_delete);
        add_icon = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_add);
        edit_icon.setVisibility(View.GONE);
        delete_icon.setVisibility(View.GONE);
        add_icon.setVisibility(View.GONE);
        toolbar_title = (TextView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.inventory_toolbar_title);
        toolbar = (Toolbar)getActivity().findViewById(R.id.inventory_toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar_title.setText("Upgrade to premium");
        arrayList = new ArrayList<>();
        arrayList.add("plan1");
        arrayList.add("plan2");
        addPlans(arrayList);
    }
    private void addPlans(ArrayList<String> arrayList){
        for (int i = 0; i < arrayList.size(); i++) {
            try {
                final int position = i;
                View layout2 = LayoutInflater.from(getActivity()).inflate(R.layout.premium_subplan_child, plans_linear, false);
                Button pay_btn = (Button)layout2.findViewById(R.id.pay_btn);
                TextView plan_type = (TextView)layout2.findViewById(R.id.premium_plan_type);
                plan_type.setText(arrayList.get(i));
                pay_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // MakePayment(position);
                    }
                });
                plans_linear.addView(layout2);
            } catch (Exception e) {
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
}
