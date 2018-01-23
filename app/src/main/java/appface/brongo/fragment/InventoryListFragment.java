package appface.brongo.fragment;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applozic.mobicomkit.api.conversation.database.MessageDatabaseService;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import appface.brongo.R;
import appface.brongo.activity.BuilderProjectActivity;
import appface.brongo.adapter.BuilderAdapter;
import appface.brongo.adapter.InventoryPersonalAdapter;
import appface.brongo.model.ApiModel;
import appface.brongo.model.BuilderModel;
import appface.brongo.model.ClientDetailsModel;
import appface.brongo.other.AllUtils;
import appface.brongo.other.NoInternetTryConnectListener;
import appface.brongo.util.AppConstants;
import appface.brongo.util.RecyclerItemClickListener;
import appface.brongo.util.RefreshTokenCall;
import appface.brongo.util.RetrofitAPIs;
import appface.brongo.util.RetrofitBuilders;
import appface.brongo.util.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static appface.brongo.util.AppConstants.FRAGMENT_TAGS.ADD_INVENTORY;

/**
 * A simple {@link Fragment} subclass.
 */
public class InventoryListFragment extends Fragment implements NoInternetTryConnectListener,BuilderAdapter.OnClick{
    private RecyclerView inventory_personal_recycle,inventory_builder_recycle;
    private InventoryPersonalAdapter inventoryPersonalAdapter;
    private Toolbar toolbar;
    private ArrayList<ClientDetailsModel.ConnectedClientObject> clientDetails_list;
    private BuilderAdapter inventory_builderAdapter;
    private ArrayList<String> client_list;
    private ArrayList<ApiModel.InventoryPersoanlList> arraylist;
    private ArrayList<BuilderModel.BuilderObject> builder_list;
    private ImageView inventory_toolbar_edit,inventory_toolbar_delete,inventory_add;
    private TextView toolbar_title,builder_count;
    private Button personal_btn,builder_btn;
    private String client,mobile,email;
    private SharedPreferences pref;
    private int taskcompleted =0;
    private int count = 0;
    boolean isVisible;
    ArrayAdapter<String> clientAdapter;
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
                inventory_add.setVisibility(View.VISIBLE);
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
                inventory_add.setVisibility(View.GONE);
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
                Utils.replaceFragment(getFragmentManager(),addInventoryFragment,R.id.inventory_frag_container,ADD_INVENTORY);
            }
        });
        return view;
    }
    private void initialise(View view){
        context = getActivity();
        client = mobile = email ="";
        client_list = new ArrayList<>();
        clientDetails_list = new ArrayList<>();
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
        inventory_builderAdapter = new BuilderAdapter(context,builder_list,getFragmentManager(),this);
        inventory_personal_recycle.setAdapter(inventoryPersonalAdapter);
        inventory_builder_recycle.setAdapter(inventory_builderAdapter);
        clientAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, client_list);
        toolbar = (Toolbar)getActivity().findViewById(R.id.inventory_toolbar);
        toolbar.setVisibility(View.VISIBLE);
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
        fetchConnectedClient();
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
                    fetchBuilderList();
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
                            if (statusCode == 200) {
                                ArrayList<BuilderModel.BuilderObject> builderList = builderModel.getData();
                                if (builderList.size() != 0) {
                                    builder_list.clear();
                                    builder_list.addAll(builderList);
                                    inventory_builderAdapter.notifyDataSetChanged();
                                    count= Integer.parseInt(message);
                                    if (count>0){
                                        builder_count.setText(count+"");
                                        builder_count.setVisibility(View.VISIBLE);
                                    }else{
                                        builder_count.setVisibility(View.GONE);
                                        builder_btn.setBackgroundResource(R.drawable.button_change);
                                        builder_btn.setTextColor(context.getResources().getColor(R.color.appColor));
                                    }
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
    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.LoaderUtils.dismissLoader();
    }

    private void tc_dialog(final int position, final BuilderModel.BuilderObject builderObject, boolean isAcceptVisible){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_tc);
        //dialog.setCanceledOnTouchOutside(false);
        // dialog.setCancelable(false);
        final ImageView cross_btn = (ImageView) dialog.findViewById(R.id.tc_close_btn);
        final Button accept_btn = (Button)dialog.findViewById(R.id.tcDialog_accept);
        TextView commission_text = (TextView)dialog.findViewById(R.id.tcDialog_commission);
        commission_text.setText(builderObject.getCommission()+"% Commission");
        cross_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if(isAcceptVisible){
            accept_btn.setVisibility(View.VISIBLE);
        }else{
            accept_btn.setVisibility(View.GONE);
        }
        accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accept_builder(position,builderObject);
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.show();
    }
    private void builderRejectApi(final int position, final BuilderModel.BuilderObject builderObject){
        if (Utils.isNetworkAvailable(context)) {
            Utils.LoaderUtils.showLoader(context);
            ApiModel.ClientAcceptModel clientAcceptModel = new ApiModel.ClientAcceptModel();
            clientAcceptModel.setClientMobileNo(builderObject.getUserId());
            clientAcceptModel.setBrokerMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
            clientAcceptModel.setPostingType("");
            clientAcceptModel.setPropertyId(builderObject.getPropertyId());
            clientAcceptModel.setReason("");
            clientAcceptModel.setPostedUser("builder");
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            Call<ResponseBody> call = retrofitAPIs.rejectLeadApi(tokenaccess, "android", deviceId, clientAcceptModel);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response != null) {
                        Utils.LoaderUtils.dismissLoader();
                        String responseString = null;
                        if (response.isSuccessful()) {
                            try {
                                responseString = response.body().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                String message = jsonObject.optString("message");
                                int statusCode = jsonObject.optInt("statusCode");
                                if (statusCode == 200 && message.equalsIgnoreCase("Thanks For Your Valuable Feedback")) {
                                    builder_list.remove(position);
                                    if(count>0){
                                        count = count-1;
                                    }
                                    if (count>0){
                                        builder_count.setVisibility(View.VISIBLE);
                                        builder_count.setText(count+"");
                                    }else{
                                        builder_count.setVisibility(View.GONE);
                                    }
                                    inventory_builderAdapter.notifyDataSetChanged();
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
                                    builderRejectApi(position,builderObject);
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
                    Utils.LoaderUtils.dismissLoader();
                    Utils.showToast(context, "Some Problem Occured");
                }
            });
        }else{
            Utils.internetDialog(context,this);
        }
    }
    private void accept_builder(final int position, final BuilderModel.BuilderObject builderObject){
        if(Utils.isNetworkAvailable(context)) {
            Utils.LoaderUtils.showLoader(context);
            ApiModel.BuilderAcceptModel builderAcceptModel = new ApiModel.BuilderAcceptModel();
            builderAcceptModel.setBrokerMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
            builderAcceptModel.setPropertyId(builderObject.getPropertyId());
            builderAcceptModel.setUserId(builderObject.getUserId());
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            Call<ApiModel.ResponseModel> call = retrofitAPIs.acceptBuilderApi(tokenaccess, "android", deviceId, builderAcceptModel);
            call.enqueue(new Callback<ApiModel.ResponseModel>() {
                @Override
                public void onResponse(Call<ApiModel.ResponseModel> call, Response<ApiModel.ResponseModel> response) {
                    Utils.LoaderUtils.dismissLoader();
                    if (response != null) {
                        String responseString = null;
                        if (response.isSuccessful()) {
                            ApiModel.ResponseModel responseModel = response.body();
                            int statusCode = responseModel.getStatusCode();
                            String message = responseModel.getMessage();
                            if (statusCode == 200 && message.equalsIgnoreCase("Builder And Broker Connection Is Established")) {
                                fetchBuilderList();
                                Utils.showToast(context, message);
                            }
                        } else {
                            try {
                                responseString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                String message = jsonObject.optString("message");
                                int statusCode = jsonObject.optInt("statusCode");
                                if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                    new AllUtils().getTokenRefresh(context);
                                    accept_builder(position,builderObject);
                                }else{
                                    Utils.showToast(context, message);
                                }
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiModel.ResponseModel> call, Throwable t) {
                    Utils.LoaderUtils.dismissLoader();
                    Utils.showToast(context, t.getMessage().toString());
                }
            });
        }else{
            Utils.internetDialog(context,this);
        }
    }
    private void builder_registerDialog(final BuilderModel.BuilderObject builderObject){
        isVisible = false;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.5f);
        dialog.setContentView(R.layout.register_client_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        //dialog.setCanceledOnTouchOutside(false);
        // dialog.setCancelable(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final TextView clientAdd = (TextView)dialog.findViewById(R.id.client_register_add);
        final LinearLayout manual_register = (LinearLayout)dialog.findViewById(R.id.manual_register_linear);
        final LinearLayout client_register_linear = (LinearLayout)dialog.findViewById(R.id.client_register_linear);
        final EditText client_name = (EditText)dialog.findViewById(R.id.client_name_register);
        final EditText client_email = (EditText)dialog.findViewById(R.id.client_email_register);
        final EditText client_mobile = (EditText)dialog.findViewById(R.id.client_mobile_register);
        MaterialBetterSpinner client_spinner = (MaterialBetterSpinner) dialog.findViewById(R.id.inventory_spinner_client1);
        Button register_btn = (Button)dialog.findViewById(R.id.client_register_register);
        Button register_cancel_btn = (Button)dialog.findViewById(R.id.client_register_cancel);
        client_spinner.setAdapter(clientAdapter);
        client_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mobile = clientDetails_list.get(position).getMobileNo();
                client = clientDetails_list.get(position).getFirstName();
                email = clientDetails_list.get(position).getEmailId();
            }
        });

        clientAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isVisible){
                    isVisible=true;
                    mobile = client = email="";
                    client_register_linear.setVisibility(View.GONE);
                    manual_register.setVisibility(View.VISIBLE);
                    clientAdd.setVisibility(View.GONE);
                }else{
                    isVisible = false;
                }
            }
        });
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mobile.equalsIgnoreCase("")){
                    if(mobile.length() == 10 && mobile.startsWith("6") || mobile.startsWith("7") || mobile.startsWith("8") || mobile.startsWith("9")){
                        callRegisterApi(builderObject,mobile,client,email);
                    }else{
                        Utils.showToast(context,"Invalid mobile Number");
                    }
                }else {
                        Utils.showToast(context, "data should not be empty");
                }
                dialog.dismiss();
            }
        });
        register_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobile = client = email="";
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void callRegisterApi(final BuilderModel.BuilderObject builderObject, final String mobile_no, final String name, final String email) {
        if (Utils.isNetworkAvailable(context)) {
            Utils.LoaderUtils.showLoader(context);
            ApiModel.BuilderAcceptModel builderRegisterModel = new ApiModel.BuilderAcceptModel();
            builderRegisterModel.setBrokerMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
            builderRegisterModel.setPropertyId(builderObject.getPropertyId());
            builderRegisterModel.setEmailId(email);
            builderRegisterModel.setMobileNo(mobile_no);
            builderRegisterModel.setName(name);
            builderRegisterModel.setBuilderId(builderObject.getUserId());
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            Call<ApiModel.ResponseModel> call = retrofitAPIs.registertBuilderApi(tokenaccess, "android", deviceId, builderRegisterModel);
            call.enqueue(new Callback<ApiModel.ResponseModel>() {
                @Override
                public void onResponse(Call<ApiModel.ResponseModel> call, Response<ApiModel.ResponseModel> response) {
                    Utils.LoaderUtils.dismissLoader();
                    if (response != null) {
                        String responseString = null;
                        if (response.isSuccessful()) {
                            ApiModel.ResponseModel responseModel = response.body();
                            int statusCode = responseModel.getStatusCode();
                            String message = responseModel.getMessage();
                            if (statusCode == 200 && message.equalsIgnoreCase("Client Has Registred Successfully")) {
                                Utils.showToast(context, message);
                            }
                        } else {
                            try {
                                responseString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                String message = jsonObject.optString("message");
                                int statusCode = jsonObject.optInt("statusCode");
                                if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                    new AllUtils().getTokenRefresh(context);
                                    callRegisterApi(builderObject, mobile_no, name, email);
                                }
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiModel.ResponseModel> call, Throwable t) {
                    Utils.LoaderUtils.dismissLoader();
                    Utils.showToast(context, t.getMessage().toString());
                }
            });
        }else{
            Utils.internetDialog(context,this);
        }
    }

    @Override
    public void acceptTc(int position,BuilderModel.BuilderObject builderObject, boolean accept) {
        tc_dialog(position,builderObject,accept);
    }

    @Override
    public void proceedToWeb(int position,BuilderModel.BuilderObject builderObject) {
        Intent intent = new Intent(context, BuilderProjectActivity.class);
        intent.putExtra("url",builderObject.getUrl());
        intent.putExtra("title",builderObject.getProjectName());
        intent.putExtra("user_id",builderObject.getUserId());
        intent.putExtra("prop_id",builderObject.getPropertyId());
        if(builderObject.getUrl() != null && !(builderObject.getUrl()).isEmpty()){
            startActivity(intent);
        }
    }

    @Override
    public void registerClient(int position,BuilderModel.BuilderObject builderObject) {
        if(builderObject.getUrl() != null && !(builderObject.getUrl()).isEmpty()){
            builder_registerDialog(builderObject);
        }
    }

    @Override
    public void rejectProject(int position,BuilderModel.BuilderObject builderObject) {
        builderRejectApi(position,builderObject);
    }
    private void fetchConnectedClient(){
        if(Utils.isNetworkAvailable(context)) {
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
            Call<ClientDetailsModel.ConnectedClientModel> call = retrofitAPIs.connectedClientApi(tokenaccess, "android", deviceId, mobileNo);
            call.enqueue(new Callback<ClientDetailsModel.ConnectedClientModel>() {
                @Override
                public void onResponse(Call<ClientDetailsModel.ConnectedClientModel> call, Response<ClientDetailsModel.ConnectedClientModel> response) {
                    Utils.LoaderUtils.dismissLoader();
                    if (response != null) {
                        if (response.isSuccessful()) {
                            ClientDetailsModel.ConnectedClientModel builderModel = response.body();
                            int statusCode = builderModel.getStatusCode();
                            String message = builderModel.getMessage();
                            if (statusCode == 200) {
                                ArrayList<ClientDetailsModel.ConnectedClientObject> list = builderModel.getData();
                                if (list.size() != 0) {
                                    clientDetails_list.clear();
                                    clientDetails_list.addAll(list);
                                    for(int i = 0;i<clientDetails_list.size();i++){
                                        client_list.add(clientDetails_list.get(i).getFirstName());
                                    }
                                    clientAdapter.notifyDataSetChanged();

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
                                    fetchConnectedClient();
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
                public void onFailure(Call<ClientDetailsModel.ConnectedClientModel> call, Throwable t) {
                    Toast.makeText(context, "Some Problem Occured", Toast.LENGTH_SHORT).show();
                    Utils.LoaderUtils.dismissLoader();
                }
            });
        }else{
            Utils.internetDialog(context,this);
        }

    }

}
