package in.brongo.brongo_broker.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.activity.BuilderProjectActivity;
import in.brongo.brongo_broker.adapter.BuilderAdapter;
import in.brongo.brongo_broker.adapter.InventoryPersonalAdapter;
import in.brongo.brongo_broker.model.ApiModel;
import in.brongo.brongo_broker.model.BuilderModel;
import in.brongo.brongo_broker.model.ClientDetailsModel;
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

import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.ADD_INVENTORY;

/**
 * A simple {@link Fragment} subclass.
 */
public class InventoryListFragment extends Fragment implements NoInternetTryConnectListener,BuilderAdapter.OnClick,InventoryPersonalAdapter.DeleteInventoryListener,NoTokenTryListener,AllUtils.test{
    private RecyclerView inventory_personal_recycle,inventory_builder_recycle;
    private InventoryPersonalAdapter inventoryPersonalAdapter;
    private Toolbar toolbar;
    int apicode = 0;
    private RelativeLayout parentLayout;
    private ArrayList<ClientDetailsModel.ConnectedClientObject> clientDetails_list;
    private BuilderAdapter inventory_builderAdapter;
    private ArrayList<String> client_list;
    private ArrayList<ApiModel.InventoryPersoanlList> arraylist;
    private ArrayList<BuilderModel.BuilderObject> builder_list;
    private ImageView inventory_toolbar_edit,inventory_toolbar_delete,inventory_add;
    private TextView toolbar_title,builder_count;
    private Button personal_btn,builder_btn;
    private String client,mobile,email,emailPattern;
    private SharedPreferences pref;
    private int taskcompleted =0;
    private int count = 0;
    boolean isVisible;
    private boolean isMobileVisible,isEmailVisible;
    ArrayAdapter<String> clientAdapter;
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
        try {
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
                    if(builder_list.size() == 0) {
                        fetchBuilderList();
                    }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
    private void initialise(View view){
        try {
            context = getActivity();
            client = mobile = email = emailPattern ="";
            emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            parentLayout = getActivity().findViewById(R.id.menu_parent_relative);
            client_list = new ArrayList<>();
            clientDetails_list = new ArrayList<>();
            inventory_personal_recycle = view.findViewById(R.id.inventory_personal_recycle);
            inventory_builder_recycle = view.findViewById(R.id.inventory_builder_recycle);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(context);
            inventory_personal_recycle.setLayoutManager(layoutManager);
            inventory_builder_recycle.setLayoutManager(layoutManager1);
            builder_count = view.findViewById(R.id.builder_count);
            inventory_add = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_add);
            inventory_add.setVisibility(View.VISIBLE);
            personal_btn = view.findViewById(R.id.inventory_personal_btn);
            builder_btn = view.findViewById(R.id.inventory_builder_btn);
            arraylist = new ArrayList<>();
            builder_list = new ArrayList<>();
            pref = context.getSharedPreferences(AppConstants.PREF_NAME,0);
            inventoryPersonalAdapter = new InventoryPersonalAdapter(context,arraylist,getFragmentManager(),this);
            inventory_builderAdapter = new BuilderAdapter(context,builder_list,getFragmentManager(),this);
            inventory_personal_recycle.setAdapter(inventoryPersonalAdapter);
            inventory_builder_recycle.setAdapter(inventory_builderAdapter);
            clientAdapter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_dropdown_item_1line, client_list);
            toolbar = getActivity().findViewById(R.id.inventory_toolbar);
            toolbar.setVisibility(View.VISIBLE);
            toolbar_title = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.inventory_toolbar_title);
            inventory_toolbar_delete = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_delete);
            inventory_toolbar_edit = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_edit);
            inventory_toolbar_delete.setVisibility(View.GONE);
            inventory_toolbar_edit.setVisibility(View.GONE);
            toolbar_title.setText("Inventory List");
            int count = pref.getInt(AppConstants.BUILDER_INVENTORY,0);
            if (count>0){
                builder_count.setText(count+"");
                builder_count.setVisibility(View.VISIBLE);
            }
            fetchList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void fetchList(){
        try {
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
                        Utils.LoaderUtils.dismissLoader();
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
                                }
                            } else {
                                String responseString = null;
                                try {
                                    responseString = response.errorBody().string();
                                    JSONObject jsonObject = new JSONObject(responseString);
                                    int statusCode = jsonObject.optInt("statusCode");
                                    String message = jsonObject.optString("message");
                                    if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                        apicode = 100;
                                        openTokenDialog(context);
                                    }else {
                                        Utils.setSnackBar(parentLayout, message);
                                    }
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<ApiModel.InventoryModel> call, Throwable t) {
                        Utils.LoaderUtils.dismissLoader();
                        if (t.getMessage().equals("Too many follow-up requests: 21")) {
                            apicode = 100;
                            openTokenDialog(context);
                        }else {
                            Utils.showToast(context, t.getLocalizedMessage().toString(), "Failure");
                        }

                    }
                });
            }else{
                taskcompleted = 100;
                Utils.internetDialog(context,this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchBuilderList(){
        try {
            if(Utils.isNetworkAvailable(context)) {
                if(!getActivity().isFinishing()){
                    Utils.LoaderUtils.showLoader(context);
                }
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
                                       /* count= Integer.parseInt(message);
                                        if (count>0){
                                            builder_count.setText(count+"");
                                            builder_count.setVisibility(View.VISIBLE);
                                        }else{
                                            builder_count.setVisibility(View.GONE);
                                            builder_btn.setBackgroundResource(R.drawable.button_change);
                                            builder_btn.setTextColor(context.getResources().getColor(R.color.appColor));
                                        }*/
                                    }
                                }
                                fetchConnectedClient();
                            } else {
                                String responseString = null;
                                try {
                                    responseString = response.errorBody().string();
                                    JSONObject jsonObject = new JSONObject(responseString);
                                    int statusCode = jsonObject.optInt("statusCode");
                                    String message = jsonObject.optString("message");
                                    if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                        apicode = 200;
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
                    public void onFailure(Call<BuilderModel.BuilderDetailsModel> call, Throwable t) {
                        Utils.LoaderUtils.dismissLoader();
                        if (t.getMessage().equals("Too many follow-up requests: 21")) {
                            apicode = 200;
                            openTokenDialog(context);
                        }else {
                            Utils.showToast(context, t.getLocalizedMessage().toString(), "Failure");
                        }
                    }
                });
            }else{
                taskcompleted = 200;
                Utils.internetDialog(context,this);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.dialog_tc);
            final ImageView cross_btn = dialog.findViewById(R.id.tc_close_btn);
            final Button accept_btn = dialog.findViewById(R.id.tcDialog_accept);
            TextView tc_text = dialog.findViewById(R.id.bid_accepted_text);
            if(builderObject.getChannelPAgree() != null) {
                tc_text.setText(builderObject.getChannelPAgree().toString());
            }
            TextView commission_text = dialog.findViewById(R.id.tcDialog_commission);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void builderRejectApi(final int position, final BuilderModel.BuilderObject builderObject){
        try {
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
                                       Utils.setSnackBar(parentLayout,"Please try again");
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
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Utils.LoaderUtils.dismissLoader();
                        if (t.getMessage().equals("Too many follow-up requests: 21")) {
                            new AllUtils().getTokenRefresh(context);
                            Utils.setSnackBar(parentLayout,"Please try again");
                        }else {
                            Utils.showToast(context, t.getLocalizedMessage().toString(), "Failure");
                        }
                    }
                });
            }else{
                Utils.internetDialog(context,this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void accept_builder(final int position, final BuilderModel.BuilderObject builderObject){
        try {
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
                                if (statusCode == 200) {
                                    fetchBuilderList();
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
                                        Utils.setSnackBar(parentLayout, message);
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
                        if (t.getMessage().equals("Too many follow-up requests: 21")) {
                            new AllUtils().getTokenRefresh(context);
                            Utils.setSnackBar(parentLayout,"Please try again");
                        }else {
                            Utils.showToast(context, t.getLocalizedMessage().toString(), "Failure");
                        }
                    }
                });
            }else{
                Utils.internetDialog(context,this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void builder_registerDialog(final BuilderModel.BuilderObject builderObject){
      isMobileVisible=true;
      isEmailVisible = true;
        mobile = client = email="";
        try {
            isVisible = false;
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            //dialog.getWindow().setDimAmount(0.5f);
            dialog.setContentView(R.layout.register_client_dialog);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            final TextView clientAdd = dialog.findViewById(R.id.client_register_add);
            final LinearLayout manual_register = dialog.findViewById(R.id.manual_register_linear);
            final LinearLayout client_register_linear = dialog.findViewById(R.id.client_register_linear);
            final EditText client_name = dialog.findViewById(R.id.client_name_register);
            final EditText client_email = dialog.findViewById(R.id.client_email_register);
            final TextInputLayout email_layout  = dialog.findViewById(R.id.input_layout_client_email);
            final TextInputLayout mobile_layout  = dialog.findViewById(R.id.input_layout_client_mobile);
            final EditText client_mobile = dialog.findViewById(R.id.client_mobile_register);
            final CheckBox email_checkbox = dialog.findViewById(R.id.email_register_check);
            final CheckBox mobile_checkbox = dialog.findViewById(R.id.mobile_register_check);
            MaterialBetterSpinner client_spinner = dialog.findViewById(R.id.inventory_spinner_client1);
            Button register_btn = dialog.findViewById(R.id.client_register_register);
            Button register_cancel_btn = dialog.findViewById(R.id.client_register_cancel);
            client_spinner.setAdapter(clientAdapter);
            client_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //mobile = clientDetails_list.get(position).getMobileNo();
                    client = clientDetails_list.get(position).getFirstName();
                    //email = clientDetails_list.get(position).getEmailId();
                    client_name.setText(client);
                    client_register_linear.setVisibility(View.GONE);
                    manual_register.setVisibility(View.VISIBLE);
                    clientAdd.setVisibility(View.GONE);

                }
            });
            client_email.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String email1 = client_email.getText().toString().trim();
                    if (email1.matches(emailPattern) && s.length() > 0){
                        email_layout.setError("");
                        email_layout.setErrorEnabled(false);
                        email = email1;
                    }else if(s.length()>0){
                        email_layout.setErrorEnabled(true);
                        email_layout.setError("Invalid email id");
                        email = "";
                    }else if(s.length() == 0){
                        email_layout.setError("");
                        email_layout.setErrorEnabled(false);
                        email = "";
                    }
                }
            });
            client_mobile.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 10) {
                        Utils.hideKeyboard(context, client_mobile);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String phone = client_mobile.getText().toString().trim();
                    if ((phone.startsWith("6") || phone.startsWith("7") || phone.startsWith("8") || phone.startsWith("9")) && (phone.length() == 10)){
                        mobile_layout.setError("");
                        mobile_layout.setErrorEnabled(false);
                        mobile = phone;
                    }else if(phone.length() == 0) {
                        mobile = "";
                        mobile_layout.setError("");
                        mobile_layout.setErrorEnabled(false);
                    }else
                    {
                        mobile = "";
                        mobile_layout.setError("Invalid Mobile number");
                        mobile_layout.setErrorEnabled(true);
                    }
                }
            });
            email_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        isEmailVisible = true;
                    }else{
                        isEmailVisible = false;
                        if(!isMobileVisible){
                            mobile_checkbox.setChecked(true);
                        }
                    }
                }
            });
            mobile_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        isMobileVisible = true;
                    }else{
                        isMobileVisible = false;
                        if(!isEmailVisible){
                            email_checkbox.setChecked(true);
                        }
                    }
                }
            });
            clientAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        client_register_linear.setVisibility(View.GONE);
                        manual_register.setVisibility(View.VISIBLE);
                        clientAdd.setVisibility(View.GONE);
                        client_email.setText("");
                        client_mobile.setText("");
                        client_name.setText("");
                }
            });
            register_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    client = client_name.getText().toString();
                        if ((!mobile.equalsIgnoreCase("") || (email.length() > 0))&& (client.length() > 0)) {
                            if(mobile.length()>0 && email.length()>0) {
                                if(!(!isEmailVisible && ! isMobileVisible)){
                                    callRegisterApi(builderObject);
                                    dialog.dismiss();

                                }else{
                                    Utils.setSnackBar(parentLayout, "Select atleast one checkbox");
                                }
                            }else{
                            if (mobile.length() > 0) {
                                    if (isEmailVisible) {
                                        Utils.setSnackBar(parentLayout, "Email should not be empty");
                                    } else {
                                        if (isMobileVisible) {
                                            callRegisterApi(builderObject);
                                            dialog.dismiss();
                                        } else {
                                            Utils.setSnackBar(parentLayout, "Select mobile visibility checkbox");
                                        }
                                    }
                            } else {
                                if (isMobileVisible) {
                                    Utils.setSnackBar(parentLayout, "Mobile should not be empty");
                                } else {
                                    if (isEmailVisible) {
                                        callRegisterApi(builderObject);
                                        dialog.dismiss();
                                    } else {
                                        Utils.setSnackBar(parentLayout, "Select email visibility checkbox");
                                    }
                                }
                            }
                        }
                        } else {
                            if (client.length() == 0) {
                                Utils.setSnackBar(parentLayout, "Client Name should not be empty");
                            } else {
                                Utils.setSnackBar(parentLayout, "Either Mobile Number or Email is mandatory");
                            }
                        }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void callRegisterApi(final BuilderModel.BuilderObject builderObject) {
        try {
            if (Utils.isNetworkAvailable(context)) {
                ApiModel.BuilderAcceptModel builderRegisterModel = new ApiModel.BuilderAcceptModel();
                builderRegisterModel.setBrokerMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
                builderRegisterModel.setPropertyId(builderObject.getPropertyId());
                builderRegisterModel.setEmailId(email);
                builderRegisterModel.setMobileNo(mobile);
                builderRegisterModel.setName(client);
                builderRegisterModel.setBuilderId(builderObject.getUserId());
                builderRegisterModel.setEmailVisibility(isEmailVisible);
                builderRegisterModel.setMobileNoVisibility(isMobileVisible);
                Utils.LoaderUtils.showLoader(context);
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
                        Utils.LoaderUtils.dismissLoader();
                        if (t.getMessage().equals("Too many follow-up requests: 21")) {
                            new AllUtils().getTokenRefresh(context);
                            Utils.setSnackBar(parentLayout,"Please try again");
                        }else {
                            Utils.showToast(context, t.getLocalizedMessage().toString(), "Failure");
                        }
                    }
                });
            }else{
                Utils.internetDialog(context,this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void acceptTc(int position,BuilderModel.BuilderObject builderObject, boolean accept) {
        tc_dialog(position,builderObject,accept);
    }

    @Override
    public void proceedToWeb(int position,BuilderModel.BuilderObject builderObject) {
        try {
            Intent intent = new Intent(context, BuilderProjectActivity.class);
            intent.putExtra("url",builderObject.getUrl());
            intent.putExtra("title",builderObject.getProjectName());
            intent.putExtra("user_id",builderObject.getUserId());
            intent.putExtra("prop_id",builderObject.getPropertyId());
            if(builderObject.getUrl() != null && !(builderObject.getUrl()).isEmpty()){
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerClient(int position,BuilderModel.BuilderObject builderObject) {
        try {
            if(builderObject.getUrl() != null && !(builderObject.getUrl()).isEmpty()){
                builder_registerDialog(builderObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rejectProject(int position,BuilderModel.BuilderObject builderObject) {
        builderRejectApi(position,builderObject);
    }
    private void fetchConnectedClient(){
        try {
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
                                }
                            } else {
                                String responseString = null;
                                try {
                                    responseString = response.errorBody().string();
                                    JSONObject jsonObject = new JSONObject(responseString);
                                    int statusCode = jsonObject.optInt("statusCode");
                                    String message = jsonObject.optString("message");
                                    if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                        apicode = 300;
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
                    public void onFailure(Call<ClientDetailsModel.ConnectedClientModel> call, Throwable t) {
                        Utils.LoaderUtils.dismissLoader();
                        if (t.getMessage().equals("Too many follow-up requests: 21")) {
                           apicode = 300;
                           openTokenDialog(context);
                        }else {
                            Utils.showToast(context, t.getLocalizedMessage().toString(), "Failure");
                        }
                    }
                });
            }else{
                Utils.internetDialog(context,this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void deleteInventory(final ApiModel.InventoryPersoanlList object,final int position){
        try {
            if(Utils.isNetworkAvailable(context)) {
                Utils.LoaderUtils.showLoader(context);
                ApiModel.ClientAcceptModel clientAcceptModel = new ApiModel.ClientAcceptModel();
                //clientAcceptModel.setRentPropertyId(rentPropertyId);
                clientAcceptModel.setBrokerMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
                clientAcceptModel.setPropertyId(object.getPropertyId());
                RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
                String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
                String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
                Call<ResponseBody> call = retrofitAPIs.dropInventoryApi(tokenaccess, "android", deviceId, clientAcceptModel);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Utils.LoaderUtils.dismissLoader();
                        if (response != null) {
                            String responseString = null;
                            if (response.isSuccessful()) {
                                try {
                                    responseString = response.body().string();
                                    JSONObject jsonObject = new JSONObject(responseString);
                                    String message = jsonObject.optString("message");
                                    int statusCode = jsonObject.optInt("statusCode");
                                    if (statusCode == 200 && message.equalsIgnoreCase("Property Deleted Successfully")) {
                                        arraylist.remove(position);
                                        inventoryPersonalAdapter.notifyDataSetChanged();
                               Utils.setSnackBar(parentLayout,message);
                                    }
                                } catch (Exception e) {
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
                                        Utils.setSnackBar(parentLayout, message);
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
                        if (t.getMessage().equals("Too many follow-up requests: 21")) {
                            new AllUtils().getTokenRefresh(context);
                            Utils.setSnackBar(parentLayout,"Please try again");
                        }else {
                            Utils.showToast(context, t.getLocalizedMessage().toString(), "Failure");
                        }
                    }
                });
            }else{
                Utils.internetDialog(context,this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDelete(ApiModel.InventoryPersoanlList object, int position) {
     deleteInventory(object,position);
    }


    @Override
    public void onTryRegenerate() {
        try {
            getToken(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        new AllUtils().getToken(context,this);
    }

    @Override
    public void onSuccessRes(boolean isSuccess) {
        if(isSuccess){
            switch (apicode){
                case 100:
                    fetchList();
                    break;
                case 200:
                    fetchBuilderList();
                    break;
                case 300:
                    fetchConnectedClient();
                    break;
            }
        }else{
            Utils.LoaderUtils.dismissLoader();
            openTokenDialog(context);
        }
    }
}
