package appface.brongo.fragment;


import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import appface.brongo.R;
import appface.brongo.adapter.HistoricalAdapter;
import appface.brongo.model.ApiModel;
import appface.brongo.other.AllUtils;
import appface.brongo.other.NoInternetTryConnectListener;
import appface.brongo.other.NoTokenTryListener;
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
public class HistoricalDealFragment extends Fragment implements NoInternetTryConnectListener,NoTokenTryListener,AllUtils.test {
    private Context context;
   private Button his_open_btn,his_close_btn;
   private RecyclerView his_recycle1,his_recycle2;
    private ImageView edit_icon,delete_icon,add_icon;
    private TextView toolbar_title;
    private Toolbar toolbar;
    private RelativeLayout parentLayout;
    private ArrayList<ApiModel.BuyAndRentModel> openList,closeList;
   private HistoricalAdapter adapter1,adapter2;
    private SharedPreferences pref;
    public HistoricalDealFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_historical_deal, container, false);
        initialise(view);
        his_open_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                his_recycle2.setVisibility(View.GONE);
                his_recycle1.setVisibility(View.VISIBLE);
                his_open_btn.setBackgroundResource(R.drawable.dialog_button);
                his_close_btn.setBackgroundResource(R.drawable.button_change);
                his_open_btn.setTextColor(context.getResources().getColor(R.color.white));
                his_close_btn.setTextColor(context.getResources().getColor(R.color.appColor));
            }
        });
        his_close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                his_recycle2.setVisibility(View.VISIBLE);
                his_recycle1.setVisibility(View.GONE);
                his_open_btn.setBackgroundResource(R.drawable.button_change);
                his_close_btn.setBackgroundResource(R.drawable.dialog_button);
                his_open_btn.setTextColor(context.getResources().getColor(R.color.appColor));
                his_close_btn.setTextColor(context.getResources().getColor(R.color.white));
            }
        });
        return view;
    }
    private void initialise(View view){
        context = getActivity();
        parentLayout = (RelativeLayout)getActivity().findViewById(R.id.menu_parent_relative);
        pref = context.getSharedPreferences(AppConstants.PREF_NAME,0);
        his_recycle1 = (RecyclerView)view.findViewById(R.id.his_recycle_open_deal);
        his_recycle2 = (RecyclerView)view.findViewById(R.id.his_recycle_close_deal);
        LinearLayoutManager verticalmanager = new LinearLayoutManager(context, 0, false);
        verticalmanager.setOrientation(LinearLayoutManager.VERTICAL);
        his_recycle1.setLayoutManager(verticalmanager);
        LinearLayoutManager verticalmanager1 = new LinearLayoutManager(context, 0, false);
        verticalmanager1.setOrientation(LinearLayoutManager.VERTICAL);
        his_recycle2.setLayoutManager(verticalmanager1);
        his_open_btn = (Button)view.findViewById(R.id.historical_open_btn);
        his_close_btn = (Button)view.findViewById(R.id.historical_closed_btn);
        openList = new ArrayList<>();
        closeList = new ArrayList<>();
        toolbar_title = (TextView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.inventory_toolbar_title);
        toolbar = (Toolbar)getActivity().findViewById(R.id.inventory_toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar_title.setText("Historical Deals");
        edit_icon = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_edit);
        delete_icon =(ImageView) getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_delete);
        add_icon = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_add);
        edit_icon.setVisibility(View.GONE);
        delete_icon.setVisibility(View.GONE);
        add_icon.setVisibility(View.GONE);
        adapter1 = new HistoricalAdapter(openList,context);
        adapter2 = new HistoricalAdapter(closeList,context);
        his_recycle1.setAdapter(adapter1);
        his_recycle2.setAdapter(adapter2);
        populateList();
    }
    private void populateList(){
        if(Utils.isNetworkAvailable(context)) {
            Utils.LoaderUtils.showLoader(context);
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
            Call<ApiModel.OpenDealModels> call = retrofitAPIs.getActiveDeals(tokenaccess, "android", deviceId, mobileNo,false);
            call.enqueue(new Callback<ApiModel.OpenDealModels>() {
                @Override
                public void onResponse(Call<ApiModel.OpenDealModels> call, Response<ApiModel.OpenDealModels> response) {
                    Utils.LoaderUtils.dismissLoader();
                    if (response != null) {
                        if (response.isSuccessful()) {
                            ApiModel.OpenDealModels openDealModels = response.body();
                            int statusCode = openDealModels.getStatusCode();
                            String message = openDealModels.getMessage();
                            if (statusCode == 200 && message.equalsIgnoreCase("")) {
                                ArrayList<ApiModel.BuyAndRentModel> openlist1 = openDealModels.getData().get(0).getOpenDeals();
                                ArrayList<ApiModel.BuyAndRentModel> closelist1 = openDealModels.getData().get(0).getClosedDeals();
                                if (openlist1.size() != 0) {
                                    openList.clear();
                                    openList.addAll(openlist1);
                                }
                                if (closelist1.size() != 0) {
                                    closeList.clear();
                                   closeList.addAll(closelist1);
                                }
                                adapter1.notifyDataSetChanged();
                                adapter2.notifyDataSetChanged();
                            }
                        } else {
                            String responseString = null;
                            try {
                                responseString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                int statusCode = jsonObject.optInt("statusCode");
                                String message = jsonObject.optString("message");
                                if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                   getToken(context);
                                } else {
                                    Utils.setSnackBar(parentLayout, message);
                                }
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiModel.OpenDealModels> call, Throwable t) {
                    Utils.LoaderUtils.dismissLoader();
                    Utils.showToast(context, t.getMessage().toString(),"Failure");
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
        populateList();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.LoaderUtils.dismissLoader();
    }

    @Override
    public void onTryRegenerate() {
        populateList();
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
         populateList();
        }else{
            Utils.LoaderUtils.dismissLoader();
            openTokenDialog(context);
        }
    }
}
