package appface.brongo.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import appface.brongo.R;
import appface.brongo.adapter.NotificationAdapter;
import appface.brongo.model.ApiModel;
import appface.brongo.other.AllUtils;
import appface.brongo.other.NoInternetTryConnectListener;
import appface.brongo.util.AppConstants;
import appface.brongo.util.RefreshTokenCall;
import appface.brongo.util.RetrofitAPIs;
import appface.brongo.util.RetrofitBuilders;
import appface.brongo.util.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment implements NotificationAdapter.CallListener,NoInternetTryConnectListener {
    private ArrayList<ApiModel.NotificationChildModel> arrayList;
    private SharedPreferences pref;
    private LinearLayout no_noti_linear;
    private Context context;
    private NotificationAdapter notificationAdapter;
    /*private ProgressDialog pd;*/
    private int size=20;
    private int unread,from=0;
    private SharedPreferences.Editor editor;


    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        unread = pref.getInt(AppConstants.NOTIFICATION_BADGES,0);
        editor.putInt(AppConstants.NOTIFICATION_BADGES,0).commit();
        RecyclerView noti_recycle = (RecyclerView)view.findViewById(R.id.notification_recycle);
        no_noti_linear = (LinearLayout)view.findViewById(R.id.no_notification_linear);
        no_noti_linear.setVisibility(View.GONE);
        LinearLayoutManager verticalmanager = new LinearLayoutManager(context, 0, false);
        verticalmanager.setOrientation(LinearLayoutManager.VERTICAL);
        noti_recycle.setLayoutManager(verticalmanager);
         notificationAdapter = new NotificationAdapter(context,arrayList,noti_recycle,this);
        noti_recycle.setAdapter(notificationAdapter);
        populateNotification(from,size);

        notificationAdapter.setOnLoadMoreListener(new NotificationAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //add null , so the adapter will check view_type and show progress bar at bottom
                ++from;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            populateNotification(from,size);
                            //Generating more data
                        }
                    }, 1500);
                }
        });
        return view;
    }
    private void populateNotification(final int i, final int size){
       if (Utils.isNetworkAvailable(context)) {
            Utils.LoaderUtils.showLoader(context);
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
            Call<ApiModel.NotificationModel> call = retrofitAPIs.getNotificationApi(tokenaccess, "android", deviceId, mobileNo, i, size);
            call.enqueue(new Callback<ApiModel.NotificationModel>() {
                @Override
                public void onResponse(Call<ApiModel.NotificationModel> call, Response<ApiModel.NotificationModel> response) {
                    Utils.LoaderUtils.dismissLoader();
                    if (response != null) {
                        if (response.isSuccessful()) {
                            ApiModel.NotificationModel notificationModel = response.body();
                            int statusCode = notificationModel.getStatusCode();
                            String message = notificationModel.getMessage();
                            if (statusCode == 200 && message.equalsIgnoreCase("")) {
                                ArrayList<ApiModel.NotificationChildModel> noti_list = notificationModel.getData();
                                if (noti_list.size() != 0) {
                                    for (int j = 0; j < noti_list.size(); j++) {
                                        arrayList.add(noti_list.get(j));
                                    }
                                    notificationAdapter.notifyDataSetChanged();
                                    notificationAdapter.setLoaded();
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
                                    new AllUtils().getTokenRefresh(context);
                                    populateNotification(i, size);
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
                public void onFailure(Call<ApiModel.NotificationModel> call, Throwable t) {
                    Utils.LoaderUtils.dismissLoader();
                    Utils.showToast(context, "Some Problem Occured");
                }
            });
        }else{
           Utils.internetDialog(context,this);
       }
    }

    @Override
    public void callBtnClick(String phone,String propertyId) {
        callClient(phone,propertyId);
    }
    private void callClient(final String lead_mobile, final String propertyId) {
        if(Utils.isNetworkAvailable(context)) {
            String client_no = lead_mobile;
            String brokerno = (pref.getString(AppConstants.MOBILE_NUMBER, ""));
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            ApiModel.KnowlarityModel knowlarityModel = new ApiModel.KnowlarityModel();
            knowlarityModel.setFrom(brokerno);
            knowlarityModel.setTo(client_no);
            knowlarityModel.setPropertyId(propertyId);
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());

            Call<ResponseBody> call = retrofitAPIs.callKnowlarityApi(tokenaccess, "android", deviceId, knowlarityModel);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
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
                                    callBtnClick(lead_mobile, propertyId);
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
                    Toast.makeText(context, "some error occured", Toast.LENGTH_SHORT);
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

    @Override
    public void onTryReconnect() {
        populateNotification(from,size);
    }
}
