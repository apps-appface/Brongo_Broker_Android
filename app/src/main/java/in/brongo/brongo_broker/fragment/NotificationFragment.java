package in.brongo.brongo_broker.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.adapter.NotiAdapter;
import in.brongo.brongo_broker.model.ApiModel;
import in.brongo.brongo_broker.other.AllUtils;
import in.brongo.brongo_broker.other.NoInternetTryConnectListener;
import in.brongo.brongo_broker.other.NoTokenTryListener;
import in.brongo.brongo_broker.util.AppConstants;
import in.brongo.brongo_broker.util.RetrofitAPIs;
import in.brongo.brongo_broker.util.RetrofitBuilders;
import in.brongo.brongo_broker.util.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment implements NotiAdapter.CallListener,NoInternetTryConnectListener,NoTokenTryListener,AllUtils.test {
    private ArrayList<ApiModel.NotificationChildModel> arrayList;
    private ImageView edit_icon,delete_icon,add_icon;
    private SharedPreferences pref;
    private TextView toolbar_title;
    private Toolbar toolbar;
    private LinearLayout no_noti_linear;
    private Context context;
    private RelativeLayout parentLayout;
    protected Handler handler;
    private NotiAdapter notificationAdapter;
    private ApiModel.NotificationChildModel notificationItemModel;
    /*private ProgressDialog pd;*/
    private int apiCode, size=20;
    private int noti_position,unread,from=0;
    String client_mobile,client_property_id;
    private boolean isNotified=false ,isLoader= false;
    private int taskCompleted = 0;
    private SharedPreferences.Editor editor;


    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        client_mobile = client_property_id ="";
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        arrayList = new ArrayList<>();
        context = getActivity();
        /*pd = new ProgressDialog(context, R.style.CustomProgressDialog);
        pd.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress_loader));
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(true);
        pd.getWindow().setGravity(Gravity.BOTTOM);*/
        pref = getActivity().getSharedPreferences(AppConstants.PREF_NAME,0);
        editor = pref.edit();
        parentLayout = (RelativeLayout)getActivity().findViewById(R.id.menu_parent_relative);
        unread = pref.getInt(AppConstants.NOTIFICATION_BADGES,0);
        notificationItemModel = new ApiModel.NotificationChildModel();
        editor.putInt(AppConstants.NOTIFICATION_BADGES,0).commit();
        RecyclerView noti_recycle = (RecyclerView)view.findViewById(R.id.notification_recycle);
        no_noti_linear = (LinearLayout)view.findViewById(R.id.no_notification_linear);
        no_noti_linear.setVisibility(View.GONE);
        edit_icon = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_edit);
        delete_icon =(ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_delete);
        add_icon = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_add);
        edit_icon.setVisibility(View.GONE);
        delete_icon.setVisibility(View.GONE);
        add_icon.setVisibility(View.GONE);
        toolbar_title = (TextView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.inventory_toolbar_title);
        toolbar = (Toolbar)getActivity().findViewById(R.id.inventory_toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar_title.setText("Notifications");
        LinearLayoutManager verticalmanager = new LinearLayoutManager(context, 0, false);
        verticalmanager.setOrientation(LinearLayoutManager.VERTICAL);
        noti_recycle.setLayoutManager(verticalmanager);
         notificationAdapter = new NotiAdapter(context,arrayList,noti_recycle,this);
        noti_recycle.setAdapter(notificationAdapter);
        startLoader();
        populateNotification(from,size);

        notificationAdapter.setOnLoadMoreListener(new NotiAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //add null , so the adapter will check view_type and show progress bar at bottom

                        //   remove progress item
                        //add items one by one

                        //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
                Handler handler = new Handler();

                final Runnable r = new Runnable() {
                    public void run() {
                        if (!arrayList.contains(null)) {
                            arrayList.add(null);
                            notificationAdapter.notifyItemInserted(arrayList.size() - 1);
                        }
                        ++from;
                        populateNotification(from,size);
                    }
                };

                handler.post(r);

            }
        });
        return view;
    }
    private void populateNotification(final int i, final int size){
       if (Utils.isNetworkAvailable(context)) {
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
            Call<ApiModel.NotificationModel> call = retrofitAPIs.getNotificationApi(tokenaccess, "android", deviceId, mobileNo, i, size);
            call.enqueue(new Callback<ApiModel.NotificationModel>() {
                @Override
                public void onResponse(Call<ApiModel.NotificationModel> call, Response<ApiModel.NotificationModel> response) {
                    stopLoader();
                    if(from !=0) {
                        arrayList.remove(arrayList.size() - 1);
                        notificationAdapter.notifyItemRemoved(arrayList.size());
                    }
                    if (response != null) {
                        if (response.isSuccessful()) {
                            ApiModel.NotificationModel notificationModel = response.body();
                            int statusCode = notificationModel.getStatusCode();
                            String message = notificationModel.getMessage();
                            if (statusCode == 200 && message.equalsIgnoreCase("")) {
                                ArrayList<ApiModel.NotificationChildModel> noti_list = notificationModel.getData();
                                if (noti_list.size() != 0) {
                                    arrayList.addAll(noti_list);
                                    notificationAdapter.notifyDataSetChanged();
                                    if(noti_list.size()==size){
                                        notificationAdapter.setLoaded(false);
                                    }else{
                                        notificationAdapter.setLoaded(true);
                                    }
                                }else if(i == 0 && noti_list.size()==0){
                                    no_noti_linear.setVisibility(View.VISIBLE);
                                }
                           /* if(noti_list.size()<size){
                                notificationAdapter.setLoaded();
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
                                    apiCode = 100;
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
                public void onFailure(Call<ApiModel.NotificationModel> call, Throwable t) {
                    if(from !=0) {
                        arrayList.remove(arrayList.size() - 1);
                        notificationAdapter.notifyItemRemoved(arrayList.size());
                    }
                    stopLoader();
                    Utils.showToast(context, t.getLocalizedMessage().toString(),"Failure");
                }
            });
        }else{
           taskCompleted = 100;
           Utils.internetDialog(context,this);
       }
    }

    @Override
    public void callBtnClick(String phone,String propertyId) {
        callClient(phone,propertyId);
    }

    @Override
    public void readBtnClick(ApiModel.NotificationChildModel notificationChildModel,int position,boolean isDataSet) {
        readNotification(notificationChildModel,position,isDataSet);
    }

    @Override
    public void proceedBtnClick(ApiModel.NotificationChildModel notificationChildModel, int position) {
        tc_dialog(notificationChildModel,position);
        if(notificationChildModel.isRead()==false) {
            readNotification(notificationChildModel, position, true);
        }
    }

    @Override
    public void rejectBtnClick(ApiModel.NotificationChildModel notificationChildModel, int position) {
        builderRejectApi(notificationChildModel,position);

    }

    private void callClient(final String lead_mobile, final String propertyId) {
        client_mobile = lead_mobile;
        client_property_id = propertyId;
        if(Utils.isNetworkAvailable(context)) {
            startLoader();
            String client_no = lead_mobile;
            String brokerno = (pref.getString(AppConstants.MOBILE_NUMBER, ""));
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            ApiModel.KnowlarityModel knowlarityModel = new ApiModel.KnowlarityModel();
            knowlarityModel.setFrom(brokerno);
            knowlarityModel.setTo(client_no);
            knowlarityModel.setDealId(propertyId);
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());

            Call<ResponseBody> call = retrofitAPIs.callKnowlarityApi(tokenaccess, "android", deviceId, knowlarityModel);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    stopLoader();
                    if (response != null) {
                        String responseString = null;
                        if (response.isSuccessful()) {
                            try {
                                responseString = response.body().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                String message = jsonObject.optString("message");
                                int statusCode = jsonObject.optInt("statusCode");
                                if (statusCode == 200 && message.equalsIgnoreCase("You Can Processed With Call")) {
                                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                                    callIntent.setData(Uri.parse("tel:" + "+919590224224"));
                                    startActivity(callIntent);
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
                                    Utils.setSnackBar(parentLayout,"Please try again");
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
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    stopLoader();
                    Utils.showToast(context, t.getLocalizedMessage().toString(),"Failure");
                }
            });
        }else{
            taskCompleted = 200;
            Utils.internetDialog(context,this);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        stopLoader();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    stopLoader();
    }

    @Override
    public void onTryReconnect() {
        switch (taskCompleted){
            case 100:
                populateNotification(from,size);
                break;
            case 200:
               callClient(client_mobile,client_property_id);
                break;
            case 300:
              readNotification(notificationItemModel,noti_position,isNotified);
                break;
            case 400:
                accept_builder(notificationItemModel,noti_position);
                break;
            case 500:
                builderRejectApi(notificationItemModel,noti_position);
                break;
        }

    }
    private void readNotification(final ApiModel.NotificationChildModel notificationChildModel, final int position, final boolean isDataSet) {
        notificationItemModel = notificationChildModel;
        noti_position = position;
        isNotified = isDataSet;
        if(Utils.isNetworkAvailable(context)) {
            startLoader();
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
            String id = notificationChildModel.getId();
            Call<ResponseBody> call = retrofitAPIs.readNotificationApi(tokenaccess, "android", deviceId, mobileNo, id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    stopLoader();
                    if (response != null) {
                        String responseString = null;
                        if (response.isSuccessful()) {
                            JSONObject jsonObject = null;
                            try {
                                responseString = response.body().string();
                                jsonObject = new JSONObject(responseString);
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                            int statusCode = jsonObject.optInt("statusCode");
                            String message = jsonObject.optString("message");
                            if (statusCode == 200 && message.equalsIgnoreCase("Updated Successfully")) {
                                arrayList.get(position).setRead(true);
                                    //arrayList.add(position,notificationChildModel);
                                if(isDataSet) {
                                    notificationAdapter.notifyDataSetChanged();
                                }
                            }
                            // referAdapter.notifyDataSetChanged();
                        } else {
                            try {
                                responseString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                int statusCode = jsonObject.optInt("statusCode");
                                String message = jsonObject.optString("message");
                                if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                    new AllUtils().getTokenRefresh(context);
                                    Utils.setSnackBar(parentLayout,"Please try again");
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
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    stopLoader();
                    Utils.showToast(context, t.getLocalizedMessage().toString(),"Failure");
                /*if(pd.isShowing()) {
                    pd.dismiss();
                }*/
                }
            });
        }else{
            taskCompleted = 300;
            Utils.internetDialog(context,this);
        }
    }
    private void accept_builder(final ApiModel.NotificationChildModel notificationChildModel, final int position){
        notificationItemModel = notificationChildModel;
        noti_position = position;
        if(Utils.isNetworkAvailable(context)) {
            startLoader();
            ApiModel.BuilderAcceptModel builderAcceptModel = new ApiModel.BuilderAcceptModel();
            builderAcceptModel.setBrokerMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
            builderAcceptModel.setPropertyId(notificationChildModel.getPropertyId());
            builderAcceptModel.setUserId(notificationChildModel.getUserId());
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            Call<ApiModel.ResponseModel> call = retrofitAPIs.acceptBuilderApi(tokenaccess, "android", deviceId, builderAcceptModel);
            call.enqueue(new Callback<ApiModel.ResponseModel>() {
                @Override
                public void onResponse(Call<ApiModel.ResponseModel> call, Response<ApiModel.ResponseModel> response) {
                    stopLoader();
                    if (response != null) {
                        String responseString = null;
                        if (response.isSuccessful()) {
                            ApiModel.ResponseModel responseModel = response.body();
                            int statusCode = responseModel.getStatusCode();
                            String message = responseModel.getMessage();
                            if (statusCode == 200 && message.equalsIgnoreCase("Builder And Broker Connection Is Established")) {
                                arrayList.get(position).setStatus("accept");
                                notificationAdapter.notifyDataSetChanged();
                                Utils.setSnackBar(parentLayout,message);
                            }
                        } else {
                            try {
                                responseString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                String message = jsonObject.optString("message");
                                int statusCode = jsonObject.optInt("statusCode");
                                if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                    new AllUtils().getTokenRefresh(context);
                                    Utils.setSnackBar(parentLayout,"Please try again");
                                }else{
                                    Utils.setSnackBar(parentLayout,message);
                                }
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiModel.ResponseModel> call, Throwable t) {
                    stopLoader();
                    Utils.showToast(context, t.getLocalizedMessage().toString(),"Failure");
                }
            });
        }else{
            taskCompleted = 400;
            Utils.internetDialog(context,this);
        }
    }
    private void builderRejectApi(final ApiModel.NotificationChildModel notificationChildModel,final int position){
        notificationItemModel = notificationChildModel;
        noti_position = position;
        if (Utils.isNetworkAvailable(context)) {
            startLoader();
            ApiModel.ClientAcceptModel clientAcceptModel = new ApiModel.ClientAcceptModel();
            clientAcceptModel.setClientMobileNo(notificationChildModel.getUserId());
            clientAcceptModel.setBrokerMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
            clientAcceptModel.setPostingType("");
            clientAcceptModel.setPropertyId(notificationChildModel.getPropertyId());
            clientAcceptModel.setReason("");
            clientAcceptModel.setPostedUser("builder");
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            Call<ResponseBody> call = retrofitAPIs.rejectLeadApi(tokenaccess, "android", deviceId, clientAcceptModel);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    stopLoader();
                    if (response != null) {
                        String responseString = null;
                        if (response.isSuccessful()) {
                            try {
                                responseString = response.body().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                String message = jsonObject.optString("message");
                                int statusCode = jsonObject.optInt("statusCode");
                                if (statusCode == 200 ) {
                                    arrayList.get(position).setStatus("reject");
                                    notificationAdapter.notifyDataSetChanged();
                                    if(notificationChildModel.isRead()==false) {
                                        readNotification(notificationChildModel, position, true);
                                    }
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
                                    Utils.setSnackBar(parentLayout,"Please try again");
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
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    stopLoader();
                    Utils.showToast(context, t.getLocalizedMessage().toString(),"Failure");
                }
            });
        }else{
            taskCompleted = 500;
            Utils.internetDialog(context,this);
        }
    }

    private void tc_dialog(final ApiModel.NotificationChildModel notificationChildModel ,final int position){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_tc);
        //dialog.setCanceledOnTouchOutside(false);
        // dialog.setCancelable(false);
        final ImageView cross_btn = (ImageView) dialog.findViewById(R.id.tc_close_btn);
        final Button accept_btn = (Button)dialog.findViewById(R.id.tcDialog_accept);
        TextView commission_text = (TextView)dialog.findViewById(R.id.tcDialog_commission);
        if(arrayList.get(position).getCommission() != null) {
            commission_text.setText(notificationChildModel.getCommission() + "% Commission");
        }
        cross_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accept_builder(notificationChildModel,position);
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.show();
    }
    private void startLoader(){
        if(!isLoader){
            Utils.LoaderUtils.showLoader(context);
            isLoader = true;
        }
    }
    private void stopLoader(){
        if(isLoader){
            Utils.LoaderUtils.dismissLoader();
            isLoader = false;
        }
    }

    @Override
    public void onTryRegenerate() {
        switch (apiCode){
            case 100:
               getToken(context);
                break;
        }
    }
    private void openTokenDialog(Context context){
        Utils.tokenDialog(context,this);
    }
    private void getToken(Context context){
        new AllUtils().getToken(context,this);
    }
    @Override
    public void onSuccessRes(boolean isSuccess) {
        if(isSuccess){
            populateNotification(from,size);
        }else{
            Utils.LoaderUtils.dismissLoader();
         openTokenDialog(context);
        }
    }
}
