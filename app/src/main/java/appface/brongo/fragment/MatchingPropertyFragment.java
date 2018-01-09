package appface.brongo.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import appface.brongo.R;
import appface.brongo.adapter.InventoryPersonalAdapter;
import appface.brongo.adapter.MatchingAdapter;
import appface.brongo.model.ApiModel;
import appface.brongo.other.NoInternetTryConnectListener;
import appface.brongo.util.AppConstants;
import appface.brongo.util.DatabaseHandler;
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
public class MatchingPropertyFragment extends Fragment implements NoInternetTryConnectListener {
    private Context context;
    private RecyclerView matching_recycle;
    private MatchingAdapter matchingAdapter;
    private ArrayList<ApiModel.MatchingModel> arraylist = new ArrayList<>();;
    private SharedPreferences pref;
    ProgressDialog pd;
    private String posting_type,brokerMobile,propertyType,subPropertyType,microMarketName;

    public MatchingPropertyFragment() {
        // Required empty public constructor
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           posting_type  = getArguments().getString("lead_posting_type");
            propertyType  = getArguments().getString("lead_prop_type");
            subPropertyType  = getArguments().getString("lead_sub_prop_type");
            microMarketName  = getArguments().getString("lead_address");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_matching_property, container, false);
        initialise(view);
        return view;
    }

    private void initialise(View view){
        context = getActivity();
        matching_recycle = (RecyclerView)view.findViewById(R.id.matching_prop_recycle);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        matching_recycle.setLayoutManager(layoutManager);
        matchingAdapter = new MatchingAdapter(context,arraylist,getFragmentManager());
        matching_recycle.setAdapter(matchingAdapter);
        pref =context.getSharedPreferences(AppConstants.PREF_NAME,0);
        fetchList();
        pd = new ProgressDialog(context, R.style.MyDialogTheme);
        pd.setCancelable(false);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
       // pd.show();
    }
    private void fetchList(){
        DatabaseHandler db = new DatabaseHandler(context);
        Cursor cursor = db.getListItem();
        if (cursor != null) {
            cursor.moveToNext();
            for(int i =0;i<cursor.getCount();i++) {
                String post_type = cursor.getString(cursor.getColumnIndex("postingtype"));
                String prop_type = cursor.getString(cursor.getColumnIndex("propertytype"));
                if (Utils.match_deal(posting_type, post_type, propertyType, prop_type)) {
                    ApiModel.MatchingModel matchingModel = new ApiModel.MatchingModel();
                    matchingModel.setAddress(cursor.getString(cursor.getColumnIndex("address")));
                    matchingModel.setArea(cursor.getString(cursor.getColumnIndex("area")));
                    matchingModel.setBhk(cursor.getString(cursor.getColumnIndex("bhk")));
                    matchingModel.setBudget(cursor.getString(cursor.getColumnIndex("budget")));
                    matchingModel.setCommission(cursor.getString(cursor.getColumnIndex("commission")));
                    matchingModel.setDealid(cursor.getString(cursor.getColumnIndex("dealid")));
                    matchingModel.setImage(cursor.getString(cursor.getColumnIndex("image")));
                    matchingModel.setInventory_type(cursor.getString(cursor.getColumnIndex("inventory_type")));
                    matchingModel.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
                    matchingModel.setPostingtype(cursor.getString(cursor.getColumnIndex("postingtype")));
                    matchingModel.setPropertystatus(cursor.getString(cursor.getColumnIndex("propertystatus")));
                    matchingModel.setPropertytype(cursor.getString(cursor.getColumnIndex("propertytype")));
                    matchingModel.setSubproperty(cursor.getString(cursor.getColumnIndex("subproperty")));
                    matchingModel.setName(cursor.getString(cursor.getColumnIndex("name")));
                    arraylist.add(matchingModel);
                }
                cursor.moveToNext();
            }
        }

       /* RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
        ApiModel.InventoryPersoanlList inventoryPersoanlList = new ApiModel.InventoryPersoanlList();
        inventoryPersoanlList.setBrokerMobileNo(pref.getString(AppConstants.MOBILE_NUMBER,""));
        inventoryPersoanlList.setPostingType(posting_type.toUpperCase());
        inventoryPersoanlList.setSubPropertyType(subPropertyType);
        inventoryPersoanlList.setPropertyType(propertyType.toUpperCase());
        inventoryPersoanlList.setMicroMarketName(microMarketName);
        String deviceId = Utils.getDeviceId(context);
        String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
        String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
        Call<ApiModel.InventoryModel> call = retrofitAPIs.getMatchingInventory(tokenaccess, "android", deviceId, inventoryPersoanlList);
        call.enqueue(new Callback<ApiModel.InventoryModel>() {
            @Override
            public void onResponse(Call<ApiModel.InventoryModel> call, Response<ApiModel.InventoryModel> response) {
                if (response != null) {
                    if (response.isSuccessful()) {
                        ApiModel.InventoryModel inventoryModel = response.body();
                        int statusCode = inventoryModel.getStatusCode();
                        String message = inventoryModel.getMessage();
                        if (statusCode == 200 && message.equalsIgnoreCase("")) {
                            ArrayList<ApiModel.InventoryPersoanlList> matchList = inventoryModel.getData();
                            if(matchList.size() != 0){
                                arraylist.addAll(matchList);
                                matchingAdapter.notifyDataSetChanged();
                            }
                            pd.dismiss();
                        }
                    } else {
                        String responseString = null;
                        pd.dismiss();
                        try {
                            responseString = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(responseString);
                            int statusCode = jsonObject.optInt("statusCode");
                            String message = jsonObject.optString("message");
                            if(statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token"))
                            {
                                new AllUtils().getTokenRefresh(context);
                            }else{
                                Utils.showToast(context,message);
                            }
                           *//* if(pd.isShowing()) {
                                pd.dismiss();
                            }*//*
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<ApiModel.InventoryModel> call, Throwable t) {
                Toast.makeText(context, "Some Problem Occured", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });*/
        matchingAdapter.notifyDataSetChanged();

    }

    @Override
    public void onTryReconnect() {

    }
}
