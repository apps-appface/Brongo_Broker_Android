package appface.brongo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import appface.brongo.R;
import appface.brongo.model.ApiModel;
import appface.brongo.util.Utils;

/**
 * Created by Rohit Kumar on 10/9/2017.
 */

public class HistoricalAdapter extends RecyclerView.Adapter<HistoricalAdapter.MyViewHolder> {


    List<ApiModel.HistoricalModel> arraylist = Collections.emptyList();
    Context context;


    public HistoricalAdapter(List<ApiModel.HistoricalModel> arraylist, Context context) {
        this.arraylist = arraylist;
        this.context = context;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView his_name,his_post_type,his_commission,his_address,his_bhk,his_budget,his_prop_status,his_sub_proptype;
        public MyViewHolder(View view) {
            super(view);
            his_name=(TextView) view.findViewById(R.id.his_lead_name);
            his_post_type=(TextView) view.findViewById(R.id.his_lead_post_type);
            his_commission=(TextView) view.findViewById(R.id.his_deal_commission);
            his_address=(TextView) view.findViewById(R.id.his_lead_address);
            his_bhk=(TextView) view.findViewById(R.id.his_lead_bhk);
            his_budget=(TextView) view.findViewById(R.id.his_lead_budget);
            his_prop_status=(TextView) view.findViewById(R.id.his_lead_prop_status);
            his_sub_proptype=(TextView) view.findViewById(R.id.his_lead_prop_type);
        }
    }



    @Override
    public HistoricalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.historical_deal_child, parent, false);

        return new HistoricalAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final HistoricalAdapter.MyViewHolder holder, final int position) {
        holder.his_name.setText(arraylist.get(position).getClientName());
        holder.his_address.setText(arraylist.get(position).getMicroMarketName());
        holder.his_commission.setText(arraylist.get(position).getCommission() + "");
        if(arraylist.get(position).getBedRoomType().equalsIgnoreCase("")){
            holder.his_bhk.setVisibility(View.GONE);
        }else {
            holder.his_bhk.setText(arraylist.get(position).getBedRoomType());
        }
        if(arraylist.get(position).getPropertyStatus().equalsIgnoreCase("")){
            holder.his_prop_status.setVisibility(View.GONE);
        }else {
            holder.his_prop_status.setText(arraylist.get(position).getPropertyStatus());
        }
        if(arraylist.get(position).getSubPropertyType().equalsIgnoreCase("")){
            holder.his_sub_proptype.setVisibility(View.GONE);
        }else {
            holder.his_sub_proptype.setText(arraylist.get(position).getSubPropertyType());
        }
        holder.his_post_type.setText(arraylist.get(position).getPostingType().toUpperCase()+"/"+arraylist.get(position).getPropertyType().toUpperCase());
        if(arraylist.get(position).getPostingType().equalsIgnoreCase("sell")){
            String budget = arraylist.get(position).getExpectedPrice()+"";
            budget = Utils.stringToInt(budget);
            if(budget.equalsIgnoreCase("")){
                holder.his_budget.setVisibility(View.GONE);
            }else {
                holder.his_budget.setText(budget);
            }
            holder.his_post_type.setBackgroundColor(Color.parseColor("#3664cb"));
        }else if(arraylist.get(position).getPostingType().equalsIgnoreCase("RENT_OUT")){
            String budget = arraylist.get(position).getExpectedRent()+"";
            budget = Utils.stringToInt(budget);
            if(budget.equalsIgnoreCase("")){
                holder.his_budget.setVisibility(View.GONE);
            }else {
                holder.his_budget.setText(budget);
            }
            holder.his_post_type.setBackgroundColor(Color.parseColor("#80cb36"));
        }else if(arraylist.get(position).getPostingType().equalsIgnoreCase("rent")){
            String budget = arraylist.get(position).getBudget();
            budget = Utils.stringToInt(budget);
            if(budget.equalsIgnoreCase("")){
                holder.his_budget.setVisibility(View.GONE);
            }else {
                holder.his_budget.setText(budget);
            }
            holder.his_post_type.setBackgroundColor(Color.parseColor("#80cb36"));
        }else if(arraylist.get(position).getPostingType().equalsIgnoreCase("BUY")){
            String budget = arraylist.get(position).getBudget();
            budget = Utils.stringToInt(budget);
            if(budget.equalsIgnoreCase("")){
                holder.his_budget.setVisibility(View.GONE);
            }else {
                holder.his_budget.setText(budget);
            }
            holder.his_post_type.setBackgroundColor(Color.parseColor("#ea8737"));
        }
    }


    @Override
    public int getItemCount()
    {
        return arraylist.size();
    }
}
