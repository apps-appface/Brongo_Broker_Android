package appface.brongo.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.util.Util;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import appface.brongo.R;
import appface.brongo.fragment.AddInventoryFragment;
import appface.brongo.fragment.IndividualInventoryFragment;
import appface.brongo.model.ApiModel;
import appface.brongo.other.AllUtils;
import appface.brongo.other.NoInternetTryConnectListener;
import appface.brongo.util.AppConstants;
import appface.brongo.util.CustomApplicationClass;
import appface.brongo.util.RefreshTokenCall;
import appface.brongo.util.RetrofitAPIs;
import appface.brongo.util.RetrofitBuilders;
import appface.brongo.util.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Rohit Kumar on 9/20/2017.
 */

public class InventoryPersonalAdapter extends RecyclerView.Adapter<InventoryPersonalAdapter.EmployeeViewHolder> implements NoInternetTryConnectListener{
    private Context context;
    private ArrayList<ApiModel.InventoryPersoanlList> arrayList;
    private LayoutInflater inflater;
    private FragmentManager fragmentManager;
    private SharedPreferences pref;


    public InventoryPersonalAdapter(Context context, ArrayList<ApiModel.InventoryPersoanlList> arrayList,FragmentManager fragmentManager) {
        this.context = context;
        this.arrayList = arrayList;
        this.fragmentManager = fragmentManager;
        inflater = LayoutInflater.from(context);
        pref = context.getSharedPreferences(AppConstants.PREF_NAME,0);
    }

