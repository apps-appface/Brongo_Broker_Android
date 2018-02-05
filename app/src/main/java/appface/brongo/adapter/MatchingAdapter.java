package appface.brongo.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import appface.brongo.R;
import appface.brongo.model.ApiModel;
import appface.brongo.util.AppConstants;
import appface.brongo.util.CustomApplicationClass;
import appface.brongo.util.Utils;

/**
 * Created by Rohit Kumar on 12/11/2017.
 */

public class MatchingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<ApiModel.MatchingModel> arrayList;
    private LayoutInflater inflater;
    private static final int TYPE_PERSONAL = 0;
    private static final int TYPE_BUILDER = 1;
    private ProgressDialog pd;
    private FragmentManager fragmentManager;
    private SharedPreferences pref;


    public MatchingAdapter(Context context, ArrayList<ApiModel.MatchingModel> arrayList,FragmentManager fragmentManager) {
        this.context = context;
        this.arrayList = arrayList;
        this.fragmentManager = fragmentManager;
        inflater = LayoutInflater.from(context);
        pd = new ProgressDialog(context, R.style.CustomProgressDialog);
        pd.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress_loader));
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);
        pref = context.getSharedPreferences(AppConstants.PREF_NAME,0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_PERSONAL) {
            //Inflating header view
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_player_child, parent, false);
            return new PersonalViewHolder(itemView);
        } else if (viewType == TYPE_BUILDER) {
            //Inflating footer view
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_builder_child, parent, false);
            return new BuilderViewHolder(itemView);
        } else return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder personalholder, int position) {
        if (personalholder instanceof PersonalViewHolder) {
            PersonalViewHolder holder = (PersonalViewHolder) personalholder;
            String budget = arrayList.get(position).getBudget()+"";
            budget = Utils.stringToInt(budget);
            holder.personal_tag.setVisibility(View.VISIBLE);
            holder.invent_child_clientName.setText(arrayList.get(position).getName());
            holder.invent_child_client.setText(arrayList.get(position).getPostingtype().toUpperCase()+"/"+arrayList.get(position).getPropertytype().toUpperCase());
            holder.invent_child_mobile.setText(arrayList.get(position).getMobile());
            holder.invent_child_location.setText(arrayList.get(position).getAddress());
            if(arrayList.get(position).getBhk().equalsIgnoreCase("")){
                holder.invent_child_bhk.setVisibility(View.GONE);
            }else {
                holder.invent_child_bhk.setText(arrayList.get(position).getBhk());
            }
            if(arrayList.get(position).getPropertystatus().equalsIgnoreCase("")){
                holder.invent_child_prop_status.setVisibility(View.GONE);
            }else {
                holder.invent_child_prop_status.setText(arrayList.get(position).getPropertystatus());
            }
            if(budget.equalsIgnoreCase("")){
                holder.invent_child_budget.setVisibility(View.GONE);
            }else {
                holder.invent_child_budget.setText(budget);
            }if(arrayList.get(position).getSubproperty() == null){
                holder.invent_child_prop_type.setVisibility(View.GONE);
            }else {
                if (arrayList.get(position).getSubproperty().equalsIgnoreCase("")) {
                    holder.invent_child_prop_type.setVisibility(View.GONE);
                } else {
                    holder.invent_child_prop_type.setText(arrayList.get(position).getSubproperty());
                }
            }
            if(arrayList.get(position).getPostingtype().equalsIgnoreCase("sell")){
                holder.invent_child_client.setBackgroundColor(Color.parseColor("#3664cb"));
            }else if(arrayList.get(position).getPostingtype().equalsIgnoreCase("rentOut")){
                holder.invent_child_client.setBackgroundColor(Color.parseColor("#80cb36"));
            }else if(arrayList.get(position).getPostingtype().equalsIgnoreCase("rent")){
                holder.invent_child_client.setBackgroundColor(Color.parseColor("#80cb36"));
            }else{
                holder.invent_child_client.setBackgroundColor(Color.parseColor("#ea8737"));
            }

            holder.invent_child_editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //inventoryEdit(position);

                }
            });
            holder.invent_child_deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //inventory_delete_dialog(position);
                }
            });
            holder.recycle_item_linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // changeFragment(position);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    private class BuilderViewHolder extends RecyclerView.ViewHolder {

        public BuilderViewHolder(View view) {
            super(view);

        }
    }

    private class PersonalViewHolder extends RecyclerView.ViewHolder {
        TextView personal_tag,invent_child_client,invent_child_clientName,invent_child_bhk,invent_child_mobile,invent_child_prop_status,invent_child_location,invent_child_budget,invent_child_prop_type;
        ImageView prop_image;
        LinearLayout recycle_item_linear;
        Button invent_child_editBtn,invent_child_deleteBtn;

        public PersonalViewHolder(View itemView) {
            super(itemView);
            invent_child_clientName = (TextView) itemView.findViewById(R.id.invent_personal_name);
            personal_tag = (TextView) itemView.findViewById(R.id.personal_tag);
            invent_child_client = (TextView) itemView.findViewById(R.id.invent_pesonal_postingtype);
            invent_child_mobile = (TextView) itemView.findViewById(R.id.invent_pesonal_mobile);
            prop_image = (ImageView) itemView.findViewById(R.id.invent_personal_image);
            invent_child_editBtn = (Button) itemView.findViewById(R.id.edit_image);
            invent_child_deleteBtn = (Button) itemView.findViewById(R.id.delete_image);
            recycle_item_linear = (LinearLayout)itemView.findViewById(R.id.linear_item);
        }
    }
    @Override
    public int getItemViewType(int position) {
        int viewtype =100;
        if (arrayList.get(position).getInventory_type().equalsIgnoreCase("personal")) {
            viewtype = TYPE_PERSONAL;
        } else if (arrayList.get(position).getInventory_type().equalsIgnoreCase("builder")) {
            viewtype = TYPE_BUILDER;
        }
        return viewtype;
    }
}
