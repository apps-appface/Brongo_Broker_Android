package in.brongo.brongo_broker.adapter;

import android.app.Dialog;
import android.support.v4.app.FragmentManager;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;


import java.util.ArrayList;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.fragment.AddInventoryFragment;
import in.brongo.brongo_broker.fragment.IndividualInventoryFragment;
import in.brongo.brongo_broker.model.ApiModel;
import in.brongo.brongo_broker.other.NoInternetTryConnectListener;
import in.brongo.brongo_broker.uiwidget.FlowLayout;
import in.brongo.brongo_broker.util.AppConstants;
import in.brongo.brongo_broker.util.CustomApplicationClass;
import in.brongo.brongo_broker.util.Utils;

import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.ADD_INVENTORY;
import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.INDIVIDUAL_INVENTORY;

/**
 * Created by Rohit Kumar on 9/20/2017.
 */

public class InventoryPersonalAdapter extends RecyclerView.Adapter<InventoryPersonalAdapter.EmployeeViewHolder> implements NoInternetTryConnectListener{
    private Context context;
    private DeleteInventoryListener deleteInventoryListener;
    private ArrayList<ApiModel.InventoryPersoanlList> arrayList;
    private LayoutInflater inflater;
    private FragmentManager fragmentManager;
    private SharedPreferences pref;


    public InventoryPersonalAdapter(Context context, ArrayList<ApiModel.InventoryPersoanlList> arrayList,FragmentManager fragmentManager,DeleteInventoryListener deleteInventoryListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.fragmentManager = fragmentManager;
        inflater = LayoutInflater.from(context);
        this.deleteInventoryListener = deleteInventoryListener;
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
        holder.setIsRecyclable(false);
        final ApiModel.InventoryPersoanlList object1 = arrayList.get(position);
        String budget = arrayList.get(position).getBudget()+"";
        budget = Utils.stringToInt(budget);
        /*holder.statename.setText(object1.getMicroMarketState());
        holder.cityname.setText(object1.getMicroMarketCity());
        holder.microname.setText(object1.getMicroMarketName());*/
       // Glide.with(context).load(arrayList.get(position).getPropertyImage1()).into(holder.prop_image);
       /* Glide.with(context).load(arrayList.get(position).getPropertyImage1()).skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.prop_image);*/
        String image = arrayList.get(position).getPropertyImage1();
           /* Glide.with(context)
                    .load(arrayList.get(position).getPropertyImage1())
                    .into(holder.prop_image);*/
            Glide.with(context)
                    .load(arrayList.get(position).getPropertyImage1())
                    .apply(CustomApplicationClass.getPropertyImage(true))
                    .into(holder.prop_image);
        holder.invent_child_clientName.setText(arrayList.get(position).getClientName());
        holder.invent_child_client.setText(arrayList.get(position).getPostingType().toUpperCase()+"/"+arrayList.get(position).getPropertyType().toUpperCase());
        holder.invent_child_mobile.setText(arrayList.get(position).getClientMobileNo());
       addView(arrayList.get(position).getMicroMarketName(),holder.flowLayout);
        addView(arrayList.get(position).getBedRoomType(),holder.flowLayout);
        addView(arrayList.get(position).getPropertyStatus(),holder.flowLayout);
        addView(budget,holder.flowLayout);
        addView(arrayList.get(position).getSubPropertyType(),holder.flowLayout);
        String backColor = Utils.getPostingColor(arrayList.get(position).getPostingType());
            holder.invent_child_client.setBackgroundColor(Color.parseColor(backColor));
        holder.invent_child_editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inventoryEdit(position);

            }
        });
        holder.invent_child_deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inventory_delete_dialog(object1,position);
            }
        });
        holder.recycle_item_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(position);
            }
        });
    }

    private void addView(String text, FlowLayout flowLayout) {
        if(text != null) {
            if (!text.isEmpty()) {
                try {
                    View layout2 = LayoutInflater.from(context).inflate(R.layout.deal_child, flowLayout, false);
                    TextView deal_textview = (TextView) layout2.findViewById(R.id.deal_text);
                    deal_textview.setText(text);
                    flowLayout.addView(layout2);
                } catch (Exception e) {
                    String error = e.toString();
                }
            }
        }
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
        FlowLayout flowLayout;
        LinearLayout recycle_item_linear,invent_child_editBtn,invent_child_deleteBtn;

        public EmployeeViewHolder(View itemView) {
            super(itemView);
            flowLayout = (FlowLayout)itemView.findViewById(R.id.invent_personal_flowlayout);
            invent_child_clientName = (TextView) itemView.findViewById(R.id.invent_personal_name);
            invent_child_client = (TextView) itemView.findViewById(R.id.invent_pesonal_postingtype);
            invent_child_mobile = (TextView) itemView.findViewById(R.id.invent_pesonal_mobile);
            prop_image = (ImageView) itemView.findViewById(R.id.invent_personal_image);
            invent_child_editBtn = (LinearLayout) itemView.findViewById(R.id.edit_image);
            invent_child_deleteBtn = (LinearLayout) itemView.findViewById(R.id.delete_image);
            recycle_item_linear = (LinearLayout)itemView.findViewById(R.id.linear_item);
        }
    }

    private void inventory_delete_dialog(final  ApiModel.InventoryPersoanlList object,final int position){
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
            String message = "Are you sure you want to delete the inventory details of '" + object.getClientName() + "'?";
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
                    deleteInventoryListener.onDelete(object,position);
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
        Utils.replaceFragment(fragmentManager,addInventoryFragment,R.id.inventory_frag_container,ADD_INVENTORY);
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
        Utils.replaceFragment(fragmentManager,individualInventoryFragment,R.id.inventory_frag_container,INDIVIDUAL_INVENTORY);
    }
    public interface DeleteInventoryListener{
        void onDelete(ApiModel.InventoryPersoanlList object,int position);
    }

}
