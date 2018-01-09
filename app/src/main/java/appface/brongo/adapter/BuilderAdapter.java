package appface.brongo.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import appface.brongo.R;
import appface.brongo.model.ApiModel;
import appface.brongo.model.BuilderModel;
import appface.brongo.other.AllUtils;
import appface.brongo.other.NoInternetTryConnectListener;
import appface.brongo.util.AppConstants;
import appface.brongo.util.CircleTransform;
import appface.brongo.util.CustomApplicationClass;
import appface.brongo.util.RetrofitAPIs;
import appface.brongo.util.RetrofitBuilders;
import appface.brongo.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Rohit Kumar on 12/28/2017.
 */

public class BuilderAdapter extends RecyclerView.Adapter<BuilderAdapter.EmployeeViewHolder> implements NoInternetTryConnectListener{
    private Context context;
    private ArrayList<BuilderModel.BuilderObject> arrayList;
    private LayoutInflater inflater;
    private boolean isVisible = false;;
    private String client,mobile,email;
    private FragmentManager fragmentManager;
    private SharedPreferences pref;


    public BuilderAdapter(Context context, ArrayList<BuilderModel.BuilderObject> arrayList, FragmentManager fragmentManager) {
        this.context = context;
        this.arrayList = arrayList;
        this.fragmentManager = fragmentManager;
        inflater = LayoutInflater.from(context);
        pref = context.getSharedPreferences(AppConstants.PREF_NAME,0);
    }

