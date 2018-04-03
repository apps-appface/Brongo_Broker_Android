package in.brongo.brongo_broker.adapter;

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

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.model.ApiModel;
import in.brongo.brongo_broker.uiwidget.FlowLayout;
import in.brongo.brongo_broker.util.Utils;

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

        TextView his_name,his_post_type,his_commission,his_deal_id;
        FlowLayout his_flowlayout;
        public MyViewHolder(View view) {
            super(view);
            his_flowlayout = view.findViewById(R.id.his_flow);
            his_name= view.findViewById(R.id.his_lead_name);
            his_post_type= view.findViewById(R.id.his_lead_post_type);
            his_commission= view.findViewById(R.id.his_deal_commission);
            his_deal_id= view.findViewById(R.id.his_deal_id);
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
        try {
            holder.his_name.setText(arraylist.get(position).getClientName());
            //holder.his_address.setText(arraylist.get(position).getMicroMarketName());
            holder.his_commission.setText(arraylist.get(position).getCommission() + "%");
            holder.his_deal_id.setText("DEAL ID : "+arraylist.get(position).getPropertyId());
            addview(arraylist.get(position).getProperty(),holder.his_flowlayout);
            holder.his_post_type.setText(arraylist.get(position).getPostingType().toUpperCase()+"/"+arraylist.get(position).getPropertyType().toUpperCase());
            String back_color = Utils.getPostingColor(arraylist.get(position).getPostingType());
            holder.his_post_type.setBackgroundColor(Color.parseColor(back_color));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
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
                    e.printStackTrace();
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
