package appface.brongo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import appface.brongo.R;
import appface.brongo.model.ApiModel;
import appface.brongo.uiwidget.FlowLayout;
import appface.brongo.util.Utils;

/**
 * Created by Rohit Kumar on 10/9/2017.
 */

public class HistoricalAdapter extends RecyclerView.Adapter<HistoricalAdapter.MyViewHolder> {


    List<ApiModel.BuyAndRentModel> arraylist = Collections.emptyList();
    Context context;


    public HistoricalAdapter(List<ApiModel.BuyAndRentModel> arraylist, Context context) {
        this.arraylist = arraylist;
        this.context = context;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView his_name,his_post_type,his_commission,his_address,his_bhk,his_budget,his_prop_status,his_sub_proptype,his_deal_id;
        FlowLayout his_flowlayout;
        public MyViewHolder(View view) {
            super(view);
            his_flowlayout = (FlowLayout)view.findViewById(R.id.his_flow);
            his_name=(TextView) view.findViewById(R.id.his_lead_name);
            his_post_type=(TextView) view.findViewById(R.id.his_lead_post_type);
            his_commission=(TextView) view.findViewById(R.id.his_deal_commission);
            his_address=(TextView) view.findViewById(R.id.his_lead_address);
            his_bhk=(TextView) view.findViewById(R.id.his_lead_bhk);
            his_budget=(TextView) view.findViewById(R.id.his_lead_budget);
            his_prop_status=(TextView) view.findViewById(R.id.his_lead_prop_status);
            his_sub_proptype=(TextView) view.findViewById(R.id.his_lead_prop_type);
            his_deal_id=(TextView) view.findViewById(R.id.his_deal_id);
        }
    }



    @Override
    public HistoricalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.historical_deal_child, parent, false);

        return new HistoricalAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final HistoricalAdapter.MyViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        holder.his_name.setText(arraylist.get(position).getClientName());
       //holder.his_address.setText(arraylist.get(position).getMicroMarketName());
        holder.his_commission.setText(arraylist.get(position).getCommission() + "%");
        holder.his_deal_id.setText(arraylist.get(position).getPropertyId());
        addview(arraylist.get(position).getProperty(),holder.his_flowlayout);
       /* if(arraylist.get(position).getBedRoomType().equalsIgnoreCase("")){
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
        }*/
     //  addView(arraylist.get(position).getMicroMarketName(),holder.his_flowlayout);
       // addView(arraylist.get(position).getBedRoomType(),holder.his_flowlayout);
      //  String budget = arraylist.get(position).getBudget()+"";
      //  budget = Utils.stringToInt(budget);
        //addView(budget,holder.his_flowlayout);
        holder.his_post_type.setText(arraylist.get(position).getPostingType().toUpperCase()+"/"+arraylist.get(position).getPropertyType().toUpperCase());
        String back_color = Utils.getPostingColor(arraylist.get(position).getPostingType());
        holder.his_post_type.setBackgroundColor(Color.parseColor(back_color));
       // addView(arraylist.get(position).getPropertyStatus(),holder.his_flowlayout);
        //addView(arraylist.get(position).getSubPropertyType(),holder.his_flowlayout);
    }
   /* private void addView(String text, FlowLayout flowLayout) {
        if(text != null) {
            if (!text.isEmpty()) {
                try {
                    View layout2 = LayoutInflater.from(context).inflate(R.layout.deal_child, flowLayout, false);
                    TextView deal_textview = (TextView) layout2.findViewById(R.id.deal_text);
                    deal_textview.setBackgroundResource(R.drawable.rounded_purple);
                    deal_textview.setText(text);
                    flowLayout.addView(layout2);
                } catch (Exception e) {
                    String error = e.toString();
                }
            }
        }
    }*/
    private void addview(ArrayList<String> keyList, FlowLayout flowLayout) {
        if(keyList != null && keyList.size()> 0 ) {
            for (int j = 0; j < keyList.size(); j++) {
                try {
                    if(!keyList.get(j).isEmpty()) {
                        View layout2 = LayoutInflater.from(context).inflate(R.layout.deal_child, flowLayout, false);
                        TextView deal_textview = (TextView) layout2.findViewById(R.id.deal_text);
                        deal_textview.setBackgroundResource(R.drawable.rounded_blue_btn);
                        deal_textview.setTextColor(context.getResources().getColor(R.color.white));
                        deal_textview.setText(keyList.get(j));
                        flowLayout.addView(layout2);
                    }
                } catch (Exception e) {
                    String error = e.toString();
                }
            }
        }
    }


    @Override
    public int getItemCount()
    {
        return arraylist.size();
    }
}