    @Override
    public BuilderAdapter.EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.inventory_builder_child, parent, false);
        BuilderAdapter.EmployeeViewHolder holder = new BuilderAdapter.EmployeeViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final BuilderAdapter.EmployeeViewHolder holder, final int position) {
      holder.invent_builder_name.setText(arrayList.get(position).getProjectName());
        holder.invent_builder_address.setText(arrayList.get(position).getLocation().getMicroMarketName());
        holder.invent_builder_commission.setText("");
       /* Glide.with(context).load(arrayList.get(position).getImageFiles()).placeholder(R.drawable.no_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.invent_builder_image);*/
        Glide.with(context)
                .load(arrayList.get(position).getImageFiles())
                .apply(CustomApplicationClass.getRequestOptionProperty(false))
                .into(holder.invent_builder_image);
        if(arrayList.get(position).getPricePerSQFT()== 0 || arrayList.get(position).getLandArea()==0){
            holder.invent_builder_budget.setVisibility(View.GONE);
        }else{
            int budget = (arrayList.get(position).getLandArea())* (arrayList.get(position).getPricePerSQFT());
            String budget1 = Utils.numToWord(budget);
            if(budget1.equalsIgnoreCase("")){
                holder.invent_builder_budget.setVisibility(View.GONE);
            }else {
                holder.invent_builder_budget.setText(budget1);
            }
        }
        if(arrayList.get(position).getNoOfBedRooms() == 0){
            holder.invent_builder_bhk.setVisibility(View.GONE);
        }else{
            holder.invent_builder_bhk.setText(arrayList.get(position).getPricingSheet()+" Bedrooms");
        }
        if(arrayList.get(position).getLandArea() == 0){
            holder.invent_builder_floorSize.setVisibility(View.GONE);
        }else{
            holder.invent_builder_floorSize.setText(arrayList.get(position).getLandArea()+" sqft");
        }
        if(arrayList.get(position).getProjectStatus().equalsIgnoreCase("")){
            holder.invent_builder_prop_status.setVisibility(View.GONE);
        }else{
            holder.invent_builder_prop_status.setText(arrayList.get(position).getProjectStatus());
        }
        if(arrayList.get(position).getProjectType().equalsIgnoreCase("")){
            holder.invent_builder_prop_type.setVisibility(View.GONE);
        }else{
            holder.invent_builder_prop_type.setText(arrayList.get(position).getProjectType());
        }
        holder.invent_builder_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder_registerDialog(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public void onTryReconnect() {

    }

    public class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView invent_builder_name,invent_builder_address,invent_builder_bhk,invent_builder_commission,invent_builder_prop_status,invent_builder_prop_type,invent_builder_budget,invent_builder_floorSize;
        ImageView invent_builder_image;
        LinearLayout recycle_item_linear;
        Button invent_builder_tc,invent_builder_register;

        public EmployeeViewHolder(View itemView) {
            super(itemView);
            invent_builder_name = (TextView) itemView.findViewById(R.id.invent_builder_project);
            invent_builder_address = (TextView) itemView.findViewById(R.id.invent_builder_addresss);
            invent_builder_bhk = (TextView) itemView.findViewById(R.id.invent_builder_bhk);
            invent_builder_commission = (TextView) itemView.findViewById(R.id.invent_builder_commission);
            invent_builder_prop_status = (TextView) itemView.findViewById(R.id.invent_builder_prop_status);
            invent_builder_prop_type = (TextView) itemView.findViewById(R.id.invent_builder_prop_type);
            invent_builder_budget = (TextView) itemView.findViewById(R.id.invent_builder_budget);
            invent_builder_floorSize = (TextView) itemView.findViewById(R.id.invent_builder_size);
            invent_builder_image = (ImageView) itemView.findViewById(R.id.invent_builder_image);
            invent_builder_tc = (Button) itemView.findViewById(R.id.invent_builder_tc);
            invent_builder_register = (Button) itemView.findViewById(R.id.invent_builder_register);
        }
    }

    private void builder_registerDialog(final int position){
        client = mobile = email ="";
        String[] inventory_bhklist = {"1 BHK","2 BHK","3 BHK","4 BHK","4 BHK+"};
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.5f);
        dialog.setContentView(R.layout.register_client_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        //dialog.setCanceledOnTouchOutside(false);
        // dialog.setCancelable(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        TextView clientAdd = (TextView)dialog.findViewById(R.id.client_register_add);
        final LinearLayout manual_register = (LinearLayout)dialog.findViewById(R.id.manual_register_linear);
        final EditText client_name = (EditText)dialog.findViewById(R.id.client_name_register);
        final EditText client_email = (EditText)dialog.findViewById(R.id.client_email_register);
        final EditText client_mobile = (EditText)dialog.findViewById(R.id.client_mobile_register);
        MaterialBetterSpinner client_spinner = (MaterialBetterSpinner) dialog.findViewById(R.id.inventory_spinner_client1);
        Button register_btn = (Button)dialog.findViewById(R.id.client_register_register);
        Button register_cancel_btn = (Button)dialog.findViewById(R.id.client_register_cancel);
        ArrayAdapter<String> clientAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, inventory_bhklist);
        client_spinner.setAdapter(clientAdapter);
        client_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                client = parent.getItemAtPosition(position).toString();
            }
        });
        clientAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isVisible){
                    isVisible=true;
                    manual_register.setVisibility(View.VISIBLE);
                }else{
                    isVisible = false;
                    manual_register.setVisibility(View.GONE);
                }
            }
        });
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mobile.equalsIgnoreCase("")){
                    mobile = client_mobile.getText().toString();
                    client = client_name.getText().toString();
                    email = client_email.getText().toString();
                }
                if(mobile.equalsIgnoreCase("")){
                    Utils.showToast(context,"Mobile should not be empty");
                }else if(mobile.length() == 10 && mobile.startsWith("7") || mobile.startsWith("8") || mobile.startsWith("9")){
                    callRegisterApi(position,mobile,client,email);
                }else{
                    Utils.showToast(context,"Invalid mobile Number");
                }
            }
        });
        register_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void callRegisterApi(final int position, final String mobile_no, final String name, final String email) {
        if (Utils.isNetworkAvailable(context)) {
            Utils.LoaderUtils.showLoader(context);
            ApiModel.BuilderAcceptModel builderRegisterModel = new ApiModel.BuilderAcceptModel();
            builderRegisterModel.setBrokerMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
            builderRegisterModel.setPropertyId(arrayList.get(position).getPropertyId());
            builderRegisterModel.setEmailId(email);
            builderRegisterModel.setMobileNo(mobile_no);
            builderRegisterModel.setName(name);
            builderRegisterModel.setBuilderId(arrayList.get(position).getUserId());
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
                                    callRegisterApi(position, mobile_no, name, email);
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
}
