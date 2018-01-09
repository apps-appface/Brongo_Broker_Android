package appface.brongo.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.applozic.mobicomkit.api.conversation.database.MessageDatabaseService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import appface.brongo.R;
import appface.brongo.adapter.BuilderAdapter;
import appface.brongo.adapter.InventoryPersonalAdapter;
import appface.brongo.model.ApiModel;
import appface.brongo.model.BuilderModel;
import appface.brongo.other.AllUtils;
import appface.brongo.other.NoInternetTryConnectListener;
import appface.brongo.util.AppConstants;
import appface.brongo.util.RecyclerItemClickListener;
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
public class InventoryListFragment extends Fragment implements NoInternetTryConnectListener {
    private RecyclerView inventory_personal_recycle,inventory_builder_recycle;
    private InventoryPersonalAdapter inventoryPersonalAdapter;
    private BuilderAdapter inventory_builderAdapter;
    private ArrayList<ApiModel.InventoryPersoanlList> arraylist;
    private ArrayList<BuilderModel.BuilderObject> builder_list;
    private ImageView inventory_toolbar_edit,inventory_toolbar_delete,inventory_add;
    private TextView toolbar_title,builder_count;
    private Button personal_btn,builder_btn;
    private SharedPreferences pref;
    private int taskcompleted =0;
    private String builderMessage="";
    private Context context;
    public InventoryListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inventory_list, container, false);
        initialise(view);
        personal_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                personal_btn.setBackgroundResource(R.drawable.dialog_button);
                builder_btn.setBackgroundResource(R.drawable.button_change);
                builder_btn.setTextColor(context.getResources().getColor(R.color.appColor));
                personal_btn.setTextColor(context.getResources().getColor(R.color.white));
                builder_count.setTextColor(context.getResources().getColor(R.color.white));
                builder_count.setBackgroundResource(R.drawable.text_circular);
                //changeListValue(0);
                inventory_builder_recycle.setVisibility(View.GONE);
                inventory_personal_recycle.setVisibility(View.VISIBLE);
            }
        });
        builder_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(builderMessage.length() > 0) {
                    Utils.showToast(context, builderMessage);
                }
                personal_btn.setBackgroundResource(R.drawable.button_change);
                builder_btn.setBackgroundResource(R.drawable.dialog_button);
                builder_btn.setTextColor(context.getResources().getColor(R.color.white));
                personal_btn.setTextColor(context.getResources().getColor(R.color.appColor));
                builder_count.setTextColor(context.getResources().getColor(R.color.appColor));
                builder_count.setBackgroundResource(R.drawable.text_circular_white);
                //changeListValue(0);
                inventory_builder_recycle.setVisibility(View.VISIBLE);
                inventory_personal_recycle.setVisibility(View.GONE);
            }
        });
        inventory_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddInventoryFragment addInventoryFragment = new AddInventoryFragment();
                Utils.replaceFragment(getFragmentManager(),addInventoryFragment,R.id.inventory_frag_container,true);
                toolbar_title.setText("Add Inventory");
                inventory_add.setVisibility(View.GONE);
            }
        });
        return view;
    }
    private void initialise(View view){
        context = getActivity();
        inventory_personal_recycle = (RecyclerView)view.findViewById(R.id.inventory_personal_recycle);
        inventory_builder_recycle = (RecyclerView)view.findViewById(R.id.inventory_builder_recycle);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(context);
        inventory_personal_recycle.setLayoutManager(layoutManager);
        inventory_builder_recycle.setLayoutManager(layoutManager1);
        builder_count = (TextView)view.findViewById(R.id.builder_count);
        inventory_add = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_add);
        inventory_add.setVisibility(View.VISIBLE);
        personal_btn = (Button)view.findViewById(R.id.inventory_personal_btn);
        builder_btn = (Button)view.findViewById(R.id.inventory_builder_btn);
        arraylist = new ArrayList<>();
        builder_list = new ArrayList<>();
        pref = context.getSharedPreferences(AppConstants.PREF_NAME,0);
        inventoryPersonalAdapter = new InventoryPersonalAdapter(context,arraylist,getFragmentManager());
        inventory_builderAdapter = new BuilderAdapter(context,builder_list,getFragmentManager());
        inventory_personal_recycle.setAdapter(inventoryPersonalAdapter);
        inventory_builder_recycle.setAdapter(inventory_builderAdapter);
        toolbar_title = (TextView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.inventory_toolbar_title);
        inventory_toolbar_delete = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_delete);
        inventory_toolbar_edit = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_edit);
        inventory_toolbar_delete.setVisibility(View.GONE);
        inventory_toolbar_edit.setVisibility(View.GONE);
        toolbar_title.setText("Inventory List");
       /* pd = new ProgressDialog(context, R.style.CustomProgressDialog);
        pd.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress_loader));
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);*/
        fetchList();
    }
    private void fetchList(){
        if(Utils.isNetworkAvailable(context)) {
            Utils.LoaderUtils.showLoader(context);
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
            Call<ApiModel.InventoryModel> call = retrofitAPIs.getInventoryApi(tokenaccess, "android", deviceId, mobileNo);
            call.enqueue(new Callback<ApiModel.InventoryModel>() {
                @Override
                public void onResponse(Call<ApiModel.InventoryModel> call, Response<ApiModel.InventoryModel> response) {
                    if (response != null) {
                        if (response.isSuccessful()) {
                            ApiModel.InventoryModel inventoryModel = response.body();
                            int statusCode = inventoryModel.getStatusCode();
                            String message = inventoryModel.getMessage();
                            if (statusCode == 200 && message.equalsIgnoreCase("")) {
                                ArrayList<ApiModel.InventoryPersoanlList> inventoryPersoanlLists = inventoryModel.getData();
                                if (inventoryPersoanlLists.size() != 0) {
                                    arraylist.addAll(inventoryPersoanlLists);
                                    inventoryPersonalAdapter.notifyDataSetChanged();
                                }
                                fetchBuilderList();
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
                                    fetchList();
                                } else {
                                    Utils.LoaderUtils.dismissLoader();
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
                public void onFailure(Call<ApiModel.InventoryModel> call, Throwable t) {
                    Toast.makeText(context, "Some Problem Occured", Toast.LENGTH_SHORT).show();
                    Utils.LoaderUtils.dismissLoader();
                }
            });
        }else{
            taskcompleted = 100;
            Utils.internetDialog(context,this);
        }
    }

    private void fetchBuilderList(){
        if(Utils.isNetworkAvailable(context)) {
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
            Call<BuilderModel.BuilderDetailsModel> call = retrofitAPIs.builderInventoryApi(tokenaccess, "android", deviceId, mobileNo);
            call.enqueue(new Callback<BuilderModel.BuilderDetailsModel>() {
                @Override
                public void onResponse(Call<BuilderModel.BuilderDetailsModel> call, Response<BuilderModel.BuilderDetailsModel> response) {
                    Utils.LoaderUtils.dismissLoader();
                    if (response != null) {
                        if (response.isSuccessful()) {
                            BuilderModel.BuilderDetailsModel builderModel = response.body();
                            int statusCode = builderModel.getStatusCode();
                            String message = builderModel.getMessage();
                            if (statusCode == 200 && message.equalsIgnoreCase("")) {
                                ArrayList<BuilderModel.BuilderObject> builderList = builderModel.getData();
                                if (builderList.size() != 0) {
                                    builder_list.clear();
                                    builder_list.addAll(builderList);
                                    inventory_builderAdapter.notifyDataSetChanged();
                                    builder_count.setText(builderList.size()+"");
                                }
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
                                builderMessage = message;
                                if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                    new AllUtils().getTokenRefresh(context);
                                    fetchBuilderList();
                                } else {
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
                public void onFailure(Call<BuilderModel.BuilderDetailsModel> call, Throwable t) {
                    Toast.makeText(context, "Some Problem Occured", Toast.LENGTH_SHORT).show();
                    Utils.LoaderUtils.dismissLoader();
                }
            });
        }else{
            taskcompleted = 200;
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
        switch (taskcompleted){
            case 100:
                fetchList();
                break;
            case 200:
                Utils.LoaderUtils.showLoader(context);
                fetchBuilderList();
                break;
        }
    }
}