    @Override
    public InventoryPersonalAdapter.EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.inventory_player_child, parent, false);
        InventoryPersonalAdapter.EmployeeViewHolder holder = new InventoryPersonalAdapter.EmployeeViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final InventoryPersonalAdapter.EmployeeViewHolder holder, final int position) {
        ApiModel.InventoryPersoanlList object1 = arrayList.get(position);
        String budget = arrayList.get(position).getBudget()+"";
        budget = Utils.stringToInt(budget);
        /*holder.statename.setText(object1.getMicroMarketState());
        holder.cityname.setText(object1.getMicroMarketCity());
        holder.microname.setText(object1.getMicroMarketName());*/
       // Glide.with(context).load(arrayList.get(position).getPropertyImage1()).into(holder.prop_image);
       /* Glide.with(context).load(arrayList.get(position).getPropertyImage1()).skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.prop_image);*/
        Glide.with(context)
                .load(arrayList.get(position).getPropertyImage1())
                .apply(CustomApplicationClass.getRequestOptionProperty(false))
                .into(holder.prop_image);
        holder.invent_child_clientName.setText(arrayList.get(position).getClientName());
        holder.invent_child_client.setText(arrayList.get(position).getPostingType().toUpperCase()+"/"+arrayList.get(position).getPropertyType().toUpperCase());
        holder.invent_child_mobile.setText(arrayList.get(position).getClientMobileNo());
        holder.invent_child_location.setText(arrayList.get(position).getMicroMarketName());
        if(arrayList.get(position).getBedRoomType().equalsIgnoreCase("")){
            holder.invent_child_bhk.setVisibility(View.GONE);
        }else {
            holder.invent_child_bhk.setText(arrayList.get(position).getBedRoomType());
        }
        if(arrayList.get(position).getPropertyStatus().equalsIgnoreCase("")){
            holder.invent_child_prop_status.setVisibility(View.GONE);
        }else {
            holder.invent_child_prop_status.setText(arrayList.get(position).getPropertyStatus());
        }
        if(budget.equalsIgnoreCase("")){
            holder.invent_child_budget.setVisibility(View.GONE);
        }else {
            holder.invent_child_budget.setText(budget);
        }if(arrayList.get(position).getSubPropertyType() == null){
            holder.invent_child_prop_type.setVisibility(View.GONE);
        }else {
            if (arrayList.get(position).getSubPropertyType().equalsIgnoreCase("")) {
                holder.invent_child_prop_type.setVisibility(View.GONE);
            } else {
                holder.invent_child_prop_type.setText(arrayList.get(position).getSubPropertyType());
            }
        }
        if(arrayList.get(position).getPostingType().equalsIgnoreCase("sell")){
            holder.invent_child_client.setBackgroundColor(Color.parseColor("#3664cb"));
        }else if(arrayList.get(position).getPostingType().equalsIgnoreCase("rent_Out")){
            holder.invent_child_client.setBackgroundColor(Color.parseColor("#80cb36"));
        }else if(arrayList.get(position).getPostingType().equalsIgnoreCase("rent")){
            holder.invent_child_client.setBackgroundColor(Color.parseColor("#80cb36"));
        }else{
            holder.invent_child_client.setBackgroundColor(Color.parseColor("#ea8737"));
        }

        holder.invent_child_editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inventoryEdit(position);

            }
        });
        holder.invent_child_deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inventory_delete_dialog(position);
            }
        });
        holder.recycle_item_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(position);
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
        TextView invent_child_client,invent_child_clientName,invent_child_bhk,invent_child_mobile,invent_child_prop_status,invent_child_location,invent_child_budget,invent_child_prop_type;
        ImageView prop_image;
        LinearLayout recycle_item_linear;
        Button invent_child_editBtn,invent_child_deleteBtn;

        public EmployeeViewHolder(View itemView) {
            super(itemView);
            invent_child_clientName = (TextView) itemView.findViewById(R.id.invent_personal_name);
            invent_child_client = (TextView) itemView.findViewById(R.id.invent_pesonal_postingtype);
            invent_child_bhk = (TextView) itemView.findViewById(R.id.invent_personal_bhk);
            invent_child_mobile = (TextView) itemView.findViewById(R.id.invent_pesonal_mobile);
            invent_child_prop_status = (TextView) itemView.findViewById(R.id.invent_personal_prop_status);
            invent_child_location = (TextView) itemView.findViewById(R.id.invent_personal_address);
            invent_child_budget = (TextView) itemView.findViewById(R.id.invent_personal_budget);
            invent_child_prop_type = (TextView) itemView.findViewById(R.id.invent_personal_prop_type);
            prop_image = (ImageView) itemView.findViewById(R.id.invent_personal_image);
            invent_child_editBtn = (Button) itemView.findViewById(R.id.edit_image);
            invent_child_deleteBtn = (Button) itemView.findViewById(R.id.delete_image);
            recycle_item_linear = (LinearLayout)itemView.findViewById(R.id.linear_item);
        }
    }
    private void deleteInventory(final int position){
        if(Utils.isNetworkAvailable(context)) {
            Utils.LoaderUtils.showLoader(context);
            ApiModel.ClientAcceptModel clientAcceptModel = new ApiModel.ClientAcceptModel();
            //clientAcceptModel.setRentPropertyId(rentPropertyId);
            clientAcceptModel.setBrokerMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
            clientAcceptModel.setPropertyId(arrayList.get(position).getPropertyId());
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
                                    arrayList.remove(position);
                                    notifyDataSetChanged();
                                    Utils.showToast(context, message);
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
                                    deleteInventory(position);
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
    private void inventory_delete_dialog(final int position){
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.dialog_delete_inventory);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            //dialog.setCanceledOnTouchOutside(false);
            // dialog.setCancelable(false);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            TextView delete_client_message = (TextView) dialog.findViewById(R.id.delete_inventory_client_name);
            Button delete_client_cancel = (Button) dialog.findViewById(R.id.inventory_delete_cancel);
            Button delete_client_delete = (Button) dialog.findViewById(R.id.inventory_delete_delete);
            String message = "Are you sure you want to delete the \n inventory details of '" + arrayList.get(position).getClientName() + "'?";
            delete_client_message.setText(message);
            delete_client_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            delete_client_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        deleteInventory(position);
                    dialog.dismiss();
                }
            });
            dialog.show();
    }
    private void inventoryEdit(int position){
        Bundle bundle = new Bundle();
        bundle.putString("bedRoomType",arrayList.get(position).getBedRoomType());
        bundle.putLong("budget",arrayList.get(position).getBudget());
        bundle.putString("propertyImage1",arrayList.get(position).getPropertyImage1());
        bundle.putString("note",arrayList.get(position).getNote());
        bundle.putString("emailId",arrayList.get(position).getEmailId());
        bundle.putString("clientMobileNo",arrayList.get(position).getClientMobileNo());
        bundle.putString("clientName",arrayList.get(position).getClientName());
        bundle.putString("propertyStatus",arrayList.get(position).getPropertyStatus());
        bundle.putString("propertyType",arrayList.get(position).getPropertyType());
        bundle.putString("microMarketState",arrayList.get(position).getMicroMarketState());
        bundle.putString("microMarketCity",arrayList.get(position).getMicroMarketCity());
        bundle.putString("microMarketName",arrayList.get(position).getMicroMarketName());
        bundle.putString("postingType",arrayList.get(position).getPostingType());
        bundle.putString("propertyId",arrayList.get(position).getPropertyId());
        bundle.putString("propertyImage3",arrayList.get(position).getPropertyImage3());
        bundle.putString("propertyImage2",arrayList.get(position).getPropertyImage2());
        bundle.putString("edit_inventory","edit_inventory");
        bundle.putString("subPropertyType",arrayList.get(position).getSubPropertyType());
        AddInventoryFragment addInventoryFragment = new AddInventoryFragment();
        addInventoryFragment.setArguments(bundle);
        Utils.replaceFragment(fragmentManager,addInventoryFragment,R.id.inventory_frag_container,false);
    }
    private void changeFragment(int position){
        Bundle bundle = new Bundle();
        bundle.putString("bedRoomType",arrayList.get(position).getBedRoomType());
        bundle.putLong("budget",arrayList.get(position).getBudget());
        bundle.putString("propertyImage1",arrayList.get(position).getPropertyImage1());
        bundle.putString("note",arrayList.get(position).getNote());
        bundle.putString("emailId",arrayList.get(position).getEmailId());
        bundle.putString("clientMobileNo",arrayList.get(position).getClientMobileNo());
        bundle.putString("clientName",arrayList.get(position).getClientName());
        bundle.putString("propertyStatus",arrayList.get(position).getPropertyStatus());
        bundle.putString("propertyType",arrayList.get(position).getPropertyType());
        bundle.putString("microMarketState",arrayList.get(position).getMicroMarketState());
        bundle.putString("microMarketCity",arrayList.get(position).getMicroMarketCity());
        bundle.putString("microMarketName",arrayList.get(position).getMicroMarketName());
        bundle.putString("postingType",arrayList.get(position).getPostingType());
        bundle.putString("propertyId",arrayList.get(position).getPropertyId());
        bundle.putString("propertyImage3",arrayList.get(position).getPropertyImage3());
        bundle.putString("propertyImage2",arrayList.get(position).getPropertyImage2());
        bundle.putString("edit_inventory","edit_inventory");
        bundle.putString("subPropertyType",arrayList.get(position).getSubPropertyType());
        IndividualInventoryFragment individualInventoryFragment = new IndividualInventoryFragment();
        individualInventoryFragment.setArguments(bundle);
        Utils.replaceFragment(fragmentManager,individualInventoryFragment,R.id.inventory_frag_container,true);
    }

}
