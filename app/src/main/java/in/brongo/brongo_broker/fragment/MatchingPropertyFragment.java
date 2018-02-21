package in.brongo.brongo_broker.fragment;

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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.adapter.MatchingAdapter;
import in.brongo.brongo_broker.model.ApiModel;
import in.brongo.brongo_broker.other.AllUtils;
import in.brongo.brongo_broker.other.NoInternetTryConnectListener;
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
public class MatchingPropertyFragment extends Fragment implements NoInternetTryConnectListener {
    private Context context;
    private RecyclerView matching_recycle;
    private MatchingAdapter matchingAdapter;
    private ImageView edit_icon,delete_icon,add_icon;
    private TextView toolbar_title;
    private Toolbar toolbar;
    private RelativeLayout parentLayout;
    private ArrayList<ApiModel.MatchingModel> arraylist = new ArrayList<>();
    private SharedPreferences pref;
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
        parentLayout = getActivity().findViewById(R.id.menu_parent_relative);
        matching_recycle = view.findViewById(R.id.matching_prop_recycle);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        matching_recycle.setLayoutManager(layoutManager);
        matchingAdapter = new MatchingAdapter(context,arraylist,getFragmentManager());
        matching_recycle.setAdapter(matchingAdapter);
        edit_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_edit);
        delete_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_delete);
        add_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_add);
        edit_icon.setVisibility(View.GONE);
        delete_icon.setVisibility(View.GONE);
        add_icon.setVisibility(View.GONE);
        toolbar_title = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.inventory_toolbar_title);
        toolbar = getActivity().findViewById(R.id.inventory_toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar_title.setText("Matching Properties");
        pref =context.getSharedPreferences(AppConstants.PREF_NAME,0);
        fetchList();
       // pd.show();
    }
    private void fetchList() {
        RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
        ApiModel.InventoryPersoanlList inventoryPersoanlList = new ApiModel.InventoryPersoanlList();
        inventoryPersoanlList.setBrokerMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
        inventoryPersoanlList.setPostingType(posting_type.toUpperCase());
        inventoryPersoanlList.setSubPropertyType(subPropertyType);
        inventoryPersoanlList.setPropertyType(propertyType.toUpperCase());
        inventoryPersoanlList.setMicroMarketName(microMarketName);
        String deviceId = Utils.getDeviceId(context);
        String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
        String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
        Call<ApiModel.MatchingResponseModel> call = retrofitAPIs.getMatchingInventory(tokenaccess, "android", deviceId, inventoryPersoanlList);
        call.enqueue(new Callback<ApiModel.MatchingResponseModel>() {
            @Override
            public void onResponse(Call<ApiModel.MatchingResponseModel> call, Response<ApiModel.MatchingResponseModel> response) {
                if (response != null) {
                    if (response.isSuccessful()) {
                        ApiModel.MatchingResponseModel matchingResponseModel = response.body();
                        int statusCode = matchingResponseModel.getStatusCode();
                        String message = matchingResponseModel.getMessage();
                        if (statusCode == 200 && message.equalsIgnoreCase("")) {
                            ArrayList<ApiModel.MatchingModel> matchList = matchingResponseModel.getData();
                            if (matchList.size() != 0) {
                                arraylist.addAll(matchList);
                                matchingAdapter.notifyDataSetChanged();
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
            public void onFailure(Call<ApiModel.MatchingResponseModel> call, Throwable t) {
               Utils.showToast(context,t.getMessage().toString(),"Failure");
            }
        });
        matchingAdapter.notifyDataSetChanged();
    }


    @Override
    public void onTryReconnect() {

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.LoaderUtils.dismissLoader();
    }
}
