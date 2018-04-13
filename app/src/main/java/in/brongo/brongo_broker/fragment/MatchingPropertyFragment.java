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
import in.brongo.brongo_broker.other.NoTokenTryListener;
import in.brongo.brongo_broker.util.AppConstants;
import in.brongo.brongo_broker.util.RecyclerItemClickListener;
import in.brongo.brongo_broker.util.RetrofitAPIs;
import in.brongo.brongo_broker.util.RetrofitBuilders;
import in.brongo.brongo_broker.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.INVENTORY_LIST;
import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.LEGAL;

/**
 * A simple {@link Fragment} subclass.
 */
public class MatchingPropertyFragment extends Fragment implements NoInternetTryConnectListener,NoTokenTryListener,AllUtils.test {
    private Context context;
    private RecyclerView matching_recycle;
    private MatchingAdapter matchingAdapter;
    private ImageView edit_icon, delete_icon, add_icon;
    private TextView toolbar_title;
    private Toolbar toolbar;
    private RelativeLayout parentLayout;
    private ArrayList<ApiModel.MatchingModel> arraylist = new ArrayList<>();
    private SharedPreferences pref;
    private String posting_type, brokerMobile, propertyType, subPropertyType, microMarketName,prop_id,invenType;

    public MatchingPropertyFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            posting_type = getArguments().getString("lead_posting_type","");
            propertyType = getArguments().getString("lead_prop_type","");
            subPropertyType = getArguments().getString("lead_sub_prop_type","");
            prop_id = getArguments().getString("propertyId","");
            microMarketName = getArguments().getString("lead_address","");
            invenType =getArguments().getString("invenType","");
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

    private void initialise(View view) {
        context = getActivity();
        parentLayout = getActivity().findViewById(R.id.menu_parent_relative);
        matching_recycle = view.findViewById(R.id.matching_prop_recycle);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        matching_recycle.setLayoutManager(layoutManager);
        matchingAdapter = new MatchingAdapter(context, arraylist, getFragmentManager());
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
        pref = context.getSharedPreferences(AppConstants.PREF_NAME, 0);
        fetchList();
        matching_recycle.addOnItemTouchListener(new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                InventoryListFragment inventoryListFragment = new InventoryListFragment();
                Utils.replaceFragment(getFragmentManager(),inventoryListFragment,R.id.inventory_frag_container,INVENTORY_LIST);
            }
        }));
        // pd.show();
    }

    private void fetchList() {
        try {
            if(Utils.isNetworkAvailable(context)) {
                Utils.LoaderUtils.showLoader(context);
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            ApiModel.InventoryPersoanlList inventoryPersoanlList = new ApiModel.InventoryPersoanlList();
            inventoryPersoanlList.setBrokerMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
            inventoryPersoanlList.setPostingType(posting_type.toUpperCase());
            inventoryPersoanlList.setSubPropertyType(subPropertyType);
            inventoryPersoanlList.setPropertyType(propertyType.toUpperCase());
            inventoryPersoanlList.setMicroMarketName(microMarketName);
            inventoryPersoanlList.setPropertyId(prop_id);
            String deviceId = Utils.getDeviceId(context);
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
            Call<ApiModel.MatchingResponseModel> call = retrofitAPIs.getMatchingInventory(tokenaccess, "android", deviceId, inventoryPersoanlList);
            call.enqueue(new Callback<ApiModel.MatchingResponseModel>() {
                @Override
                public void onResponse(Call<ApiModel.MatchingResponseModel> call, Response<ApiModel.MatchingResponseModel> response) {
                    Utils.LoaderUtils.dismissLoader();
                    if (response != null) {
                        if (response.isSuccessful()) {
                            ApiModel.MatchingResponseModel matchingResponseModel = response.body();
                            int statusCode = matchingResponseModel.getStatusCode();
                            String message = matchingResponseModel.getMessage();
                            if (statusCode == 200 && message.equalsIgnoreCase("")) {
                                ArrayList<ApiModel.MatchingModel> matchList = matchingResponseModel.getData();
                                arraylist.clear();
                                if (matchList.size() != 0) {
                                   /* for(int i = 0;i<matchList.size();i++){
                                        if((matchList.get(i).getType().equalsIgnoreCase("personal")) && (invenType.equalsIgnoreCase("personal"))){
                                            arraylist.add(matchList.get(i));
                                        }else if((matchList.get(i).getType().equalsIgnoreCase("builder")) && (invenType.equalsIgnoreCase("builder"))){
                                            arraylist.add(matchList.get(i));
                                        }
                                    }*/
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
                                    openTokenDialog(context);
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
                public void onFailure(Call<ApiModel.MatchingResponseModel> call, Throwable t) {
                    Utils.LoaderUtils.dismissLoader();
                    if (t.getMessage().equals("Too many follow-up requests: 21")) {
                        openTokenDialog(context);
                    } else {
                        Utils.showToast(context, t.getLocalizedMessage().toString(), "Failure");
                    }
                }
            });
            matchingAdapter.notifyDataSetChanged();
            }else{
                Utils.internetDialog(context,this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onTryReconnect() {
        fetchList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.LoaderUtils.dismissLoader();
    }

    @Override
    public void onTryRegenerate() {
        try {
            getToken(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openTokenDialog(Context context) {
        try {
            if (!getActivity().isFinishing()) {
                Utils.tokenDialog(context, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getToken(Context context) {
        new AllUtils().getToken(context, this);
    }

    @Override
    public void onSuccessRes(boolean isSuccess) {
        if (isSuccess) {
            fetchList();
        } else {
            Utils.LoaderUtils.dismissLoader();
            openTokenDialog(context);
        }
    }
}
